package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val published: String,
    val content: String,
    val countLike: Long = 0,
    val likedByMe: Boolean = false,
    val countShare: Long = 0,
    val countViews: Long = 0
)
