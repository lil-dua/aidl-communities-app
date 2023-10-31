package com.advanced.mockserver.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.advanced.mockserver.ChatMessage
import com.advanced.mockserver.utils.Constants

/***
 * Created by HoangRyan aka LilDua on 10/28/2023.
 */
// ChatDao.kt
@Dao
interface ChatDao {
    @Insert
    fun insert(chatMessage: ChatMessage)

    @Query("SELECT * FROM ${Constants.TABLE_CHATS}")
    fun getAllChats(): LiveData<List<ChatMessage>>

    @Query("SELECT * FROM ${Constants.TABLE_CHATS}")
    fun getRemoteAllChats(): List<ChatMessage>

    @Query("SELECT * FROM ${Constants.TABLE_CHATS} WHERE ${Constants.KEY_CONVERSATION_ID} = :conversationId")
    fun getChatInConversation(conversationId: Long): LiveData<List<ChatMessage>>
}