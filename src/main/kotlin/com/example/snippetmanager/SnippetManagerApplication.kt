package com.example.snippetmanager

import com.example.snippetmanager.entity.Snippet
import com.example.snippetmanager.dto.CreateSnippetDTO
import com.example.snippetmanager.dto.UpdateSnippetDTO
import com.example.snippetmanager.producer.SnippetCreatedProducer
import com.example.snippetmanager.service.SnippetService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import java.util.UUID

fun main(args: Array<String>) {
    runApplication<SnippetManagerController>(*args)
}

@SpringBootApplication
@RestController
@EnableJpaAuditing
class SnippetManagerController(private val snippetService: SnippetService, private val producer: SnippetCreatedProducer) {
    @GetMapping("/")
    fun index(@RequestParam("name") name: String) = "Hello, $name!. This is a health test!"

    // TODO: Get the userId from the request authentication (principal)
    @PostMapping("/snippet")
    suspend fun createSnippet(@RequestBody snippetDTO: CreateSnippetDTO, authentication: Authentication, @RequestHeader("Authorization") authorizationHeader: String): Snippet {
        val principal = authentication.principal
        if (principal !is Jwt) {
            throw Exception("No JWT")
        }
        val snippet = snippetService.createSnippet(snippetDTO, principal.subject, authorizationHeader.substring(7))
        producer.publishEvent(snippet.id.toString())
        return snippet
    }

    @GetMapping("/snippet")
    fun getSnippetsByUser(authentication: Authentication): List<Snippet> {
        return snippetService.getSnippetsByUser((authentication.principal as Jwt).subject)
    }

    @GetMapping("/snippet/{id}")
    fun getSnippetById(@PathVariable("id") id: UUID): Snippet {
        return snippetService.getSnippetById(id)
    }
    @PutMapping("/snippet/{id}")
    fun updateSnippetById(@PathVariable("id") id: UUID, @RequestBody snippetDTO: UpdateSnippetDTO): Snippet {
        return snippetService.updateSnippetById(id, snippetDTO)
    }
}