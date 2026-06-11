package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

private val emptyPost = Post()

class PostViewModel : ViewModel() {
    private val repository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()

    val edited = MutableLiveData(emptyPost)

    fun likeById(id: Long) = repository.likeById(id)
    @Deprecated("Не используется")
    fun shareById(id: Long) = repository.shareById(id)
    fun viewById(id: Long) = repository.viewById(id)

    @Deprecated("Не используется")
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

    fun editPostById(post: Post) {
        edited.value = post
    }
}