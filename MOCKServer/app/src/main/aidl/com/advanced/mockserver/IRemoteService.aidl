// IRemoteService.aidl
package com.advanced.mockserver;

// Declare any non-default types here with import statements
parcelable Conversation;
parcelable User;
parcelable ChatMessage;
interface IRemoteService {
   List<Conversation> getConversation();
   List<User> getUsers();
   List<ChatMessage> getChatMessages();
   void sendMessage(in ChatMessage message);
   void removeConversationById(in long conversationId);
   void removeUserById(in long userId);
   void updateConversation(in long conversationId, in String lastMessage ,in String timestamp);
}