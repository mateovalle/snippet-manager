package com.example.snippetmanager.producer

import kotlinx.coroutines.reactor.awaitSingle
import org.austral.ingsis.`class`.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Component
class SnippetCreatedProducer @Autowired constructor(
    @Value("\${stream.created-key}") streamKey: String,
    redis: ReactiveRedisTemplate<String, String>
) : RedisStreamProducer(streamKey, redis) {

    suspend fun publishEvent(snippetId: String) {
        println("Publishing on stream: $streamKey")
        emit(snippetId).awaitSingle()
    }
}