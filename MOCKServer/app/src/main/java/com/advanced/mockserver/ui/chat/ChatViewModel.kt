package com.advanced.mockserver.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.advanced.mockserver.data.database.repository.ChatRepository
import com.advanced.mockserver.data.database.repository.ConversationRepository
import com.advanced.mockserver.data.database.repository.UserRepository
import com.advanced.mockserver.ChatMessage
import com.advanced.mockserver.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/***
 * Created by HoangRyan aka LilDua on 10/28/2023.
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val conversationRepository: ConversationRepository
): ViewModel() {

    //User
    fun getUserById(userId: Long): LiveData<User> {
        return userRepository.getUserById(userId)
    }

    //Conversation
    fun updateConversation(conversationId: Long, message: String, timestamp: String)
        = conversationRepository.updateConversation(conversationId, message, timestamp)
    fun removeConversation(conversationId: Long) = conversationRepository.deleteConversationById(conversationId)

    //Chat Message
    fun insertChatMessage(chatMessage: ChatMessage) = chatRepository.insertChat(chatMessage)
    fun getChatInConversation(conversationId: Long): LiveData<List<ChatMessage>> {
        return chatRepository.getChatInConversation(conversationId)
    }

}