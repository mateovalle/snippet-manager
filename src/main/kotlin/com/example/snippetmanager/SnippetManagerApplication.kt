package com.example.snippetmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@RestController
class SnippetManagerApplication{
    @GetMapping("/snippets")
    fun snippets():List<Snippet>{
        return listOf(
            Snippet(1, "Snippet 1",  LocalDate.now()),
            Snippet(2, "Snippet 2",  LocalDate.now()),
            Snippet(3, "Snippet 3",  LocalDate.now()),
            Snippet(4, "Snippet 4",  LocalDate.now()),
        );
    }

}


fun main(args: Array<String>) {
    runApplication<SnippetManagerApplication>(*args)
}
