package com.example.snippetmanager.service

import com.example.snippetmanager.entity.Snippet
import com.example.snippetmanager.dto.CreateSnippetDTO
import com.example.snippetmanager.dto.UpdateSnippetDTO
import com.example.snippetmanager.entity.ComplianceStatus
import com.example.snippetmanager.repository.SnippetRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.configurationprocessor.json.JSONArray
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

const val baseUrl = "http://localhost:8080/"

data class Authorization(
    val userId: String,
    val type: String,
    val snippetId: String
)

@Service
class SnippetService @Autowired constructor(private val snippetRepository: SnippetRepository) {

//    private fun getMyAuthorizations(bearerToken: String): List<Authorization> {
//        val url = URL(baseUrl + "me")
//        val connection = url.openConnection() as HttpURLConnection
//        connection.requestMethod = "GET"
//        connection.setRequestProperty("Authorization", "Bearer $bearerToken")
//        val response = connection.inputStream.bufferedReader().use { it.readText() }
//        connection.disconnect()
//        val jsonResponse = JSONArray(response)
//        val content = jsonResponse.getString("content")
//        val userId = jsonResponse.getString("userId")
//        return jsonResponse. { auth -> Authorization(auth.getString("userId"), auth.getString("type"), auth.getString("snippetId")) }
//    }

    private fun createAuthorization(bearerToken: String, userId: String, authorizationType: String, snippetId: String) {
        val url = URL(baseUrl + "new")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Authorization", "Bearer $bearerToken")
        connection.setRequestProperty("Content-Type", "application/json; utf-8")
        val jsonInputString = "{\"userId\": \"$userId\", \"type\": \"$authorizationType\", \"snippetId\": \"$snippetId\"}"

        connection.outputStream.use { os ->
            val input = jsonInputString.toByteArray(charset("utf-8"))
            os.write(input, 0, input.size)
        }

        val response = connection.inputStream.bufferedReader().use { it.readText() }
        connection.disconnect()
    }
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
            createAuthorization(bearerToken, userId, "WRITE", savedSnippet.id.toString())
        } catch (e: Exception) {
            snippetRepository.delete(snippet)
            throw e;
        }
        return savedSnippet
    }
    fun getSnippetsByUser(userId: String): List<Snippet> {
        return snippetRepository.findByUserId(userId)
    }
    fun getSnippetById(id: UUID): Snippet {
        return snippetRepository.findById(id).orElseThrow {
            throw Exception("Snippet not found")
        }
    }
    @Transactional
    fun updateSnippetById(id: UUID, snippetDTO: UpdateSnippetDTO): Snippet {
        val snippet = snippetRepository.findById(id).orElseThrow {
            throw Exception("Snippet not found")
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

}
