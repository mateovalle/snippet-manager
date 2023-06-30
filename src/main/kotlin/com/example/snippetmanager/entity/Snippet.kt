package com.example.snippetmanager.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

enum class SnippetType {
    PRINTSCRIPT
}

@Entity
data class Snippet(
    @Id @GeneratedValue
    val id: UUID? = null,

    @Column(nullable = false)
    val userId: String = "",

    @Column(nullable = false)
    val name: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: SnippetType = SnippetType.PRINTSCRIPT,

    @Column(nullable = false)
    val content: String = "",

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
