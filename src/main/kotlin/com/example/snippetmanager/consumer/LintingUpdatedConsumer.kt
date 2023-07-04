package com.example.snippetmanager.consumer

import com.example.snippetmanager.entity.ComplianceStatus
import com.example.snippetmanager.producer.SnippetCreatedProducer
import com.example.snippetmanager.service.SnippetService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.austral.ingsis.`class`.redis.RedisStreamConsumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class LintingUpdatedConsumer @Autowired constructor(
    redis: ReactiveRedisTemplate<String, String>,
    @Value("\${stream.updated-key}") streamKey: String,
    @Value("\${groups.product}") groupId: String,
    private val producer: SnippetCreatedProducer,
    private val snippetService: SnippetService
) : RedisStreamConsumer<String>(streamKey, groupId, redis) {

    override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>> {
        return StreamReceiver.StreamReceiverOptions.builder()
            .pollTimeout(Duration.ofMillis(10000)) // Set poll rate
            .targetType(String::class.java) // Set type to de-serialize record
            .build();
    }

    override fun onMessage(record: ObjectRecord<String, String>) {
        // What we want to do with the stream
        println("Id: ${record.id}, Value: ${record.value}, Stream: ${record.stream}, Group: ${groupId}")
        val snippets = snippetService.getSnippetsByUser(record.value)
        snippetService.updateManyCompliance(snippets, ComplianceStatus.PENDING)
        snippets.forEach { snippet ->
            GlobalScope.launch {
                producer.publishEvent(snippet.id.toString())
            }
        }
    }

}