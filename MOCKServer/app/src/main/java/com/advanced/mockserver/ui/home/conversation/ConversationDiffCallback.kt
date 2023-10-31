package com.advanced.mockserver.ui.home.conversation

import androidx.recyclerview.widget.DiffUtil
import com.advanced.mockserver.Conversation

/***
 * Created by HoangRyan aka LilDua on 10/29/2023.
 */
class ConversationDiffCallback(
    private val oldList: List<Conversation>,
    private val newList: List<Conversation>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].conversationId == newList[newItemPosition].conversationId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}