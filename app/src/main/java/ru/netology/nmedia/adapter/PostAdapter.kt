package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.AndroidUtils.completionPost

/**
 * Это контракт на обработку действий пользователя.
 * Если кто-то хочет работать с постами, он должен уметь реагировать на эти события
 *
 * Здесь нет реализации, только список возможных действий.
 * Это нужно для того, чтобы Adapter не знал, что именно делать при нажатии, а только сообщал: “по этому посту нажали лайк”, “удалить”, “редактировать”.
 */
interface PostListener{
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onView(post: Post)
    fun onRemove(post: Post)
    fun onEdit(post: Post)
    fun onVideo(post: Post)
    fun onPostClick(post: Post)
}

/**
 PostAdapter - адаптер для RecycleView
 private val postListener: PostListener - адаптер получает объект, который умеет обрабатывать действия над постом

 ListAdapter<Post, PostViewHolder> означает:
    1. Элементы списка имеют тип Post
    2. Для отображения одного элемента используется PostViewHolder

 (PostDiffUtilCallBack) - это объект, который сравнивает старый и новый список и помогает обновлять только изменившиеся элементы

 Почему это важно:
    1. Обычный 'RecyclerView.Adapter' можно обновлять вручную,
    2. ListAdapter делает это удобнее и эффективнее через DiffUtil.
 */
class PostAdapter(
    private val postListener: PostListener
): ListAdapter<Post, PostViewHolder>(PostDiffUtilCallBack) {
    /**
    Этот метод вызывается, когда RecyclerView нужно создать новый элемент UI.

    viewType нужен, если у вас несколько типов элементов.

    Здесь еще нет конкретного Post. Только создается “пустая карточка”.
     */
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        /**
         * CardPostBinding - это класс, созданный через ViewBinding.
         *
         * inflate(...) превращает XML-разметку карточки в объект, с которым можно работать из кода.
         *
         * LayoutInflater.from(viewGroup.context) - берет Context, чтобы создать view из XML.
         *
         * viewGroup - родительский контейнер.
         *
         * false - значит “не прикреплять к родителю прямо сейчас”, это сделает RecyclerView сам.
         */
        val binding = CardPostBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )

        return PostViewHolder(
            binding, postListener
        )
    }

    /**
     * Этот метод вызывается, когда нужно заполнить уже созданную карточку данными.
     *
     * getItem(position) берет объект Post из списка по позиции.
     *
     * viewHolder.bind(post) передает пост в ViewHolder, чтобы он отобразил данные.
     */
    override fun onBindViewHolder(viewHolder: PostViewHolder, position: Int) {
        val post = getItem(position)
        viewHolder.bind(post)
    }
}

/**
 * PostViewHolder отвечает за одну карточку в списке.
 *
 * binding дает доступ ко всем view внутри карточки.
 *
 * postListener нужен для кликов и действий.
 *
 * 'RecyclerView.ViewHolder(binding.root)' означает, что корневой view карточки - это 'binding.root'.
 */
class PostViewHolder(
    private val binding: CardPostBinding,
    private val postListener: PostListener
): RecyclerView.ViewHolder(binding.root) {
    /**
     * Метод bind(post) получает конкретный Post.
     *
     * Дальше он делегирует всю работу функции completionPost(...).
     *
     * То есть bind() - это место, где пост превращается в готовый UI.
     * */
    fun bind(post: Post) {
        completionPost(
            post = post,
            postListener,
            cardPostBinding = binding
        )
    }
}

/**
 * Это объект, который умеет сравнивать два Post.
 *
 * DiffUtil нужен, чтобы понять, что изменилось между старым списком и новым.
 *
 * object здесь удобен, потому что callback один и тот же для всех.
 * */
object PostDiffUtilCallBack: DiffUtil.ItemCallback<Post>() {

    /**
     * Этот метод отвечает на вопрос: это один и тот же объект списка или нет?
     *
     * Обычно сравнивают по id.
     *
     * Если id одинаковый, значит это один и тот же пост, даже если его содержимое поменялось.
     * */
    override fun areItemsTheSame(
        oldItem: Post,
        newItem: Post
    ): Boolean {
        return oldItem.id == newItem.id
    }

    /**
     * Этот метод отвечает на вопрос: содержимое одинаковое или нет?
     *
     * Если Post - это data class, то == сравнивает все поля.
     *
     * Если поменялся текст, картинка, счетчик лайков и т. п., DiffUtil поймет, что элемент надо обновить.
     * */
    override fun areContentsTheSame(
        oldItem: Post,
        newItem: Post
    ): Boolean {
        return oldItem == newItem
    }
}