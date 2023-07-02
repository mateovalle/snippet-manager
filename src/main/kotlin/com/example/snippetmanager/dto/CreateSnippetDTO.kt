package com.example.snippetmanager.dto

import com.example.snippetmanager.entity.SnippetType

data class CreateSnippetDTO(
    val name: String,
    val type: SnippetType,
    val content: String
)
