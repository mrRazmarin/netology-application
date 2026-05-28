package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl: PostRepository {

    private var posts = listOf(
        Post(
        id = 1,
        author = "Нетология. Университет интернет-професий будущего",
        published = "21 мая в 18:36",
        countLike = 100,
        likedByMe = false,
        countShare = 100,
        countViews = 1_900,
        content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен - http://netolo.gy/fyb"
        ),
        Post(
            id = 2,
            author = "Нетология. Университет интернет-професий будущего",
            published = "18 сентября в 10:12",
            countLike = 100,
            likedByMe = false,
            countShare = 100,
            countViews = 1_900,
            content = "Знаний хватит на всех: на следующей недели разбираемся с разработкой мобильных приложений, бла бла бла бла бла бла бла бла бла бла бла бла бла бла бла..."
        )
    )

    private val data = MutableLiveData(posts)

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
}