package com.rudransh.krash.client

import com.rudransh.krash.chat.*
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

fun main() = runBlocking {
    val username = print("Enter username: ").let { readln() }

    val channel = ManagedChannelBuilder
        .forAddress("localhost", 9090)
        .usePlaintext()
        .build()

    val stub = ChatServiceGrpcKt.ChatServiceCoroutineStub(channel)

    launch {
        try {
            stub.streamMessages(
                User.newBuilder().setUsername(username).build()
            ).collect {
                println("\n${it.sender}: ${it.content}")
            }
        } catch (e: Exception) {
            println("Disconnected")
        }
    }

    while (true) {
        val msg = readln()

        stub.sendMessage(
            ChatMessage.newBuilder()
                .setSender(username)
                .setContent(msg)
                .setTimestamp(System.currentTimeMillis())
                .build()
        )
    }
}