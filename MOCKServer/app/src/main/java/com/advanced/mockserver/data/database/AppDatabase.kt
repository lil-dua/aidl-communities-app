package com.advanced.mockserver.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.advanced.mockserver.data.database.dao.ChatDao
import com.advanced.mockserver.data.database.dao.ConversationDao
import com.advanced.mockserver.data.database.dao.UserDao
import com.advanced.mockserver.ChatMessage
import com.advanced.mockserver.Conversation
import com.advanced.mockserver.User
import com.advanced.mockserver.utils.Constants

/***
 * Created by HoangRyan aka LilDua on 10/27/2023.
 */
@Database(
    entities = [User::class, Conversation::class, ChatMessage::class],
    version = Constants.DATABASE_VERSION
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun conversationDao(): ConversationDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext, // Use applicationContext
                AppDatabase::class.java,
                Constants.DATABASE_NAME
            ).build()
        }
    }
}