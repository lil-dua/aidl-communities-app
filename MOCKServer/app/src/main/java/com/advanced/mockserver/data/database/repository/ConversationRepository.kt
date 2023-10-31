package com.advanced.mockserver.data.database.repository

import androidx.lifecycle.LiveData
import com.advanced.mockserver.data.database.dao.ConversationDao
import com.advanced.mockserver.Conversation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.sql.Timestamp
import javax.inject.Inject
import javax.inject.Singleton

/***
 * Created by HoangRyan aka LilDua on 10/28/2023.
 */
@Singleton
class ConversationRepository @Inject constructor(private val conversationDao: ConversationDao) {
    fun insertConversation(conversation: Conversation): Flow<Unit> = flow{
        emit(conversationDao.insert(conversation))
    }.flowOn(Dispatchers.IO)

    fun getAllConversation(): LiveData<List<Conversation>> {
        return conversationDao.getAllConversations()
    }

    fun updateConversation(conversationId: Long, lastMessage: String, timestamp: String): Flow<Unit> = flow {
        emit(conversationDao.updateConversation(conversationId, lastMessage,timestamp))
    }.flowOn(Dispatchers.IO)

    fun deleteConversationById(conversationId: Long) : Flow<Unit> = flow {
        emit(conversationDao.deleteConversationById(conversationId))
    }.flowOn(Dispatchers.IO)
}