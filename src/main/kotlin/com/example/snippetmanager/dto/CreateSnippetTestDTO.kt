package com.example.snippetmanager.dto

data class CreateSnippetTestDTO(
    val snippetId: String,
    val inputs: List<String>,
    val outputs: List<String>
)