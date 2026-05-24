package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val published: String,
    val content: String,
    var countLike: Long = 0,
    var likedByMe: Boolean = false,
    var countShare: Long = 0,
    var countViews: Long = 0
)
