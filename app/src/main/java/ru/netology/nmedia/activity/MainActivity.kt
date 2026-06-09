package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.View
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
        val postViewModel: PostViewModel by viewModels()
        val adapter = PostAdapter(createPostListener(postViewModel))

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

        mainBinding.list.adapter = adapter

        setupObserve(
            viewModel = postViewModel,
            binding = mainBinding,
            adapter = adapter
        )

        setupClickListeners(
            binding = mainBinding,
            viewModel = postViewModel
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

        viewModel.edited.observe(this) { edited ->
            if (edited.id != 0L) {
                binding.inputContent.setText(edited.content)
                binding.showEditMode()
                AndroidUtils.showKeyboard(binding.inputContent)
            }
        }
    }

    private fun createPostListener(postViewModel: PostViewModel): PostListener  {
        return object : PostListener {
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

        }
    }

    private fun ActivityMainBinding.clearInput() {
        inputContent.clearFocus()
        inputContent.setText("")
    }

    private fun ActivityMainBinding.showCreateMode() {
        editsGroup.visibility = View.GONE
        addButton.visibility = View.VISIBLE
    }

    private fun ActivityMainBinding.showEditMode() {
        editsGroup.visibility = View.VISIBLE
        addButton.visibility = View.GONE
    }

    private fun savePost(
        binding: ActivityMainBinding,
        viewModel: PostViewModel
    ) {
        val content = binding.inputContent.text?.toString()
        if (content.isNullOrBlank()) {
            Toast.makeText(
                this,
                getText(R.string.error_empty_text),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        viewModel.save(content)
        AndroidUtils.hideKeyboard(binding.inputContent)
    }

    private fun setupClickListeners(
        binding: ActivityMainBinding,
        viewModel: PostViewModel
    ) {
        binding.btnSave.setOnClickListener {
            savePost(
                binding,
                viewModel
            )

            binding.clearInput()
            binding.showCreateMode()
            AndroidUtils.hideKeyboard(binding.inputContent)
        }

        binding.btnCancel.setOnClickListener {
            viewModel.setEmptyPost()

            binding.clearInput()
            binding.showCreateMode()
            AndroidUtils.hideKeyboard(binding.inputContent)
        }

        binding.addButton.setOnClickListener {
            savePost(
                binding,
                viewModel
            )

            binding.clearInput()
            AndroidUtils.hideKeyboard(binding.inputContent)
        }
    }
}