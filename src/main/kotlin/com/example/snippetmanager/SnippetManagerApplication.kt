package com.example.snippetmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class SnippetManagerApplication

fun main(args: Array<String>) {
    runApplication<SnippetManagerApplication>(*args)
}

@RestController
class AuthorizationController {
    @GetMapping("/")
    fun index(@RequestParam("name") name: String) = "Hello, $name!"
}