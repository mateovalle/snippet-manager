package com.example.snippetmanager.repository

import com.example.snippetmanager.entity.TestCase
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface TestCaseRepository: JpaRepository<TestCase, UUID> {
    fun findBySnippetId(snippetId: UUID): Optional<TestCase>
}