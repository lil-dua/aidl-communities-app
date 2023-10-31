package com.advanced.mockserver

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/***
 * Created by HoangRyan aka LilDua on 10/28/2023.
 */
@Parcelize
data class ChatMessage(
    val chatId: Long = 0,
    val senderId: Long,
    val receiverId: Long,
    val message: String,
    val timestamp: String,
    val conversationId: Long
): Parcelable