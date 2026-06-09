package ru.netology.nmedia.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityNewPostBinding
import ru.netology.nmedia.utils.AndroidUtils.applySystemBarsPadding

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.main.applySystemBarsPadding()

        binding.ok.setOnClickListener {
            val text = binding.edit.text.toString()

            if (text.isBlank()){
                setResult(RESULT_CANCELED)
            }
            else {
                setResult(RESULT_OK, Intent().apply {
                    putExtra(KEY_POST_TEXT, text)
                })
            }
            finish()
        }

    }

    companion object {
        const val KEY_POST_TEXT = "post_text"
    }
}

object NewPostContract : ActivityResultContract<Unit, String>() {
    override fun createIntent(
        context: Context,
        input: Unit
    ) = Intent(context, NewPostActivity::class.java)

    override fun parseResult(resultCode: Int, intent: Intent?): String = intent?.getStringExtra(NewPostActivity.KEY_POST_TEXT) ?: ""
}