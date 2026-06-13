package ru.netology.nmedia.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.adapter.PostListener
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.AndroidUtils.applySystemBarsPadding
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {
    private val postViewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    val packageManager: PackageManager by lazy { requireActivity().packageManager }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bindingView = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostAdapter(createPostListener(postViewModel))
        bindingView.main.applySystemBarsPadding()

        bindingView.list.adapter = adapter

        setupObserve(
            viewModel = postViewModel,
            adapter = adapter
        )

        bindingView.add.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        return bindingView.root
    }

    private fun setupObserve(
        viewModel: PostViewModel,
        adapter: PostAdapter
    ) {
        viewModel.data.observe(viewLifecycleOwner) { posts ->
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
                findNavController().navigate(R.id.action_feedFragment_to_editPostFragment)
            }

            override fun onVideo(post: Post) {
                val url = post.video
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    url.trim().toUri()
                )
                if (intent.resolveActivity(packageManager) != null){
                    Log.e("Check exists browser", "Browser exist!")
                    startActivity(intent)
                } else {
                    Log.e("Check exists browser", "Browser not found!")
                    Log.i("Field post.video", url)
                }
            }
        }
    }
}