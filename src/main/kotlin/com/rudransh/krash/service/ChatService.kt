package com.rudransh.krash.service

import com.rudransh.krash.chat.*
import com.rudransh.krash.entity.Message
import com.rudransh.krash.repository.MessageRepository
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import org.springframework.stereotype.Service
import java.util.concurrent.CopyOnWriteArrayList

@Service
class ChatService(private val repo: MessageRepository) : ChatServiceGrpcKt.ChatServiceCoroutineImplBase() {
    private val clients = CopyOnWriteArrayList<SendChannel<ChatMessage>>()

    override suspend fun sendMessage(request: ChatMessage): Empty {
        repo.save(
            Message(
                sender = request.sender,
                content = request.content,
                timestamp = request.timestamp,
            )
        )
        println("Message from ${request.sender}: ${request.content}")

        clients.forEach { client -> try {
                client.trySend(request)
            } catch(e: Exception) {
                println("Failed to send message to client ${e}")
            }
        }

        return Empty.getDefaultInstance()
    }

    override fun streamMessages(request: User): Flow<ChatMessage> = channelFlow {
        println("${request.username} connected")

        clients.add(channel)

        awaitClose {
            println("${request.username} disconnected")
            clients.remove(channel)
        }
    }.buffer()
}