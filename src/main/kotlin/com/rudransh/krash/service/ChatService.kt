package com.rudransh.krash.service

import com.rudransh.krash.chat.*
import com.rudransh.krash.entity.Message
import com.rudransh.krash.repository.MessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class ChatService(private val repo: MessageRepository) : ChatServiceGrpcKt.ChatServiceCoroutineImplBase() {
    private val roomFlows = ConcurrentHashMap<String, MutableSharedFlow<ChatMessage>>()

    private fun getRoomFlow(roomId: String): MutableSharedFlow<ChatMessage> {
        return roomFlows.getOrPut(roomId) {
            MutableSharedFlow(extraBufferCapacity = 100)
        }
    }

    override suspend fun sendMessage(request: ChatMessage): Empty {
        withContext(Dispatchers.IO) {
            repo.save(Message(
                roomId = request.roomId,
                sender = request.sender,
                content = request.content,
                timestamp = System.currentTimeMillis()
            ))
        }
        println("Message from ${request.sender}: ${request.content}")

        val flow = getRoomFlow(request.roomId)
        flow.tryEmit(request)

        return Empty.getDefaultInstance()
    }

    override fun streamMessages(request: User): Flow<ChatMessage> {
        println("${request.username} joined room ${request.roomId}")

        return getRoomFlow(request.roomId)
    }
}