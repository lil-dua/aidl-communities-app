package com.advanced.mockserver.data.database.repository

import androidx.lifecycle.LiveData
import com.advanced.mockserver.data.database.dao.ChatDao
import com.advanced.mockserver.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/***
 * Created by HoangRyan aka LilDua on 10/28/2023.
 */
@Singleton
class ChatRepository @Inject constructor(private val chatDao: ChatDao) {
    fun insertChat(chatMessage: ChatMessage): Flow<Unit> = flow {
        emit(chatDao.insert(chatMessage))
    }.flowOn(Dispatchers.IO)


    fun getChatInConversation(conversationId: Long): LiveData<List<ChatMessage>> {
        return chatDao.getChatInConversation(conversationId)
    }
}