package com.advanced.mockserver

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

/***
 * Created by HoangRyan aka LilDua on 10/28/2023.
 */
@Parcelize
data class Conversation(
    val conversationId: Long = 0,
    val senderId: Long,
    @DrawableRes val senderImage: Int,
    val senderName: String,
    val receiverId: Long,
    @DrawableRes val receiverImage: Int,
    val receiverName: String,
    val lastMessage: String,
    val timestamp: String
): Parcelable