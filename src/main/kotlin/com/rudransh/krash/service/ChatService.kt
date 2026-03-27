package com.rudransh.krash.service

import com.rudransh.krash.chat.*
import com.rudransh.krash.entity.Message
import com.rudransh.krash.repository.MessageRepository
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

@Service
class ChatService(private val repo: MessageRepository) : ChatServiceGrpcKt.ChatServiceCoroutineImplBase() {
    private val roomFlows = ConcurrentHashMap<String, MutableSharedFlow<ChatMessage>>()

    private fun getRoomFlow(roomId: String): MutableSharedFlow<ChatMessage> {
        return roomFlows.getOrPut(roomId) {
            MutableSharedFlow(extraBufferCapacity = 100)
        }
    }

//    override suspend fun sendMessage(request: ChatMessage): Empty {
//        repo.save(
//            Message(
//                sender = request.sender,
//                content = request.content,
//                timestamp = request.timestamp,
//            )
//        )
//        println("Message from ${request.sender}: ${request.content}")
//
//        clients.forEach { client -> try {
//                client.trySend(request)
//            } catch(e: Exception) {
//                println("Failed to send message to client ${e}")
//            }
//        }
//
//        return Empty.getDefaultInstance()
//    }

    override suspend fun sendMessage(request: ChatMessage): Empty {
        println("Room ${request.roomId} | ${request.sender}: ${request.content}")

        val flow = getRoomFlow(request.roomId)
        flow.tryEmit(request)

        return Empty.getDefaultInstance()
    }

    override fun streamMessages(request: User): Flow<ChatMessage> {
        println("${request.username} joined room ${request.roomId}")

        return getRoomFlow(request.roomId)
    }
}