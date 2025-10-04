package com.example.rememberme

data class RememberList(
    val id: Long,
    val name: String,
    val items: List<String>,
    val createdAt: String
)
