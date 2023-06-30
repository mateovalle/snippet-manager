package com.example.snippetmanager.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*
import com.example.snippetmanager.entity.Snippet

@Repository
interface SnippetRepository : JpaRepository<Snippet, UUID> {
    fun findByUserId(userId: String): List<Snippet>
}
