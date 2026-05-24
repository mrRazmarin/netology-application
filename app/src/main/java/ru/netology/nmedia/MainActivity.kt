package ru.netology.nmedia

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.actions.PostActions
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.converterCountChoice

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val mainBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(mainBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(mainBinding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left + v.paddingLeft,
                systemBars.top + v.paddingTop,
                systemBars.right + v.paddingRight,
                systemBars.bottom + v.paddingBottom
            )
            insets
        }

        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-професий будущего",
            published= "21 мая в 18:36",
            countLike = 990_000,
            likedByMe = false,
            countShare = 10_340_000,
            countViews = 999_999,
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен - http://netolo.gy/fyb"
        )
        completionPost(post, mainBinding)
        PostActions.actionLikes(post, mainBinding)
        PostActions.actionShare(post, mainBinding)
    }

    fun completionPost(post: Post, activityBinding: ActivityMainBinding) {
        with(activityBinding){
            authorText.text = post.author
            publish.text = post.published
            contentText.text = post.content
            countLikes.text = converterCountChoice(post.countLike)
            countShare.text = converterCountChoice(post.countShare)
            countView.text = converterCountChoice(post.countViews)
        }

        with(activityBinding){
            avatar.setImageResource(R.drawable.ic_post_avatar_drawable)
            shareIcon.setImageResource(R.drawable.ic_share)
            viewIcon.setImageResource(R.drawable.ic_view)
        }
    }
}