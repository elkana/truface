package com.ppu.truface;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.File;
import java.util.Date;
import java.util.List;

import ai.trueface.sdk.core.BoundingBox;
import ai.trueface.sdk.core.ConfigurationOptions;
import ai.trueface.sdk.core.ErrorCode;
import ai.trueface.sdk.core.Faceprint;
import ai.trueface.sdk.core.SDK;
import ai.trueface.sdk.core.Similarity;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.realm.Realm;

public class MainActivity extends FlutterActivity {
    private final int CAMERA_REQUEST = 11;

    private static final String CHANNEL = "flutter.mc.channel";
    // private final String token =
    // "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbW90aW9uIjpudWxsLCJmciI6bnVsbCwiYnlwYXNzX2dwdV91dWlkIjp0cnVlLCJwYWNrYWdlX2lkIjpudWxsLCJleHBpcnlfZGF0ZSI6IjIwMjEtMDQtMzAiLCJncHVfdXVpZCI6W10sInRocmVhdF9kZXRlY3Rpb24iOm51bGwsIm1hY2hpbmVzIjoxLCJhbHByIjpudWxsLCJuYW1lIjoiR3dhbiBBbiBUYW4iLCJ0a2V5IjoibmV3IiwiZXhwaXJ5X3RpbWVfc3RhbXAiOjE2MTk3NDA4MDAuMCwiYXR0cmlidXRlcyI6bnVsbCwidHlwZSI6Im9mZmxpbmUiLCJlbWFpbCI6InRnYUBwcHUuY28uaWQifQ.LmoCnr2cSKTy4CSiCMzrblW3hMMAjQELMeKq7rW_7x4eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbW90aW9uIjpudWxsLCJmciI6bnVsbCwiYnlwYXNzX2dwdV91dWlkIjp0cnVlLCJwYWNrYWdlX2lkIjpudWxsLCJleHBpcnlfZGF0ZSI6IjIwMjEtMDQtMjIiLCJncHVfdXVpZCI6W10sInRocmVhdF9kZXRlY3Rpb24iOm51bGwsIm1hY2hpbmVzIjoxLCJhbHByIjpudWxsLCJuYW1lIjoiSmFtZXMgQ2xheXRvbiBCZWxsIiwidGtleSI6Im5ldyIsImV4cGlyeV90aW1lX3N0YW1wIjoxNjE5MDQ5NjAwLjAsImF0dHJpYnV0ZXMiOm51bGwsInR5cGUiOiJvZmZsaW5lIiwiZW1haWwiOiJqYW1lcy5iZWxsMUB3YWxtYXJ0LmNvbSJ9.XYjUde04mLQqZk_TurGZDqYCULaI1S_8vkSMloc-tzU";
    // private MethodChannel channel;

    // private final String token =
    // "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbW90aW9uIjpudWxsLCJmciI6bnVsbCwiYnlwYXNzX2dwdV91dWlkIjp0cnVlLCJwYWNrYWdlX2lkIjoiY29tLnBwdS50cnVmYWNlIiwiZXhwaXJ5X2RhdGUiOiIyMDIxLTA1LTEyIiwiZ3B1X3V1aWQiOltdLCJ0aHJlYXRfZGV0ZWN0aW9uIjpudWxsLCJtYWNoaW5lcyI6MSwiYWxwciI6bnVsbCwibmFtZSI6Ikd3YW4gQW4gVGFuIiwidGtleSI6Im5ldyIsImV4cGlyeV90aW1lX3N0YW1wIjoxNjIwNzc3NjAwLjAsImF0dHJpYnV0ZXMiOm51bGwsInR5cGUiOiJvZmZsaW5lIiwiZW1haWwiOiJ0Z2FAcHB1LmNvLmlkIn0.1lukDUPM7P4e3lym40KGgJQeg_leOyFPKLOsFYCXK9M";
    // private final String token =
    // "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbW90aW9uIjpudWxsLCJmciI6bnVsbCwiYnlwYXNzX2dwdV91dWlkIjp0cnVlLCJwYWNrYWdlX2lkIjoiY29tLnBwdS50cnVmYWNlIiwiZXhwaXJ5X2RhdGUiOiIyMDIwLTA1LTEyIiwiZ3B1X3V1aWQiOltdLCJ0aHJlYXRfZGV0ZWN0aW9uIjpudWxsLCJtYWNoaW5lcyI6MSwiYWxwciI6bnVsbCwibmFtZSI6Ikd3YW4gQW4gVGFuIiwidGtleSI6Im5ldyIsImV4cGlyeV90aW1lX3N0YW1wIjoxNTg5MjQxNjAwLjAsImF0dHJpYnV0ZXMiOm51bGwsInR5cGUiOiJvZmZsaW5lIiwiZW1haWwiOiJ0Z2FAcHB1LmNvLmlkIn0.U2BC-f1j0C5TsKhF2G17PA5W_rSOWD6iTYLATifS2h0";
    private MethodChannel channel;

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine);

        channel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL);

        channel.setMethodCallHandler((call, result) -> {

                    Log.w("truface", "RECEIVE method=" + call.method);
                    // Note: this method is invoked on the main thread.
                    if (call.method.equals("hello")) {
                        result.success("Hallo Juga");
                        // result.error("UNAVAILABLE", "Battery level not available.", null);
                    } else if (call.method.equals("sn")) {
                        // result.error("UNAVAILABLE", "Battery level not available.", null);
                    } else if (call.method.equals("toast")) {
                        Toast.makeText(getApplicationContext(), "Flutter Toast", Toast.LENGTH_LONG).show();
                        result.success("ok");

                    } else if (call.method.equals("native.camera")) {
                        showCamera();

                        result.success("ok");

                    } else if (call.method.equals("native.receiveFileFromFlutter")) {
                        String fileName = call.argument("file");

                        File file = new File(fileName);

                        result.success("ok");

                        // Toast.makeText(getApplicationContext(), "Receive File " + fileName +
                        // "\nExist=" + file.exists(),
                        // Toast.LENGTH_LONG).show();

                    } else if (call.method.equals("truface.check.license")) {
                        SDK sdk = new SDK(getApplicationContext());

                        sdk.setLicense(TrueFaceUtil.token);

                        boolean isLicensed = sdk.isLicensed();

                        if (!isLicensed)
                            result.error("UNAVAILABLE", "Not Licensed", null);
                        else
                            result.success("ok");

                        // Toast.makeText(getApplicationContext(), "Truface SDK is licensed=" +
                        // isLicensed,
                        // Toast.LENGTH_SHORT).show();
                    } else if (call.method.equals("truface.enroll.face")) {
                        String fileName = call.argument("file");
                        String groupName = call.argument("group");

                        File file = new File(fileName);

                        DataUtil.saveToDB(getApplicationContext(), file, groupName);

                        result.success("ok");

                        // Toast.makeText(getApplicationContext(), "Face " + groupName + " registered",
                        // Toast.LENGTH_SHORT)
                        // .show();
                    } else if (call.method.equals("truface.check.face")) {
                        String fileName = call.argument("file");
                        String score = call.argument("score.minimum");

                        float minScore = score == null || score.trim().length() < 1 || score.equals("null") ? 0.7f : Float.parseFloat(score);

                        File file = new File(fileName);
                        String ret = TrueFaceUtil.faceRecog(getApplicationContext(), file, minScore, true);

                        // Toast.makeText(getApplicationContext(), "Truface Check Face=" + ret,
                        // Toast.LENGTH_LONG).show();
                        result.success(ret);

                        // if (ret != null)
                        // result.error("UNAVAILABLE", ret, null);
                        // else result.success(true);
                        // else channel.invokeMethod("truface.result", imageUri.toString());
                    } else if (call.method.equals("truface.group.face")) {
                        String groupName = call.argument("group");
                        List<TrnEnroll> list = DataUtil.getEnrollList(getApplicationContext(), groupName);
                        Gson gson = new Gson();
                        String json = gson.toJson(list);
                        result.success(json);

                        // Toast.makeText(getApplicationContext(), "Truface Group Face ada " +
                        // list.size(),
                        // Toast.LENGTH_LONG).show();
                    } else if (call.method.equals("truface.all.face")) {
                        List<TrnEnroll> list = DataUtil.getEnrollList(getApplicationContext(), null);
                        Gson gson = new Gson();
                        String json = gson.toJson(list);
                        result.success(json);

                        // Toast.makeText(getApplicationContext(), "Truface Group Face ada " +
                        // list.size(),
                        // Toast.LENGTH_LONG).show();
                    } else if (call.method.equals("truface.clear")) {
                        DataUtil.clearAllEnroll(getApplicationContext());

                        result.success("ok");

                        // Toast.makeText(getApplicationContext(), "Truface DB Cleared",
                        // Toast.LENGTH_LONG).show();
                    } else if (call.method.equals("truface.delete.uid")) {
                        String uid = call.argument("uid");
                        DataUtil.deleteFromDB(getApplicationContext(), uid);

                        result.success("ok");

                        // Toast.makeText(getApplicationContext(), "Truface DB Cleared",
                        // Toast.LENGTH_LONG).show();
                    } else if (call.method.equals("truface.camera.live")) {
                        String score = call.argument("score.minimum");

                        float minScore = score == null || score.trim().length() < 1 || score.equals("null") ? 0.7f : Float.parseFloat(score);

                        Intent i = new Intent(getApplicationContext(), ActivityLive.class);
                        i.putExtra(ActivityLive.PARAM_SCORE, minScore);
                        startActivityForResult(i, ActivityLive.REQUEST_LIVE);

                        Log.e("trueface", " startActivityForResult(i, REQUEST_LIVE);");
                        result.success("...");
                    } else {
                        result.notImplemented();

                        Toast.makeText(getApplicationContext(), "Not Implemented", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && data != null && data.getExtras() != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            if (photo == null) {
                Toast.makeText(getApplicationContext(), "No Bitmap to read", Toast.LENGTH_LONG).show();
                return;
            }

            SDK sdk = new SDK(getApplicationContext());

            sdk.setLicense(TrueFaceUtil.token);

            boolean isLicensed = sdk.isLicensed();

            if (isLicensed) {
                ErrorCode errorCode = sdk.setImage(photo);
                BoundingBox[] boundingBoxs = sdk.detectObjects();
                for (BoundingBox boundingBox : boundingBoxs) {
                    Log.i("truface", boundingBox.toString());
                }
                Toast.makeText(getApplicationContext(), "With License.\nDetect Photo, ErrorCode=" + errorCode.toString()
                        + "\nBoundingBox=" + boundingBoxs.length, Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(getApplicationContext(),
                        "No License.\nReceived Bitmap Photo. Height =" + photo.getHeight(), Toast.LENGTH_LONG).show();
        } else if (requestCode == ActivityLive.REQUEST_LIVE) {

            if (data == null)
                return;

            String uid = data.getStringExtra(ActivityLive.EXTRA_FACE_UID_SUCCESS);
                // String fileName = data.getStringExtra(ActivityLive.EXTRA_FACE_FILE_SUCCESS);

            if (uid == null || uid.trim().length() < 1)
                return;

            TrnEnroll row = DataUtil.getEnroll(getApplicationContext(), uid);

            if (row == null) return;

            Gson gson = new Gson();
            String json = gson.toJson(row);

            channel.invokeMethod("truface.camera.live.result", json);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public interface AsyncResponse {
        void processFinish(String output);
    }

    private class PostTask extends AsyncTask<File, Integer, String> {

        ProgressDialog dialog;
        AsyncResponse delegate = null;
        boolean multiple;
        float minScore;

        public PostTask(boolean multiple, float minScore, AsyncResponse delegate) {
            this.delegate = delegate;
            this.multiple = multiple;
            this.minScore = minScore;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getApplicationContext());
            dialog.setMessage("please wait...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(File... params) {

            List<TrnEnroll> allEnroll = DataUtil.getAllEnroll(getApplicationContext());

            if (allEnroll.size() < 1)
                return "No DATA> Try to Enroll first";

            File file = params[0];

            if (!file.exists())
                return "Photo File not exists";

            // TODO do scan here
            String ret = TrueFaceUtil.faceRecog(getApplicationContext(), file, minScore, multiple);

            return ret;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();

            delegate.processFinish(result);
        }
    }
}
