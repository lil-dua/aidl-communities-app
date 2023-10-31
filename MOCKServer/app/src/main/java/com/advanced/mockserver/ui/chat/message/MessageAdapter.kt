package com.advanced.mockserver.ui.chat.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.advanced.mockserver.ChatMessage
import com.advanced.mockserver.databinding.ItemReceivedMessageBinding
import com.advanced.mockserver.databinding.ItemSentMessageBinding
import com.advanced.mockserver.utils.Constants

/***
 * Created by HoangRyan aka LilDua on 10/28/2023.
 */
class MessageAdapter (
    private val senderId: Long,
    private val receivedImage: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class SentViewHolder(private val sentBinding: ItemSentMessageBinding) :
        RecyclerView.ViewHolder(sentBinding.root) {
            fun bind(chatMessage: ChatMessage){
                sentBinding.message = chatMessage
            }
    }

    inner class ReceivedViewHolder(private val receivedBinding: ItemReceivedMessageBinding) :
        RecyclerView.ViewHolder(receivedBinding.root){
            fun bind(chatMessage: ChatMessage) {
                receivedBinding.message = chatMessage
                receivedBinding.imageUser.setImageResource(receivedImage)
            }
    }

    //init list chat message
    private var chatMessages: List<ChatMessage> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == Constants.VIEW_TYPE_SENT) {
            return SentViewHolder(
                ItemSentMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }else{
            return ReceivedViewHolder(
                ItemReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun getItemViewType(position: Int): Int {
        if(chatMessages[position].senderId == senderId) {
            return Constants.VIEW_TYPE_SENT
        }else {
            return Constants.VIEW_TYPE_RECEIVED
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == Constants.VIEW_TYPE_SENT){
            (holder as SentViewHolder).bind(chatMessages[position])
        }else{
            (holder as ReceivedViewHolder).bind(chatMessages[position])
        }
    }

    fun setData(newList: List<ChatMessage>) {
        val diffCallback = MessageDiffCallback(chatMessages, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        chatMessages = newList
        diffResult.dispatchUpdatesTo(this)
    }

}

