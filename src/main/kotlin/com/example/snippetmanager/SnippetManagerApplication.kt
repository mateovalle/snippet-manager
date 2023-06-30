package com.example.snippetmanager

import com.example.snippetmanager.entity.Snippet
import com.example.snippetmanager.dto.SnippetDTO
import com.example.snippetmanager.service.SnippetService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.*
import java.util.UUID

fun main(args: Array<String>) {
    runApplication<SnippetManagerController>(*args)
}

@SpringBootApplication
@RestController
@CrossOrigin(origins = ["http://localhost:3000"])
class SnippetManagerController(private val snippetService: SnippetService) {
    @GetMapping("/")
    fun index(@RequestParam("name") name: String) = "Hello, $name!. This is a health test!"

    // TODO: Get the userId from the request authentication (principal)
    @PostMapping("/snippet")
    fun createSnippet(@RequestBody snippetDTO: SnippetDTO): Snippet {
        return snippetService.createSnippet(snippetDTO, "testId")
    }

    @GetMapping("/snippet")
    fun getSnippetsByUser(): List<Snippet> {
        return snippetService.getSnippetsByUser("testId")
    }

    @GetMapping("/snippet/{id}")
    fun getSnippetById(@PathVariable("id") id: UUID): Snippet {
        return snippetService.getSnippetById(id)
    }
}