package ru.netology.nmedia.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.AndroidUtils.dateTimeNow

@Entity(tableName = "h_posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val published: String = dateTimeNow(),
    val content: String,
    @ColumnInfo(name = "likes")
    val countLike: Long,
    val likedByMe: Boolean,
    val countShare: Long,
    val countViews: Long,
    val video: String = ""
) {
    fun toDto(): Post = Post(
        id = id,
        author = author,
        published = published,
        content = content,
        countLike = countLike,
        likedByMe = likedByMe,
        countShare = countShare,
        countViews = countViews,
        video = video
    )

    companion object {
        fun toEntity(dto: Post): PostEntity = with(dto) {
            PostEntity(
                id = id,
                author = author,
                published = published,
                content = content,
                countLike = countLike,
                likedByMe = likedByMe,
                countShare = countShare,
                countViews = countViews,
                video = video
            )
        }
    }
}