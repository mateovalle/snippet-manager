package com.example.snippetmanager.dto

import com.example.snippetmanager.entity.ComplianceStatus
import com.example.snippetmanager.entity.SnippetType
import java.time.LocalDateTime

data class GetSnippetDTO(
    val id: String,
    val userId: String,
    val name: String,
    val type: SnippetType,
    val content: String,
    val compliance: ComplianceStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val access: String
)