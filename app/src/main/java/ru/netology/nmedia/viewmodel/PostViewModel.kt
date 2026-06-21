package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepositorySQLiteImpl

private val emptyPost = Post()

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PostRepositorySQLiteImpl(
        AppDb.getInstance(application).postDao
    )

    val data = repository.getAll()

    val edited = MutableLiveData(emptyPost)
    private val _postId = MutableLiveData(0L)
    val loaded: LiveData<Post> = _postId.switchMap { id ->
        if (id == 0L) {
            MutableLiveData(emptyPost)
        } else {
            data.map { posts ->
                posts.find { it.id == id } ?: emptyPost
            }
        }
    }

    fun likeById(id: Long) = repository.likeById(id)

    fun shareById(id: Long) = repository.shareById(id)
    fun viewById(id: Long) = repository.viewById(id)
    fun setEmptyPost() {
        edited.value = emptyPost
    }
    fun removePostById(id: Long) = repository.removePostById(id)

    fun save(content: String) {
        edited.value?.let { post ->
            val trimmed = content.trim()
            if (trimmed != post.content) {
                if (!trimmed.isBlank()) {
                    repository.save(
                        post.copy(content = content)
                    )
                }
            }

            edited.value = emptyPost
        }
    }

    fun loadPost(id: Long) {
        _postId.value = id
    }

    fun editPostById(post: Post) {
        edited.value = post
    }
}