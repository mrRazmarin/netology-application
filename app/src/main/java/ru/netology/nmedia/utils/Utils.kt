package ru.netology.nmedia.utils

import android.content.Context
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.PopupMenu
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostListener
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object AndroidUtils {
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

    fun dateTimeNow(): String {
        val formatter = SimpleDateFormat("dd MMMM 'в' HH:mm", Locale("ru", "RU"))
        return formatter.format(Date())
    }

    fun completionPost(
        post: Post,
        listener: PostListener,
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

            burgerMenu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_menu)

                    setOnMenuItemClickListener { item ->
                        when(item.itemId) {
                            R.id.remove -> {
                                listener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                listener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            this.likes.setOnClickListener {
                listener.onLike(post)
            }
            this.shareIcon.setOnClickListener {
                listener.onShare(post)
            }
            this.viewIcon.setOnClickListener {
                listener.onView(post)
            }
        }
    }

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(view: View) {
        view.requestFocus()
        if (view.hasWindowFocus()) {
            showKeyboardNow(view)
        } else {
            view.viewTreeObserver.addOnWindowFocusChangeListener(object : ViewTreeObserver.OnWindowFocusChangeListener{
                override fun onWindowFocusChanged(hasFocus: Boolean) {
                    if (hasFocus) {
                        showKeyboardNow(view)
                        view.viewTreeObserver.removeOnWindowFocusChangeListener(this)
                    }
                }
            })
        }
    }

    private fun showKeyboardNow(view: View) {
        if (!view.isFocused) return

        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}