package com.advanced.mockserver

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.advanced.mockserver.utils.Constants
import kotlinx.parcelize.Parcelize

/***
 * Created by HoangRyan aka LilDua on 10/28/2023.
 */
@Parcelize
@Entity(tableName = Constants.TABLE_CHATS)
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val chatId: Long = 0,
    val senderId: Long,
    val receiverId: Long,
    val message: String,
    val timestamp: String,
    val conversationId: Long
): Parcelable