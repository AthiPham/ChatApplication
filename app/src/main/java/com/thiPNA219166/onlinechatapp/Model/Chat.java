package com.thiPNA219166.onlinechatapp.Model;

public class Chat {
    private String chatID;
    private String groupName;
    private String user1Name;
    private String user2Name;
   // private String user1_user2;

    private boolean groupChat;
    private String lastMessageContent;
    private String lastSendTime;


    public Chat(){}
    public Chat(String chatID, String groupName, String lastMessageContent, String lastSendTime){
        this.chatID = chatID;
        this.groupName = groupName;
        this.lastMessageContent = lastMessageContent;
        this.lastSendTime = lastSendTime;
        this.groupChat = true;
    }
    public Chat(String chatID, String user1Name, String user2Name, String lastMessageContent, String lastSendTime){
        if (user1Name.compareTo(user2Name)<=0) { //ten nao nho hon la user1, ten lon la user2
            this.user1Name = user1Name;
            this.user2Name = user2Name;
          //  this.user1_user2 = user1Name + "_" + user2Name;

        } else {
            this.user1Name = user2Name;
            this.user2Name = user1Name;
           // this.user1_user2 = user2Name + "_" + user1Name;
        }
        this.chatID = chatID;
        this.lastMessageContent = lastMessageContent;
        this.lastSendTime = lastSendTime;
        this.groupChat = false;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUser1Name() {
        return user1Name;
    }

    public void setUser1Name(String user1Name) {
        this.user1Name = user1Name;
    }
    public String getUser2Name() {
        return user2Name;
    }

    public void setUser2Name(String user2Name) {
        this.user2Name = user2Name;
    }

   // public String getUser1_user2() {return user1_user2;}

   // public void setUser1_user2(String user1_user2) {this.user1_user2 = user1_user2;}

    public String getLastMessageContent() {
        return lastMessageContent;
    }

    public void setLastMessageContent(String lastMessageContent) {
        this.lastMessageContent = lastMessageContent;
    }

    public String getLastSendTime() {
        return lastSendTime;
    }

    public void setLastSendTime(String lastSendTime) {
        this.lastSendTime = lastSendTime;
    }


    public boolean isGroupChat(){
        return groupChat;
    }

    public void setGroupChat(boolean groupChat) {
        this.groupChat = groupChat;
    }

    public String getAnotherUserName(String me){
        if (!isGroupChat()) {
            if (me.equals(user1Name)) {
                return user2Name;
            } else {
                return user1Name;
            }
        } else return "";
    }

    /*
    public static String findUser1_User2(String user1, String user2){
        String user1_user2;
        if (user1.compareTo(user2)<=0){
            user1_user2 = user1+"_"+ user2;
        } else {
            user1_user2 = user2 + "_" +user1;
        }
        return user1_user2;
    }

     */

}
