package com.thiPNA219166.onlinechatapp.Model;

public class Message {

    private String chatID;
    private String sender;
    private String content;
    private String sendTime;

    public Message(){}
    public Message(String chatID,String sender,String content,String sendTime){
        this.sender = sender;
        this.content = content;
        this.sendTime = sendTime;
        this.chatID = chatID;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }
}
