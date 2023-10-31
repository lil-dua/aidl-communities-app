package com.advanced.mockserver

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.advanced.mockserver.utils.Constants
import kotlinx.parcelize.Parcelize

/***
 * Created by HoangRyan aka LilDua on 10/28/2023.
 */
@Parcelize
@Entity(tableName = Constants.TABLE_CONVERSATIONS)
data class Conversation(
    @PrimaryKey(autoGenerate = true)
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