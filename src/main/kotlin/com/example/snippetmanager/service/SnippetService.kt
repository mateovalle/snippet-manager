package com.example.snippetmanager.service

import com.example.snippetmanager.entity.Snippet
import com.example.snippetmanager.dto.CreateSnippetDTO
import com.example.snippetmanager.dto.UpdateSnippetDTO
import com.example.snippetmanager.repository.SnippetRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class SnippetService(private val snippetRepository: SnippetRepository) {
    fun createSnippet(snippetDTO: CreateSnippetDTO, userId: String): Snippet {
        val snippet = Snippet(
            userId = userId,
            name = snippetDTO.name,
            type = snippetDTO.type,
            content = snippetDTO.content
        )
        return snippetRepository.save(snippet)
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
        val updatedSnippet = snippet.copy(content = snippetDTO.content)
        return snippetRepository.save(updatedSnippet)
    }
}
