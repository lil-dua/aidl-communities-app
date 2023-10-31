package com.advanced.mockserver.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.advanced.mockserver.Conversation
import com.advanced.mockserver.utils.Constants
import java.sql.Timestamp

/***
 * Created by HoangRyan aka LilDua on 10/28/2023.
 */
@Dao
interface ConversationDao {
    @Insert
    fun insert(conversation: Conversation)

    @Query("SELECT * FROM ${Constants.TABLE_CONVERSATIONS}")
    fun getAllConversations(): LiveData<List<Conversation>>

    @Query("SELECT * FROM ${Constants.TABLE_CONVERSATIONS}")
    fun getRemoteAllConversations(): List<Conversation>

    @Query("SELECT * FROM ${Constants.TABLE_CONVERSATIONS} WHERE ${Constants.KEY_CONVERSATION_ID} = :conversationId")
    fun getConversationById(conversationId: Long): LiveData<Conversation>

    @Query("UPDATE ${Constants.TABLE_CONVERSATIONS} " +
            "SET ${Constants.COLUMN_LAST_MESSAGE} = :lastMessage, ${Constants.COLUMN_TIMESTAMP} =:timestamp " +
            "WHERE ${Constants.KEY_CONVERSATION_ID} = :conversationId")
    fun updateConversation(conversationId: Long, lastMessage: String, timestamp: String)

    @Query("DELETE FROM ${Constants.TABLE_CONVERSATIONS} WHERE ${Constants.KEY_CONVERSATION_ID} = :conversationId")
    fun deleteConversationById(conversationId: Long)


}