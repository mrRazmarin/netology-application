package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.converterCountChoice
import ru.netology.nmedia.viewmodel.PostViewModel

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

        val postViewModel by viewModels<PostViewModel>()

        postViewModel.data.observe(this) { post ->
            completionPost(post, mainBinding)

            mainBinding.likes.setImageResource(
                if (post.likedByMe){
                    R.drawable.ic_likes_clicked
                }else {
                    R.drawable.ic_like_heart
                }
            )
            mainBinding.countLikes.text = converterCountChoice(post.countLike)
            mainBinding.countShare.text = converterCountChoice(post.countShare)
            mainBinding.countView.text = converterCountChoice(post.countViews)
        }

        mainBinding.likes.setOnClickListener {
            postViewModel.like()
        }
        mainBinding.shareIcon.setOnClickListener {
            postViewModel.share()
        }
        mainBinding.viewIcon.setOnClickListener {
            postViewModel.view()
        }
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