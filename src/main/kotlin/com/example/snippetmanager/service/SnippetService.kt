package com.example.snippetmanager.service

import com.example.snippetmanager.entity.Snippet
import com.example.snippetmanager.dto.SnippetDTO
import com.example.snippetmanager.repository.SnippetRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SnippetService(private val snippetRepository: SnippetRepository) {
    fun createSnippet(snippetDTO: SnippetDTO, userId: String): Snippet {
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
        return snippetRepository.findById(id).get()
    }
}
