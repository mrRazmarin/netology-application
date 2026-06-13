package ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentEditPostBinding
import ru.netology.nmedia.utils.AndroidUtils.applySystemBarsPadding
import ru.netology.nmedia.viewmodel.PostViewModel

class EditPostFragment : Fragment() {

    private val postViewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEditPostBinding.inflate(inflater, container, false)
        binding.main.applySystemBarsPadding()
        var isOpen = false

        postViewModel.edited.observe(viewLifecycleOwner) { post ->
            if (post.id != 0L) {
                binding.edit.setText(post.content)
                binding.edit.setSelection(post.content.length)
            }
        }

        binding.burgerMenu.setOnClickListener {
            isOpen = !isOpen

            if (isOpen) {
                binding.save.visibility = View.VISIBLE
                binding.cancel.visibility = View.VISIBLE
            }else{
                binding.save.visibility = View.GONE
                binding.cancel.visibility = View.GONE
            }
        }

        binding.save.setOnClickListener {
            val text = binding.edit.text.toString()

            if (!text.isBlank()){
                postViewModel.save(text)
            }
            findNavController().navigateUp()
        }

        binding.cancel.setOnClickListener {
            postViewModel.setEmptyPost()
            findNavController().navigateUp()
        }

        return binding.root
    }
}
