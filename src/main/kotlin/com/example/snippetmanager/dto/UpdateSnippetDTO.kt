package com.example.snippetmanager.dto
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateSnippetDTO @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
    @JsonProperty("content") val content: String
)
