package com.advanced.mockserver.ui.home.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.advanced.mockclient.R
import com.advanced.mockserver.Conversation
import com.advanced.mockclient.databinding.ItemConversationBinding
import com.advanced.mockserver.utils.Constants
import com.advanced.mockserver.ui.home.HomeFragment

/***
 * Created by HoangRyan aka LilDua on 10/29/2023.
 */
class ConversationAdapter(
    private val homeFragment: HomeFragment,
    private val navController: NavController
) : RecyclerView.Adapter<ConversationAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(conversation: Conversation) {
            binding.conversation = conversation
            binding.imageConversation.setImageResource(conversation.senderImage)
        }
    }

    //init list user
    private var conversations: List<Conversation> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemConversationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conversation = conversations[position]
        holder.bind(conversation)

        //handle on item clicked
        holder.itemView.setOnClickListener {
            // Handle the item click here and navigate to the new fragment with contact details
            val bundle = Bundle()
            bundle.putLong(Constants.KEY_CONVERSATION_ID, conversation.conversationId)
            bundle.putLong(Constants.KEY_SENDER_ID, conversation.receiverId)
            bundle.putLong(Constants.KEY_RECEIVER_ID, conversation.senderId)
            bundle.putString(Constants.KEY_RECEIVER_NAME,conversation.senderName)
            bundle.putInt(Constants.KEY_RECEIVER_IMAGE, conversation.senderImage)

            navController.navigate(
                R.id.action_homeFragment_to_chatFragment,
                bundle
            )
        }

        //handle on long click
        holder.itemView.setOnLongClickListener {
            homeFragment.removeConversationWithId(conversation.conversationId)
            true
        }
    }

    override fun getItemCount(): Int {
        return conversations.size
    }

    fun setData(newList: List<Conversation>) {
        val diffCallback = ConversationDiffCallback(conversations, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        conversations = newList
        diffResult.dispatchUpdatesTo(this)
    }

}