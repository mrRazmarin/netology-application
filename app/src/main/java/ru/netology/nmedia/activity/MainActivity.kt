package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.adapter.PostListener
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.AndroidUtils.applySystemBarsPadding
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    private val postViewModel: PostViewModel by viewModels()
    private val editContract = registerForActivityResult(EditPostContract) { editResult ->
        postViewModel.save(editResult)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val postContract = registerForActivityResult(NewPostContract) { result ->
            postViewModel.save(result)
        }

        val adapter = PostAdapter(createPostListener(postViewModel))

        setContentView(mainBinding.root)
        mainBinding.main.applySystemBarsPadding()

        mainBinding.list.adapter = adapter

        mainBinding.add.setOnClickListener {
            postContract.launch()
        }

        setupObserve(
            viewModel = postViewModel,
            binding = mainBinding,
            adapter = adapter
        )
    }

    private fun setupObserve(
        viewModel: PostViewModel,
        binding: ActivityMainBinding,
        adapter: PostAdapter
    ) {
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
    }

    private fun createPostListener(postViewModel: PostViewModel): PostListener {
        return object : PostListener {
            override fun onLike(post: Post) {
                postViewModel.likeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    intent.type = "type/plain"
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
                val postContent = post.content

                editContract.launch(postContent)
            }
        }
    }
}