package com.advanced.mockserver.ui.chat

import android.app.Dialog
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.advanced.mockclient.R
import com.advanced.mockserver.ChatMessage
import com.advanced.mockclient.databinding.FragmentChatBinding
import com.advanced.mockserver.IRemoteService
import com.advanced.mockserver.ui.chat.message.MessageAdapter
import com.advanced.mockserver.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/***
 * Created by HoangRyan aka LilDua on 10/28/2023.
 */
class ChatFragment: Fragment() {

    private lateinit var binding: FragmentChatBinding

    //instance
    private lateinit var adapter: MessageAdapter

    private var conversationId: Long? = null
    private var receiverId: Long = 0
    private var senderId: Long = 0
    private var receiverImage: Int = 0
    private var receiverName: String = ""

    //AIDL remote service
    private var isServiceBound = false
    private var remoteService: IRemoteService? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initConnection()
    }

    private fun initConnection() {
        if(!isServiceBound) {
            val intent = Intent(IRemoteService::class.java.name)
            intent.action = "remote_service"
            intent.setPackage("com.advanced.mockserver")
            requireContext().bindService(intent,serviceConnection, Service.BIND_AUTO_CREATE)
        }
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            Log.d(Constants.TAG, "Service Connected")
            remoteService = IRemoteService.Stub.asInterface(iBinder)
            isServiceBound = true
            loadChat()
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.d(Constants.TAG, "Service Disconnected")
            isServiceBound = false
        }
    }

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
            //Get selected user id
            receiverId = requireArguments().getLong(Constants.KEY_RECEIVER_ID)
            senderId = requireArguments().getLong(Constants.KEY_SENDER_ID)
            conversationId = requireArguments().getLong(Constants.KEY_CONVERSATION_ID)
            receiverImage = requireArguments().getInt(Constants.KEY_RECEIVER_IMAGE)
            receiverName = requireArguments().getString(Constants.KEY_RECEIVER_NAME).toString()

            //init adapter
            adapter = MessageAdapter(senderId, receiverImage)
            recyclerViewChat.adapter = adapter

            chatFragment = this@ChatFragment
            loadReceiverDetail(receiverName,receiverImage)
        }
    }

    private fun loadChat() {
        try {
            if(isServiceBound) {
                val remoteChatMessage = remoteService?.chatMessages ?: emptyList()
                adapter.setData(remoteChatMessage.filter { it.conversationId == conversationId })
                binding.recyclerViewChat.smoothScrollToPosition(adapter.itemCount - 1)
                Log.i(Constants.TAG, "remoteChatMessage: $remoteChatMessage ")
            }else{
                initConnection()
            }
        } catch (remoteException: RemoteException) {
            remoteException.printStackTrace()
        }
    }

    private fun loadReceiverDetail(receiverName: String ,receiverImage: Int) {
        binding.textNameUser.text = receiverName
        binding.imageContact.setImageResource(receiverImage)
    }

    fun sendMessage() {
        val message = ChatMessage(
            senderId = senderId,
            receiverId = receiverId,
            message = binding.inputMessage.text.toString(),
            timestamp = getCurrentTime(),
            conversationId = conversationId!!
        )

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            remoteService?.sendMessage(message)
            remoteService?.updateConversation(conversationId!!,message.message,getCurrentTime())
            val remoteChatMessage = remoteService?.chatMessages ?: emptyList()
            withContext(Dispatchers.Main) {
                adapter.setData(remoteChatMessage.filter { it.conversationId == conversationId })
                binding.recyclerViewChat.smoothScrollToPosition(adapter.itemCount - 1)
            }
            Log.i(Constants.TAG, "remoteChatMessage: $remoteChatMessage ")
        }
        binding.inputMessage.text = null

    }

    fun removeConversation() {
        val removeDialog = Dialog(requireContext())
        removeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        removeDialog.setContentView(R.layout.dialog_remove_conversation)

        val textRemove: TextView = removeDialog.findViewById(R.id.text_dialog_delete)
        val textCancel: TextView = removeDialog.findViewById(R.id.text_dialog_cancel)
        textRemove.setOnClickListener {
            // Conversation remove successfully
            viewLifecycleOwner.lifecycleScope.launch {
                remoteService?.removeConversationById(conversationId!!)
                Toast.makeText(requireContext(), "Remove..", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                removeDialog.dismiss()
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

    override fun onResume() {
        super.onResume()
        if (!isServiceBound) {
            initConnection()
        }
    }

    override fun onStart() {
        super.onStart()
        if(!isServiceBound) {
            initConnection()
        }
        loadChat()
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unbindService(serviceConnection)
    }
}