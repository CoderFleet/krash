package com.rudransh.krash.service

import com.rudransh.krash.chat.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import org.springframework.stereotype.Service
import java.util.concurrent.CopyOnWriteArrayList

@Service
class ChatService : ChatServiceGrpcKt.ChatServiceCoroutineImplBase() {
    private val clients = CopyOnWriteArrayList<SendChannel<ChatMessage>>()

    override suspend fun sendMessage(request: ChatMessage): Empty {
        println("Message from ${request.sender}: ${request.content}")

        clients.forEach { client -> try {
                client.trySend(request)
            } catch(e: Exception) {
                println("Failed to send message to client")
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