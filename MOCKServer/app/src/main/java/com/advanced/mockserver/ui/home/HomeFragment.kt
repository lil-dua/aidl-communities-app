package com.advanced.mockserver.ui.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.advanced.mockserver.R
import com.advanced.mockserver.Conversation
import com.advanced.mockserver.User
import com.advanced.mockserver.databinding.FragmentHomeBinding
import com.advanced.mockserver.ui.home.conversation.ConversationAdapter
import com.advanced.mockserver.ui.home.user.UserAdapter
import com.advanced.mockserver.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/***
 * Created by HoangRyan aka LilDua on 10/28/2023.
 */
@AndroidEntryPoint
class HomeFragment: Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var userAdapter: UserAdapter

    private lateinit var conversationAdapter: ConversationAdapter
    private var keyType = Constants.VIEW_TYPE_CONVERSATIONS

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            homeFragment = this@HomeFragment

            homeViewModel.conversations.observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    insertNewUser()
                }else{
                    //Get key user id
                    val currentUser = homeViewModel.getUserById(1)
                    currentUser.observe(viewLifecycleOwner) { user ->
                        binding.textNameUser.text = user.name
                        binding.imageContact.setImageResource(user.image)
                    }
                }
            }

            //User listener
            setListenerUser()
        }
    }

    fun onRecentConversationClick() {
        keyType = Constants.VIEW_TYPE_CONVERSATIONS
        setListenerUser()  //change list to recent conversations
    }

    fun onFriendClick() {
        keyType = Constants.VIEW_TYPE_FRIENDS
        setListenerUser() //change list to friends
    }

    private fun setListenerUser() {
        if(keyType == Constants.VIEW_TYPE_CONVERSATIONS) {
            //set color
            binding.textConversations.setBackgroundResource(R.drawable.background_edit_text)
            binding.textConversations.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            binding.textFriends.setBackgroundResource(R.drawable.background_cancel)
            binding.textFriends.setTextColor(ContextCompat.getColor(requireContext(),R.color.primary_text_color))
            binding.textTitleHome.setText(R.string.conversations)

            //init view
            conversationAdapter = ConversationAdapter(this,findNavController())
            binding.recycleViewUsers.layoutManager = LinearLayoutManager(requireContext())
            binding.recycleViewUsers.adapter = conversationAdapter

            //observe the list of conversation from the viewModel and update the adapter
            homeViewModel.conversations.observe(viewLifecycleOwner) {conversations ->
                conversationAdapter.setData(conversations.filterNot { it.receiverId.toInt() == 1 })
            }

        }else {
            //set color
            binding.textFriends.setBackgroundResource(R.drawable.background_edit_text)
            binding.textFriends.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            binding.textConversations.setBackgroundResource(R.drawable.background_cancel)
            binding.textConversations.setTextColor(ContextCompat.getColor(requireContext(),R.color.primary_text_color))
            binding.textTitleHome.setText(R.string.friends)

            //init view
            userAdapter = UserAdapter(this)
            binding.recycleViewUsers.layoutManager = GridLayoutManager(requireContext(),2)
            binding.recycleViewUsers.adapter = userAdapter

            //observe the list of user from the viewModel and update the adapter
            homeViewModel.users.observe(viewLifecycleOwner) { users ->
                userAdapter.setData(users.filterNot { it.id.toInt() == 1 })
            }
        }
    }
    private fun insertNewUser() {
        // Add the contact using Kotlin Flow
        val users = listOf(
            User(id = 1, name = "Hoàng Thơm", image = R.drawable.image_hoang_thom),
            User(id = 2, name = "Tuấn Minh", image = R.drawable.image_tuan_minh),
            User(id = 3, name = "Mai Trang", image = R.drawable.image_mai_trang),
            User(id = 4, name = "Minh Quân", image = R.drawable.image_minh_quan),
        )
        for (user in users) {
            viewLifecycleOwner.lifecycleScope.launch {
                homeViewModel.insertUser(user)
                    .catch { error ->
                        Toast.makeText(requireContext(), "Unable to register! ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                    .collect {
                        //Do nothing
                    }
            }
        }

        for (user in users.filterNot { user ->  user.id.toInt() == 1}) {
            //Add new conversation
            val conversation = Conversation(
                senderId = 1, senderImage = R.drawable.image_hoang_thom, senderName = "Hoàng Thơm",
                receiverId = user.id, receiverImage = user.image, receiverName = user.name,
                lastMessage = "", timestamp = getCurrentTime())

            viewLifecycleOwner.lifecycleScope.launch {
                homeViewModel.insertConversation(conversation)
                    .catch { error ->
                        Toast.makeText(requireContext(), "Unable to register! ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                    .collect {
                        //Do nothing
                    }
            }
        }
    }

    fun removeUserWithId(userId: Long){
        val removeDialog = Dialog(requireContext())
        removeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        removeDialog.setContentView(R.layout.dialog_remove)

        val textRemove: TextView = removeDialog.findViewById(R.id.text_dialog_delete)
        val textCancel: TextView = removeDialog.findViewById(R.id.text_dialog_cancel)

        textRemove.setOnClickListener {
            // Remove the contact using Kotlin Flow
            viewLifecycleOwner.lifecycleScope.launch {
                homeViewModel.deleteUserById(userId)
                    .catch { error ->
                        Toast.makeText(requireContext(), "Error deleting contact: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                    .collect {
                        // Contact deleted successfully
                        Toast.makeText(requireContext(), "Remove..", Toast.LENGTH_SHORT).show()
                        removeDialog.dismiss()
                    }
            }
        }

        textCancel.setOnClickListener { removeDialog.dismiss() }
        removeDialog.show()
    }

    fun removeConversationWithId(conversationId: Long){
        val removeDialog = Dialog(requireContext())
        removeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        removeDialog.setContentView(R.layout.dialog_remove_conversation)

        val textRemove: TextView = removeDialog.findViewById(R.id.text_dialog_delete)
        val textCancel: TextView = removeDialog.findViewById(R.id.text_dialog_cancel)

        textRemove.setOnClickListener {
            // Remove the contact using Kotlin Flow
            viewLifecycleOwner.lifecycleScope.launch {
                homeViewModel.deleteConversationById(conversationId)
                    .catch { error ->
                        Toast.makeText(requireContext(), "Error deleting contact: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                    .collect {
                        // Contact deleted successfully
                        Toast.makeText(requireContext(), "Remove..", Toast.LENGTH_SHORT).show()
                        removeDialog.dismiss()
                    }
            }
        }

        textCancel.setOnClickListener { removeDialog.dismiss() }
        removeDialog.show()
    }

    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    override fun onStart() {
        super.onStart()
        setListenerUser()
    }

}