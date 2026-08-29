package ru.netology.nmedia.service


import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
    /**
     * ОБЩАЯ КЛАДОВКА КОНСТАНТ
     *
     * @param channelId строка-идентификатор канала уведомлений. Android требует, чтобы уведомления были
     *  привязаны к каналам (начиная с Android 8.0).
     *
     * @param ACTION_KEY
     * @param CONTENT_KEY ключи, по которым сервер передаёт данные внутри пуша. Представь,
     *  что пуш — это конверт, а эти ключи — надписи на полях («action», «content»).
     *
     * @param gson объект библиотеки Gson. Он умеет превращать JSON-строки в Kotlin-объекты (и наоборот).
     *  Сервер присылает данные в формате JSON, а Gson превращает их в понятные объекты.
     */
    companion object {
        const val channelId = "remote"
        const val ACTION_KEY = "action"
        const val CONTENT_KEY = "content"
        val gson = Gson()
    }

    /**
     * ПОДГОТОВКА КАНАЛА УВЕДОМЛЕНИЙ
     *
     * Этот метод ```onCreate()``` вызывается один раз, когда система Android впервые создаёт твой сервис
     *
     *
     * ```kotlin
     * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) // проверка: а не слишком ли старый телефон? Каналы уведомлений появились только в Android 8.0 (Oreo). На старых версиях это не нужно.
     * ```
     *
     * @param NotificationChannel  создаётся «канал» с ID "remote", именем из ресурсов и обычным
     * приоритетом. Канал — это как группа в настройках уведомлений телефона. Пользователь может
     * зайти в настройки приложения и отключить именно этот канал.
     *
     * ```getSystemService(NOTIFICATION_SERVICE)``` - просишь у Android «менеджера уведомлений».
     *
     * ```manager.createNotificationChannel(channel)``` - регистрируешь канал в системе. Без этого на
     * Android 8+ уведомления просто не покажутся.
     *
     * */
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descText = "Notification from remote server"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descText
            }

            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * НОВЫЙ ТОКЕН УСТРОЙСТВА
     *
     * Firebase присваивает каждому устройству уникальный токен — это как адрес почтового ящика. Когда токен меняется (например, при переустановке приложения), вызывается этот метод.
     *
     * В текущем коде токен просто печатается в консоль (println). В реальном приложении его нужно отправить на свой сервер, иначе сервер не будет знать, куда слать пуши.
     */
    override fun onNewToken(token: String) {
        println(token)
    }

    /**
     * ПУШ ПРИШЕЛ!
     *
     * Это самое важное. Когда сервер шлёт пуш, вызывается этот метод. ```message``` — это само сообщение.
     *
     * ```message.data``` — это ```Map<String, String>```, словарь с данными от сервера.
     * Сервер присылает что-то вроде:
     * ```kotlin
     * action: LIKE
     * content: {"userId": 123, "userName": "Вася", "postId": 456, "postAuthor": "Петя"}
     * ```
     *
     * ```message.data[ACTION_KEY]``` — достаёт значение по ключу "action".
     *
     * ```.orEmpty()``` — если ключа нет, вернётся пустая строка "", а не ```null``` (чтобы не было ошибки).
     *
     * ```Actions.valueOf(...)``` — превращает строку "LIKE" в значение enum Actions.LIKE.
     *
     * ```when``` — аналог switch. Сейчас там только один case — LIKE.
     *
     * ```gson.fromJson(..., Like::class.java)``` — берёт JSON из content, и превращает его в объект типа Like.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        //println(message)
        try {
            when (Actions.valueOf(message.data[ACTION_KEY].orEmpty())) {
                Actions.LIKE -> handleLike(
                    gson.fromJson(message.data[CONTENT_KEY], Like::class.java)
                )
            }
        }catch (e: Exception) {
            Log.e("exceptions",e.stackTraceToString())
        }
    }

    /**
     * СОБИРАЕМ УВЕДОМЛЕНИЕ
     *
     * Этот метод ```fun handleLike(content: Like)``` строит уведомление, которое пользователь увидит в шторке телефона.
     *
     * ```NotificationCompat.Builder(this, channelId)``` — конструктор уведомления. ```channelId``` — тот самый "remote", который мы создали в onCreate.
     *
     * ```setSmallIcon``` — маленькая иконка в статус-баре (должна быть в ресурсах приложения).
     *
     * ```setContentTitle``` — заголовок уведомления. ```getString(R.string.notification_user_liked, ...)``` берёт строку из ресурсов и подставляет туда userName и postAuthor. Например, если в ресурсах написано "%1$s лайкнул пост %2$s", получится «Вася лайкнул пост Петя».
     *
     * ```setPriority``` — обычный приоритет (не срочное, не тихое).
     *
     * ```.build()``` — «собрать» уведомление.
     *
     * ```notify(notification)``` — вызываем свой метод для показа.
     */
    private fun handleLike(content: Like) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    content.userName,
                    content.postAuthor
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notify(notification)
    }

    /**
     * ПОКАЗЫВАЕМ УВЕДОМЛЕНИЯ С ПРОВЕРКОЙ РАЗРЕШЕНИЯ
     *
     * Здесь ```fun notify(notification: Notification)``` происходит финальный запуск уведомления, но с защитой от ошибок.
     *
     * ```Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU``` — если Android ниже 13 (Tiramisu), разрешение на уведомления не требуется — оно давалось автоматически при установке.
     *
     * ```checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED``` — если Android 13+, проверяем: а разрешил ли пользователь показывать уведомления? В Android 13 это стало обязательным.
     *
     * ```NotificationManagerCompat.from(this)``` — получаем менеджер уведомлений (совместимая версия).
     *
     * ```.notify(Random.nextInt(100_000), notification)``` — показываем уведомление. ID генерируется случайным числом от 0 до 100 000. Если ID повторится, новое уведомление заменит старое с таким же ID. Случайное число гарантирует, что каждое уведомление будет отдельным.
     */
    private fun notify(notification: Notification) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat
                .from(this)
                .notify(Random.nextInt(100_000), notification)
        }
    }

    enum class Actions {
        LIKE
    }

    data class Like(
        val userId: Long,
        val userName: String,
        val postId: Long,
        val postAuthor: String
    )
}