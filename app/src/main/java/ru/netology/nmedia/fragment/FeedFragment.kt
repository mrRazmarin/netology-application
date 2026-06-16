package ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.utils.AndroidUtils.applySystemBarsPadding
import ru.netology.nmedia.utils.AndroidUtils.createPostListener
import ru.netology.nmedia.utils.AndroidUtils.setupObserve
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {
    private val postViewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

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
}