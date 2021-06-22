package com.ppu.truface;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TrnEnroll extends RealmObject implements Serializable {
    @PrimaryKey
    @SerializedName("uid")
    private String uid;

    @SerializedName("startDate")
    private Long createdDate;

    @SerializedName("fileName")
    private String fileName;

    @SerializedName("groupName")
    private String groupName;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "TrnEnroll{" + "uid='" + uid + '\'' + ", createdDate=" + createdDate + ", fileName='" + fileName + '\''
                + ", groupName='" + groupName + '\'' + '}';
    }
}
