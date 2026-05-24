package ru.netology.nmedia.actions

import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.converterCountChoice

object PostActions {
    fun actionLikes(post: Post, activityBinding: ActivityMainBinding) {
        with(activityBinding){
            if (!post.likedByMe)
                likes.setImageResource(R.drawable.ic_like_heart)
            else
                likes.setImageResource(R.drawable.ic_likes_clicked)

            likes.setOnClickListener {
                if (post.likedByMe) post.countLike-- else post.countLike++
                post.likedByMe = !post.likedByMe
                likes.setImageResource(
                    if (post.likedByMe)
                        R.drawable.ic_likes_clicked
                    else
                        R.drawable.ic_like_heart
                )
                countLikes.text = converterCountChoice(post.countLike)
            }
        }
    }

    fun actionShare(post: Post, activityBinding: ActivityMainBinding) {
        with(activityBinding) {
            shareIcon.setOnClickListener {
                post.countShare++
                countShare.text = converterCountChoice(post.countShare)
            }
        }
    }

    fun actionViews(post: Post, activityBinding: ActivityMainBinding) {
        with(activityBinding) {
            shareIcon.setOnClickListener {
                countShare.text = converterCountChoice(post.countShare)
            }
        }
    }
}