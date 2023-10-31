package com.advanced.mockserver.ui.home

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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.advanced.mockclient.R
import com.advanced.mockclient.databinding.FragmentHomeBinding
import com.advanced.mockserver.IRemoteService
import com.advanced.mockserver.ui.home.conversation.ConversationAdapter
import com.advanced.mockserver.ui.home.user.UserAdapter
import com.advanced.mockserver.utils.Constants
import kotlinx.coroutines.launch

/***
 * Created by HoangRyan aka LilDua on 10/28/2023.
 */
class HomeFragment: Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var conversationAdapter: ConversationAdapter
    private var keyType = 1


    //AIDL remote service
    private var isServiceBound = false
    private var remoteService: IRemoteService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initConnection()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    private fun initConnection() {
        if(!isServiceBound) {
            val intent = Intent(IRemoteService::class.java.name)
            intent.action = "remote_service"
            intent.setPackage("com.advanced.mockserver")
            requireContext().bindService(intent,serviceConnection,Service.BIND_AUTO_CREATE)
        }
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            Log.d(Constants.TAG, "Service Connected")
            remoteService = IRemoteService.Stub.asInterface(iBinder)
            isServiceBound = true
            setListenerUser()
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.d(Constants.TAG, "Service Disconnected")
            isServiceBound = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            homeFragment = this@HomeFragment

            loadUserDetails()
            keyType = Constants.VIEW_TYPE_CONVERSATIONS
            //User listener
            setListenerUser()
        }
    }

    private fun loadUserDetails() {
        binding.textNameUser.text = "Tuấn Minh"
        binding.imageContact.setImageResource(R.drawable.image_tuan_minh)
    }

    fun onRecentConversationClick() {
        keyType = Constants.VIEW_TYPE_CONVERSATIONS
        setListenerUser()
    }

    fun onFriendClick() {
        keyType = Constants.VIEW_TYPE_FRIENDS
        setListenerUser()
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

            try {
                if (isServiceBound) {
                    val remoteConversations = remoteService!!.conversation
                    conversationAdapter.setData(remoteConversations.filter { it.receiverId.toInt() == 2 })
                    Log.i(Constants.TAG, "setListenerUser: $remoteConversations ")
                }
            } catch (remoteException: RemoteException) {
                remoteException.printStackTrace()
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

            try {
                if (isServiceBound) {
                    val remoteUsers = remoteService!!.users
                    userAdapter.setData(remoteUsers.filterNot { it.id.toInt() == 2 })
                    Log.i(Constants.TAG, "setListenerUser: $remoteUsers ")
                }
            } catch (remoteException: RemoteException) {
                remoteException.printStackTrace()
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
            // Contact deleted successfully
            viewLifecycleOwner.lifecycleScope.launch {
                remoteService?.removeUserById(userId)
                Toast.makeText(requireContext(), "Remove..", Toast.LENGTH_SHORT).show()
                removeDialog.dismiss()
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
            // Conversation deleted successfully
            viewLifecycleOwner.lifecycleScope.launch {
                remoteService?.removeConversationById(conversationId)
                Toast.makeText(requireContext(), "Remove..", Toast.LENGTH_SHORT).show()
                removeDialog.dismiss()
            }
        }

        textCancel.setOnClickListener { removeDialog.dismiss() }
        removeDialog.show()
    }


    override fun onResume() {
        super.onResume()
        if (!isServiceBound) {
            initConnection()
        }

    }

    override fun onStart() {
        super.onStart()
        setListenerUser()
    }
    override fun onDestroy() {
        super.onDestroy()
        requireContext().unbindService(serviceConnection)
    }
}