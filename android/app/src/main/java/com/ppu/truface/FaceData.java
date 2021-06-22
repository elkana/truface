package com.ppu.truface;

import ai.trueface.sdk.core.Faceprint;

public class FaceData {
    Faceprint faceprint;
    String uid;

    public Faceprint getFaceprint() {
        return faceprint;
    }

    public void setFaceprint(Faceprint faceprint) {
        this.faceprint = faceprint;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "FaceData{" +
                "faceprint=" + faceprint +
                ", uid='" + uid + '\'' +
                '}';
    }
}
