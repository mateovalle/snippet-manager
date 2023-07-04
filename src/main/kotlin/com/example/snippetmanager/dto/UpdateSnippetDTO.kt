package com.example.snippetmanager.dto
import com.example.snippetmanager.entity.ComplianceStatus

data class UpdateSnippetDTO(
    val content: String?,
    val compliance: ComplianceStatus?
)
