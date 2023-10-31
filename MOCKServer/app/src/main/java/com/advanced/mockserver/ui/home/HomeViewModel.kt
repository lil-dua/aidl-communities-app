package com.advanced.mockserver.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.advanced.mockserver.data.database.repository.ConversationRepository
import com.advanced.mockserver.data.database.repository.UserRepository
import com.advanced.mockserver.Conversation
import com.advanced.mockserver.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/***
 * Created by HoangRyan aka LilDua on 10/28/2023.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    // LiveData to hold the list of users
    val users: LiveData<List<User>> = userRepository.getAllUsers()

    // LiveData to hold the list of conversations
    val conversations: LiveData<List<Conversation>> = conversationRepository.getAllConversation()

    fun insertUser(user: User) = userRepository.insertUser(user)
    fun getUserById(userId: Long): LiveData<User> {
        return userRepository.getUserById(userId)
    }
    fun insertConversation(conversation: Conversation) = conversationRepository.insertConversation(conversation)

    fun deleteUserById(userId: Long) = userRepository.deleteUserById(userId)

    fun deleteConversationById(conversationId: Long) = conversationRepository.deleteConversationById(conversationId)
}