package com.ppu.truface;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ai.trueface.sdk.core.ConfigurationOptions;
import ai.trueface.sdk.core.ErrorCode;
import ai.trueface.sdk.core.Faceprint;
import ai.trueface.sdk.core.SDK;
import ai.trueface.sdk.core.Similarity;

public class TrueFaceUtil {
    public static final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbW90aW9uIjpudWxsLCJmciI6bnVsbCwiYnlwYXNzX2dwdV91dWlkIjp0cnVlLCJwYWNrYWdlX2lkIjoiY29tLnBwdS50cnVmYWNlIiwiZXhwaXJ5X2RhdGUiOiIyMDIxLTA1LTEyIiwiZ3B1X3V1aWQiOltdLCJ0aHJlYXRfZGV0ZWN0aW9uIjpudWxsLCJtYWNoaW5lcyI6MSwiYWxwciI6bnVsbCwibmFtZSI6Ikd3YW4gQW4gVGFuIiwidGtleSI6Im5ldyIsImV4cGlyeV90aW1lX3N0YW1wIjoxNjIwNzc3NjAwLjAsImF0dHJpYnV0ZXMiOm51bGwsInR5cGUiOiJvZmZsaW5lIiwiZW1haWwiOiJ0Z2FAcHB1LmNvLmlkIn0.1lukDUPM7P4e3lym40KGgJQeg_leOyFPKLOsFYCXK9M";

    /**
     *
     * @param context
     * @param jpgFile
     * @param multiple true may slow. untested, may return duplicate name due to groupname
     * @return
     */
    public static String faceRecog(Context context, File jpgFile, float minScore, boolean multiple) {
        List<TrnEnroll> allPhoto = DataUtil.getAllEnroll(context);

        if (allPhoto.size() < 1)
            return "No Data. Try to Enroll First.";

        StopWatch sw = StopWatch.AutoStart();

        ConfigurationOptions options = new ConfigurationOptions();
        options.smallestFaceWidth = 40;

        try {
            SDK sdk = new SDK(context, options);

            sdk.setLicense(token);

            boolean isLicensed = sdk.isLicensed();

            if (!isLicensed)
                return "Sorry, Truface SDK is not licensed";

            ErrorCode errorCode = sdk.setImage(jpgFile.getPath());
            Log.i("truface", "errorCode.name=" + errorCode.name());

            if (errorCode != ErrorCode.NO_ERROR)
                return errorCode.toString();

            Faceprint face1 = sdk.getLargestFaceFeatureVector();

            if (face1 == null)
                return "Unable to get Vector of the face";

            List<String> people = new ArrayList<>();

            for (TrnEnroll e : allPhoto) {

                ErrorCode _ec = sdk.setImage(new File(e.getFileName()).getPath());

                if (_ec != ErrorCode.NO_ERROR) {
                    Log.e("truface", "error enroll = " + _ec.name() + " => " + e.toString());
                    continue;
                }

                Faceprint face2 = sdk.getLargestFaceFeatureVector();

                Similarity similarity = sdk.getSimilarity(face1, face2);

                if (similarity.similarityMeasure > minScore) {
                    if (multiple) {

                        boolean exist = false;
                        for (String _s : people){
                            if (_s.equalsIgnoreCase(e.getGroupName())){
                                exist = true;
                                break;
                            }
                        }
                        if (!exist)
                            people.add(e.getGroupName().toUpperCase());

                        Log.e("truface", "similarity=" + similarity.toString());
                    } else
                        return "HELLO, " + e.getGroupName().toUpperCase() + "\nScore:" + similarity.similarityMeasure + "\nInterval:"
                                + sw.stopAndGetAsString();
                }
            }

            if (people.size() > 0) {
                return "HELLO, " + android.text.TextUtils.join(",", people) + "\nInterval:"
                        + sw.stopAndGetAsString();
            }

            sw.stop();

            return "No Match";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        // return "Please check Console Log";
    }

    // slow
    // return UID, null otherwise
    public static String liveRecog(float minScore, SDK sdk, Faceprint face1, List<TrnEnroll> list) {

        for (TrnEnroll e : list) {

            ErrorCode _ec = sdk.setImage(new File(e.getFileName()).getPath());

            if (_ec != ErrorCode.NO_ERROR) {
                continue;
            }

            Faceprint face2 = sdk.getLargestFaceFeatureVector();

            Similarity similarity = sdk.getSimilarity(face1, face2);
            if (similarity.similarityMeasure > minScore)
                return e.getUid();
                // return "HELLO, " + e.getGroupName().toUpperCase() + "\nScore:" + similarity.similarityMeasure;
        }

        return null;
    }

    public static String liveRecogFast(float minScore, SDK sdk, Faceprint face1, List<FaceData> faces) {

        for (FaceData e : faces) {

            Similarity similarity = sdk.getSimilarity(face1, e.getFaceprint());
            if (similarity.similarityMeasure > minScore)
                return e.getUid();
                // return "HELLO, " + e.getGroupName().toUpperCase() + "\nScore:" + similarity.similarityMeasure;
        }

        return null;
    }
}
