package com.example.snippetmanager.entity

import jakarta.persistence.*
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@Entity
@EntityListeners(AuditingEntityListener::class)
data class TestCase(
    @Id @GeneratedValue
    val id: UUID? = null,

    @OneToOne
    @JoinColumn(name = "snippet_id", nullable = false, foreignKey = ForeignKey(name = "fk_test_case_snippet_id"))
    val snippet: Snippet = Snippet(),

    @ElementCollection(fetch = FetchType.EAGER)
    val inputs: List<String> = listOf(),

    @ElementCollection(fetch = FetchType.EAGER)
    val outputs: List<String> = listOf(),

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(nullable = true)
    val updatedAt: LocalDateTime? = null
)
