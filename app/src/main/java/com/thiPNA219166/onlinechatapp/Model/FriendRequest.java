package com.thiPNA219166.onlinechatapp.Model;

public class FriendRequest {
    public static final int REQUEST_WAITING = 0;
    public static final int REQUEST_ACCEPT = 1;
    public static final int REQUEST_DENY = -1;

    private String requestID;
    private String senderName;
    private String receiverName;
    //private String content;
    private String sendTime;
    private int status;

    public FriendRequest(){}
    public FriendRequest(String requestID, String senderName, String receiverName, String sendTime, int status){
        this.requestID = requestID;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.sendTime = sendTime;
        this.status = status;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

