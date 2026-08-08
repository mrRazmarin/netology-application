package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM h_posts ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>
    @Upsert
    fun save(post: PostEntity)
    @Query("""
        UPDATE h_posts SET
           likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
           likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id;
    """)
    fun likeById(id: Long)
    @Query("""
        UPDATE h_posts SET
        countShare = countShare + 1
        WHERE id = :id;
    """)
    fun shareById(id: Long)
    @Query("""
        DELETE FROM h_posts WHERE id = :id
    """)
    fun removeById(id: Long)
}