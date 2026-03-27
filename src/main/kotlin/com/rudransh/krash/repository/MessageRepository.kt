package com.rudransh.krash.repository

import com.rudransh.krash.entity.Message
import org.springframework.data.jpa.repository.JpaRepository

interface MessageRepository : JpaRepository<Message, Long> {
    fun sender(sender: String): MutableList<Message>
}