package ru.netology.nmedia.utils

import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel


fun converterCountChoice(count: Long): String {
    return when {
        count >= 1_000_000 -> convertMillions(count)
        count >= 10_000    -> "${count / 1_000}K"
        count >= 1_000     -> convertThousands(count)
        else               -> count.toString()
    }
}

private fun convertThousands(count: Long): String {
    val hundreds = count / 100

    val intPart = hundreds / 10
    val fracPart = hundreds % 10

    return if (fracPart > 0) "$intPart.${fracPart}K" else "${intPart}K"
}

private fun convertMillions(count: Long): String {
    val hundredThousands = count / 100_000

    val intPart = hundredThousands / 10
    val fracPart = hundredThousands % 10

    return if (fracPart > 0) "$intPart.${fracPart}M" else "${intPart}M"
}

fun completionPost(
    post: Post,
    likeListener: (Post) -> Unit,
    shareListener: (Post) -> Unit,
    viewListener: (Post) -> Unit,
    cardPostBinding: CardPostBinding
) {
    cardPostBinding.apply {
        authorText.text = post.author
        publish.text = post.published
        contentText.text = post.content
        countLikes.text = converterCountChoice(post.countLike)
        countShare.text = converterCountChoice(post.countShare)
        countView.text = converterCountChoice(post.countViews)

        likes.setImageResource(
            if (post.likedByMe){
                R.drawable.ic_likes_clicked
            } else {
                R.drawable.ic_like_heart
            }
        )
        avatar.setImageResource(R.drawable.ic_post_avatar_drawable)
        shareIcon.setImageResource(R.drawable.ic_share)
        viewIcon.setImageResource(R.drawable.ic_view)

        this.likes.setOnClickListener {
            likeListener(post)
        }
        this.shareIcon.setOnClickListener {
            shareListener(post)
        }
        this.viewIcon.setOnClickListener {
            viewListener(post)
        }
    }
}