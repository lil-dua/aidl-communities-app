package com.advanced.mockserver.utils

/***
 * Created by HoangRyan aka LilDua on 10/27/2023.
 */
object Constants {
    //database key
    const val DATABASE_NAME = "chat-app.db"
    const val DATABASE_VERSION = 1
    const val COLUMN_ID = "id"

    //User
    const val TABLE_USERS = "users"

    //conversation
    const val TABLE_CONVERSATIONS = "conversations"
    const val KEY_CONVERSATION_ID = "conversationId"
    const val COLUMN_LAST_MESSAGE = "lastMessage"
    const val COLUMN_TIMESTAMP = "timestamp"

    //chat
    const val TABLE_CHATS = "chats"
    const val VIEW_TYPE_SENT = 1
    const val VIEW_TYPE_RECEIVED = 2

    //chat key
    const val KEY_SENDER_ID = "senderId"
    const val KEY_RECEIVER_ID = "receiverId"
    const val KEY_RECEIVER_IMAGE = "receiverImage"

    //home fragment
    const val VIEW_TYPE_CONVERSATIONS = 1
    const val VIEW_TYPE_FRIENDS = 2
}