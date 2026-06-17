package ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.FragmentCardPostBinding
import ru.netology.nmedia.utils.AndroidUtils.applySystemBarsPadding
import ru.netology.nmedia.utils.AndroidUtils.createPostListener
import ru.netology.nmedia.utils.AndroidUtils.setupSingleObserve
import ru.netology.nmedia.viewmodel.PostViewModel

class CardPost : Fragment() {

    private val postViewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCardPostBinding.inflate(inflater, container, false)

        val adapter = PostAdapter(createPostListener(postViewModel))
        binding.cardPostMain.applySystemBarsPadding()

        binding.postCard.list.adapter = adapter

        // Скрываем кнопку "Добавить"
        binding.postCard.add.visibility = View.GONE

        setupSingleObserve(
            postViewModel,
            adapter
        )

        return binding.root
    }



}