package com.example.snippetmanager.service

import com.example.snippetmanager.entity.Snippet
import com.example.snippetmanager.dto.CreateSnippetDTO
import com.example.snippetmanager.dto.CreateSnippetTestDTO
import com.example.snippetmanager.dto.GetSnippetDTO
import com.example.snippetmanager.dto.UpdateSnippetDTO
import com.example.snippetmanager.entity.ComplianceStatus
import com.example.snippetmanager.entity.TestCase
import com.example.snippetmanager.repository.SnippetRepository
import com.example.snippetmanager.repository.TestCaseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.UUID

@ResponseStatus(HttpStatus.FORBIDDEN)
class ForbiddenException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException(message: String) : RuntimeException(message)

@Service
class SnippetService @Autowired constructor(private val snippetRepository: SnippetRepository, private val authorizationHTTPService: AuthorizationHTTPService, private val testCaseRepository: TestCaseRepository) {

    fun createSnippet(snippetDTO: CreateSnippetDTO, userId: String, bearerToken: String): Snippet {
        val snippet = Snippet(
            userId = userId,
            name = snippetDTO.name,
            type = snippetDTO.type,
            content = snippetDTO.content,
            compliance = ComplianceStatus.PENDING
        )
        val savedSnippet = snippetRepository.save(snippet)
        try {
            authorizationHTTPService.createAuthorization(bearerToken, userId, "WRITE", savedSnippet.id.toString())
        } catch (e: Exception) {
            snippetRepository.delete(snippet)
            throw e;
        }
        return savedSnippet
    }

    fun createSnippetTest(userId: String, snippetTestDTO: CreateSnippetTestDTO): TestCase {
        val snippet = snippetRepository.findById(UUID.fromString(snippetTestDTO.snippetId)).orElseThrow { NotFoundException("Snippet not found") }
        if (snippet.userId != userId) {
            throw ForbiddenException("User doesn't own the snippet")
        }
        val testCase = TestCase(
            snippet = snippet,
            inputs = snippetTestDTO.inputs,
            outputs = snippetTestDTO.outputs,
        )
        return testCaseRepository.save(testCase)
    }

    fun getSnippetsByUser(userId: String, bearerToken: String): List<GetSnippetDTO> {
        val authorizations = authorizationHTTPService.getMyAuthorizations(bearerToken)
        val snippetIds = authorizations.map { UUID.fromString(it.snippetId) }
        val snippets = snippetRepository.getSnippetsByIdIn(snippetIds)
        return snippets.map { snippet ->
            val auth = authorizations.find { it.snippetId == snippet.id.toString() }
            GetSnippetDTO(snippet.id.toString(), snippet.userId, snippet.name, snippet.type, snippet.content, snippet.compliance, snippet.createdAt, snippet.updatedAt, auth?.access ?: "NONE")
        }
    }

    fun getOwnedSnippets(userId: String): List<Snippet> {
        return snippetRepository.findByUserId(userId)
    }

    fun getSnippetById(id: UUID, userId: String, bearerToken: String): Snippet {
        // If the user is not a client, check if they are authorized to write
        if (!userId.contains("client")) {
            // If the user is not authorized to write, check if they are authorized to read
            var isAuthorized = authorizationHTTPService.isAuthorized(bearerToken, userId, "WRITE", id.toString());
            if (!isAuthorized) isAuthorized = authorizationHTTPService.isAuthorized(bearerToken, userId, "READ", id.toString());
            if (!isAuthorized) {
                throw ForbiddenException("User not authorized to read snippet.")
            }
        }
        return snippetRepository.findById(id).orElseThrow {
            throw NotFoundException("Snippet not found")
        }
    }

    @Transactional
    fun updateSnippetById(id: UUID, snippetDTO: UpdateSnippetDTO, userId: String, bearerToken: String): Snippet {
        val isAuthorized = if (userId.contains("client")) true else authorizationHTTPService.isAuthorized(bearerToken, userId, "WRITE", id.toString())
        if (!isAuthorized) {
            throw ForbiddenException("User not authorized to update snippet.")
        }
        val snippet = snippetRepository.findById(id).orElseThrow {
            throw NotFoundException("Snippet not found")
        }
        val updatedSnippet = snippet.copy(content = snippetDTO.content ?: snippet.content, compliance = snippetDTO.compliance ?: snippet.compliance)
        return snippetRepository.save(updatedSnippet)
    }

    fun updateManyCompliance(snippets: List<Snippet>, compliance: ComplianceStatus): List<Snippet> {
        val updatedSnippets = snippets.map { snippet ->
            snippet.copy(compliance = compliance)
        }
        return snippetRepository.saveAll(updatedSnippets)
    }

    fun getSnippetOwner(id: UUID): String {
        val snippet = snippetRepository.findById(id).orElseThrow {
            throw NotFoundException("Snippet not found")
        }
        return snippet.userId
    }

    fun getSnippetTestById(id: UUID): TestCase {
        return testCaseRepository.findBySnippetId(id).orElseThrow {
            throw NotFoundException("Snippet test not found")
        }
    }

}
