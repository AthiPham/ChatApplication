package com.thiPNA219166.onlinechatapp.Model;

public class Group {
    private String groupID;
    private String name;
    private String leaderName;
    private int numberOfMember;  // member luu rieng tai "/User Group/userID/"

    public Group(){}
    public Group(String groupID, String name, String leaderName, int numberOfMember){
        this.groupID = groupID;
        this.name = name;
        this.leaderName = leaderName;
        this.numberOfMember = numberOfMember;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public int getNumberOfMember() {
        return numberOfMember;
    }

    public void setNumberOfMember(int numberOfMember) {
        this.numberOfMember = numberOfMember;
    }

}
