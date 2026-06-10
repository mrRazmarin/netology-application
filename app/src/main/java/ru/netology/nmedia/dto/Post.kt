package ru.netology.nmedia.dto

import ru.netology.nmedia.utils.AndroidUtils.dateTimeNow


data class Post(
    val id: Long = 0L,
    val author: String = "",
    val published: String = dateTimeNow(),
    val content: String = "",
    val countLike: Long = 0,
    val likedByMe: Boolean = false,
    val countShare: Long = 0,
    val countViews: Long = 0,
    val video: String = ""
)
