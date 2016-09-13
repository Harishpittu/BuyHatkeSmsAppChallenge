package com.harish.buyhatkesmschallenge.models;

/**
 * Created by harish on 13/09/16.
 */
public class SMSUserObject implements Comparable<SMSUserObject>{
    private String name;
    private String lastMsg;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public int compareTo(SMSUserObject compareSMSUserObject) {

        int compareId= ((SMSUserObject) compareSMSUserObject).getId();

        //ascending order
//        return this.quantity - compareQuantity;

        //descending order
        return compareId - this.id;

    }
}
