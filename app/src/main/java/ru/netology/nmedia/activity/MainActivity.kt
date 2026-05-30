package ru.netology.nmedia.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.adapter.PostListener
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val mainBinding = ActivityMainBinding.inflate(layoutInflater)

        val originalPadding = intArrayOf(
            mainBinding.main.paddingLeft,
            mainBinding.main.paddingTop,
            mainBinding.main.paddingRight,
            mainBinding.main.paddingBottom
        )

        setContentView(mainBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(mainBinding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                originalPadding[0] + systemBars.left,
                originalPadding[1] + systemBars.top,
                originalPadding[2] + systemBars.right,
                originalPadding[3] + systemBars.bottom
            )
            WindowInsetsCompat.CONSUMED
        }

        val postViewModel: PostViewModel by viewModels()
        val adapter = PostAdapter (object : PostListener{
            override fun onLike(post: Post) {
                postViewModel.likeById(post.id)
            }

            override fun onShare(post: Post) {
                postViewModel.shareById(post.id)
            }

            override fun onView(post: Post) {
                postViewModel.viewById(post.id)
            }

            override fun onRemove(post: Post) {
                postViewModel.removePostById(post.id)
            }

            override fun onEdit(post: Post) {
                postViewModel.editPostById(post)
            }

        })
        mainBinding.list.adapter = adapter

        postViewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        postViewModel.edited.observe(this) { edited ->
            if (edited.id != 0L) {
                mainBinding.inputContent.setText(edited.content)
                AndroidUtils.showKeyboard(mainBinding.inputContent)
            }
        }

        mainBinding.addButton.setOnClickListener {
            val content = mainBinding.inputContent.text?.toString()

            if (content.isNullOrBlank()){
                Toast.makeText(
                    this,
                    getText(R.string.error_empty_text),
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            postViewModel.save(content)
            mainBinding.inputContent.clearFocus()
            mainBinding.inputContent.setText("")

            AndroidUtils.hideKeyboard(mainBinding.inputContent)
        }
    }
}