package com.advanced.mockserver.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.advanced.mockserver.ChatMessage
import com.advanced.mockserver.Conversation
import com.advanced.mockserver.IRemoteService
import com.advanced.mockserver.User
import com.advanced.mockserver.data.database.AppDatabase

/***
 * Created by HoangRyan aka LilDua on 10/27/2023.
 */
class RemoteService : Service(){
    private lateinit var chatDatabase: AppDatabase
    override fun onBind(intent: Intent?): IBinder {
        chatDatabase = AppDatabase.getDatabase(this)
        return mBinder
    }

    private val mBinder = object : IRemoteService.Stub() {
        override fun getConversation(): List<Conversation> {
            return chatDatabase.conversationDao().getRemoteAllConversations()
        }

        override fun getUsers(): List<User> {
            return chatDatabase.userDao().getRemoteAllUsers()
        }

        override fun getChatMessages(): List<ChatMessage> {
            return chatDatabase.chatDao().getRemoteAllChats()
        }

        override fun sendMessage(message: ChatMessage?) {
            chatDatabase.chatDao().insert(message!!)
        }

        override fun removeConversationById(conversationId: Long) {
            chatDatabase.conversationDao().deleteConversationById(conversationId)
        }

        override fun removeUserById(userId: Long) {
            chatDatabase.userDao().deleteUserById(userId)
        }

        override fun updateConversation(
            conversationId: Long,
            lastMessage: String?,
            timestamp: String?
        ) {
            chatDatabase.conversationDao().updateConversation(conversationId,lastMessage!!,timestamp!!)
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }
}