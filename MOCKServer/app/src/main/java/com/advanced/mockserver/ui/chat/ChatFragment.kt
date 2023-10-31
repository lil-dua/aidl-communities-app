package com.advanced.mockserver.ui.chat

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.advanced.mockserver.R
import com.advanced.mockserver.ChatMessage
import com.advanced.mockserver.databinding.FragmentChatBinding
import com.advanced.mockserver.ui.chat.message.MessageAdapter
import com.advanced.mockserver.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/***
 * Created by HoangRyan aka LilDua on 10/28/2023.
 */
@AndroidEntryPoint
class ChatFragment: Fragment() {
    private lateinit var binding: FragmentChatBinding
    private val chatViewModel: ChatViewModel by viewModels()

    //instance
    private lateinit var adapter: MessageAdapter

    //key
    private var conversationId: Long? = null
    private var receiverId: Long = 0
    private var senderId: Long = 0
    private var receiverImage: Int = 0
    private var receiverName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            chatFragment = this@ChatFragment

            //Get selected user id
            receiverId = requireArguments().getLong(Constants.KEY_RECEIVER_ID)
            senderId = requireArguments().getLong(Constants.KEY_SENDER_ID)
            conversationId = requireArguments().getLong(Constants.KEY_CONVERSATION_ID)
            receiverImage = requireArguments().getInt(Constants.KEY_RECEIVER_IMAGE)

            //init adapter
            adapter = MessageAdapter(senderId,receiverImage)
            binding.recyclerViewChat.adapter = adapter

            //get receiver detail
            chatViewModel.getUserById(receiverId).observe(viewLifecycleOwner) {
                receiverName = it.name
                binding.textNameUser.text = it.name
                binding.imageContact.setImageResource(it.image)
            }

            initConversation()   //Init view
        }
    }

    private fun initConversation() {
        if (conversationId?.toInt() != 0){
            val chatInConversation = chatViewModel.getChatInConversation(conversationId!!)
            //observe the list of chat message from the viewModel and update the adapter
            chatInConversation.observe(viewLifecycleOwner) {
                adapter.setData(it)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        initConversation()
    }

    fun sendMessage() {
        val message = ChatMessage(
            senderId = senderId,
            receiverId = receiverId,
            message = binding.inputMessage.text.toString(),
            timestamp = getCurrentTime(),
            conversationId = conversationId!!
        )

        //add chat message
        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.insertChatMessage(message)
                .catch { error ->
                    Toast.makeText(requireContext(), "Error for adding message: ${error.message}", Toast.LENGTH_SHORT).show()
                }
                .collect {
                    //Do nothing
                    updateCurrentConversation(conversationId!!, message.message)
                }
        }

        binding.inputMessage.text = null

    }

    private fun updateCurrentConversation(conversationId: Long, message: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.updateConversation(conversationId,message,getCurrentTime())
                .catch { error ->
                    Toast.makeText(requireContext(), "Error for update conversation: ${error.message}", Toast.LENGTH_SHORT).show()
                }
                .collect {
                  //Do nothing
                }
        }
    }

    fun removeConversation() {
        val removeDialog = Dialog(requireContext())
        removeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        removeDialog.setContentView(R.layout.dialog_remove_conversation)

        val textRemove: TextView = removeDialog.findViewById(R.id.text_dialog_delete)
        val textCancel: TextView = removeDialog.findViewById(R.id.text_dialog_cancel)
        textRemove.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                chatViewModel.removeConversation(conversationId!!)
                    .catch { error ->
                        Toast.makeText(requireContext(), "Error for remove contact: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                    .collect {
                        // Contact added successfully
                        Toast.makeText(requireContext(), "Remove..", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
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

    //navigation
    fun backToHome(){
        findNavController().popBackStack()
    }
}