package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.AndroidUtils.dateTimeNow

class PostRepositoryFileImpl(private val context: Context): PostRepository {
    private var nextId = 1L
    private var posts = emptyList<Post>()
        set(value) {
            field = value
            sync()
        }
    private val data = MutableLiveData(posts)

    init {
        val file = context.filesDir.resolve(FILE_NAME)

        if (file.exists()) {
            context.openFileInput(FILE_NAME).bufferedReader().use { str ->
                posts = gson.fromJson(str, typeToken)
                nextId = (posts.maxOfOrNull { it.id } ?: 0) + 1
                data.value = posts
            }
        }
    }

    private fun sync() {
        context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(posts))
        }
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id){
                it
            } else {
                it.copy(likedByMe = !it.likedByMe, countLike = if (it.likedByMe) {
                    it.countLike -1
                }else{
                    it.countLike + 1
                })
            }
        }

        data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id){
                it
            } else {
                it.copy(countShare = it.countShare + 1)
            }
        }
        data.value = posts
    }

    override fun viewById(id: Long) {

    }

    override fun removePostById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }

    override fun save(post: Post) {
        if (post.id == 0L){
            posts = listOf(post.copy(
                id = nextId++,
                author = "Mikhail Salnikov",
                countLike = 0,
                likedByMe = false,
                countShare = 0,
                countViews = 0,
                published = dateTimeNow()
            )) + posts
        } else {
            posts = posts.map {
                if (it.id == post.id) {
                    it.copy(content = post.content)
                } else {
                    it
                }
            }
        }
        data.value = posts
    }

    companion object {
        private const val FILE_NAME = "posts.json"

        private val gson = Gson()
        private val typeToken = TypeToken.getParameterized(List::class.java, Post::class.java).type
    }
}