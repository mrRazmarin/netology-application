package ru.netology.nmedia.db

import android.content.Context
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.livedata.LiveDataDaoReturnTypeConverter
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity

@Database(entities = [PostEntity::class], version = 1)
@DaoReturnTypeConverters(LiveDataDaoReturnTypeConverter::class)
abstract class AppDb: RoomDatabase() {
    abstract val postDao: PostDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDb = Room.databaseBuilder(
            context, AppDb::class.java, "app.db"
        ).allowMainThreadQueries().build()
    }
}