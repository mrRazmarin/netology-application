package ru.netology.nmedia.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.PopupMenu
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.adapter.PostListener
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object AndroidUtils {
    fun converterCountChoice(count: Long): String {
        return when {
            count >= 1_000_000 -> convertMillions(count)
            count >= 10_000 -> "${count / 1_000}K"
            count >= 1_000 -> convertThousands(count)
            else -> count.toString()
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

            likes.isChecked = post.likedByMe
            likes.text = converterCountChoice(post.countLike)

            shareIcon.text = converterCountChoice(post.countShare)

            viewIcon.text = converterCountChoice(post.countViews)

            avatar.setImageResource(R.drawable.ic_post_avatar_drawable)

            burgerMenu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_menu)

                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.edit -> {
                                listener.onEdit(post)
                                true
                            }
                            R.id.remove -> {
                                listener.onRemove(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            if (!post.video.isBlank()){
                this.preview.visibility = View.VISIBLE
            } else {
                this.preview.visibility = View.GONE
            }

            this.headerTitle.setOnClickListener {
                listener.onPostClick(post)
            }

            this.textAndVideo.setOnClickListener {
                listener.onPostClick(post)
            }

            this.preview.setOnClickListener {
                listener.onVideo(post)
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
            view.viewTreeObserver.addOnWindowFocusChangeListener(object :
                ViewTreeObserver.OnWindowFocusChangeListener {
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

    fun View.applySystemBarsPadding() {
        val originalPaddingLeft = paddingLeft
        val originalPaddingTop = paddingTop
        val originalPaddingRight = paddingRight
        val originalPaddingBottom = paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                originalPaddingLeft + systemBars.left,
                originalPaddingTop + systemBars.top,
                originalPaddingRight + systemBars.right,
                originalPaddingBottom + systemBars.bottom
            )
            WindowInsetsCompat.CONSUMED
        }
    }

    fun Fragment.createPostListener(postViewModel: PostViewModel): PostListener {
        return object : PostListener {
            override fun onLike(post: Post) {
                postViewModel.likeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "type/plain"
                    putExtra(Intent.EXTRA_TEXT, post.content)
                }

                val chooser = Intent.createChooser(intent, getString(R.string.chooser_share_post))

                startActivity(chooser)
            }

            override fun onView(post: Post) {
                postViewModel.viewById(post.id)
            }

            override fun onRemove(post: Post) {
                postViewModel.removePostById(post.id)
            }

            override fun onEdit(post: Post) {
                postViewModel.editPostById(post)

                // Проверка на каком мы сейчас экране, и в зависимости от экрана выполнять переход
                val navController = findNavController()
                when (navController.currentDestination?.id) {
                    R.id.cardPost ->
                        navController.navigate(R.id.action_cardPost_to_editPostFragment)
                    else -> navController.navigate(R.id.action_feedFragment_to_editPostFragment)
                }
            }

            override fun onPostClick(post: Post) {
                val navController = findNavController()
                // Защита от дальнейшего перехода, если уже "провалились" в Post
                when(navController.currentDestination?.id) {
                    R.id.feedFragment -> {
                        postViewModel.loadPost(post.id)
                        navController.navigate(R.id.action_feedFragment_to_cardPost)
                    }
                }
            }

            override fun onVideo(post: Post) {
                val url = post.video
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    url.trim().toUri()
                )
                if (intent.resolveActivity(requireContext().packageManager) != null){
                    Log.e("Check exists browser", "Browser exist!")
                    startActivity(intent)
                } else {
                    Log.e("Check exists browser", "Browser not found!")
                    Log.i("Field post.video", url)
                }
            }
        }
    }

    fun Fragment.setupObserve(
        viewModel: PostViewModel,
        adapter: PostAdapter
    ) {
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }
    }

    fun Fragment.setupSingleObserve(
        viewModel: PostViewModel,
        adapter: PostAdapter
    ) {
        viewModel.loaded.observe(viewLifecycleOwner) {
            post ->
            if (post.id == 0L) {
                findNavController().navigateUp()
            }else {
                adapter.submitList(listOf(post))
            }
        }
    }
}