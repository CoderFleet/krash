package com.rudransh.krash.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class Message (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    val id: Long = 0,
    val sender: String,
    val content: String,
    val timestamp: Long
)