package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.activity.EditPostActivity.Companion.KEY_EDIT_TEXT
import ru.netology.nmedia.databinding.ActivityEditPostBinding
import ru.netology.nmedia.utils.AndroidUtils.applySystemBarsPadding

class EditPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.main.applySystemBarsPadding()
        var isOpen = false

        val initText = intent.getStringExtra(KEY_EDIT_TEXT) ?: ""
        binding.edit.setText(initText)
        binding.edit.setSelection(initText.length)

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

            if (text.isBlank()){
                setResult(RESULT_CANCELED)
            }else {
                setResult(RESULT_OK, Intent().apply {
                    putExtra(KEY_EDIT_TEXT, text)
                })
            }
            finish()
        }

        binding.cancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

    }

    companion object {
        val KEY_EDIT_TEXT = "text_edit"
    }
}

object EditPostContract : ActivityResultContract<String, String>() {
    override fun createIntent(
        context: Context,
        input: String
    ): Intent {
        return Intent(context, EditPostActivity::class.java).apply {
            putExtra(KEY_EDIT_TEXT, input)
        }
    }


    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): String {
        return intent?.getStringExtra(KEY_EDIT_TEXT) ?: ""
    }
}