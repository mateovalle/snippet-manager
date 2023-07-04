package com.example.snippetmanager.entity

import jakarta.persistence.*
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

enum class SnippetType {
    PRINTSCRIPT
}

enum class ComplianceStatus {
    PENDING,
    FAILED,
    NOT_COMPLIANT,
    COMPLIANT
}

@Entity
@EntityListeners(AuditingEntityListener::class)
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    val compliance: ComplianceStatus = ComplianceStatus.PENDING,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(nullable = true)
    val updatedAt: LocalDateTime? = null
)
