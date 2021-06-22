package com.ppu.truface;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.otaliastudios.cameraview.internal.RotationHelper;
import com.otaliastudios.cameraview.size.Size;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import ai.trueface.sdk.core.ColorCode;
import ai.trueface.sdk.core.ConfigurationOptions;
import ai.trueface.sdk.core.ErrorCode;
import ai.trueface.sdk.core.Faceprint;
import ai.trueface.sdk.core.SDK;

import io.flutter.embedding.android.FlutterActivity;

public class ActivityLive extends FlutterActivity {
    public static String EXTRA_FACE_FILE_SUCCESS = "face.success.file";
    public static String EXTRA_FACE_UID_SUCCESS = "face.success.uid";

    public static final String PARAM_SCORE = "score.float";

    public static final int REQUEST_LIVE = 88;

    SDK tfsdk;
    float score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.score = extras.getFloat(PARAM_SCORE, 0.7f);        // bisa kosong di paymententri
        }


        setTitle("Live Camera");
        // if (getSupportActionBar() != null) {
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // }

        final TextView instruction = findViewById(R.id.instruction);

        ConfigurationOptions options = new ConfigurationOptions();
        options.smallestFaceWidth = 240;
        // options.enableGPU = true;
        // options.deviceIndex = 0;

        tfsdk = new SDK(getApplicationContext(), options);
        tfsdk.setLicense(TrueFaceUtil.token);

        final CameraView camera = findViewById(R.id.camera);
        camera.setFacing(Facing.FRONT);

        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        surfaceView.setBackgroundColor(Color.TRANSPARENT);
        surfaceView.setZOrderOnTop(true);
        final SurfaceHolder holder = surfaceView.getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);

        final Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setTextSize(20);

        List<TrnEnroll> list = DataUtil.getAllEnroll(getApplicationContext());

        if (list == null || list.size() < 1) {
            instruction.setText("Try to enroll first.");
            return;
        }

        List<FaceData> faces = new ArrayList<>();
        for (TrnEnroll trnEnroll : list){
            ErrorCode _ec = tfsdk.setImage(new File(trnEnroll.getFileName()).getPath());

            if (_ec != ErrorCode.NO_ERROR) {
                continue;
            }

            Faceprint face2 = tfsdk.getLargestFaceFeatureVector();

            FaceData fd = new FaceData();
            fd.setFaceprint(face2);
            fd.setUid(trnEnroll.getUid());
            faces.add(fd);
        }

        camera.addFrameProcessor(new FrameProcessor() {
            @Override
            @WorkerThread
            public void process(@NonNull Frame frame) {
                Size size = frame.getSize();
                int userRotation = frame.getRotationToUser();

                if (frame.getDataClass() == byte[].class) {
                    byte[] data = frame.getData();
                    if (userRotation == 90) {
                        data = RotationHelper.rotate(data, size, 90);
                    }
                    if (userRotation == 270) {
                        data = RotationHelper.rotate(data, size, 270);
                    }

                    final long startTime = System.currentTimeMillis();
                    ErrorCode ec = tfsdk.setImage(size.getWidth(), size.getHeight(), data, ColorCode.yuv_nv12);
                    final Faceprint face = tfsdk.getLargestFaceFeatureVector();
                    // final FaceBoxAndLandmarks face = tfsdk.detectLargestFace();
                    String uid = null;
                    if (ec == ErrorCode.NO_ERROR) {
                        uid = TrueFaceUtil.liveRecogFast(score, tfsdk, face, faces);
                    }
                    final long endTime = System.currentTimeMillis();
                    //
//                    byte[] finalData = data;
                    String finalUid = uid;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (face != null && finalUid != null) {

//                                instruction.setText(finalRet);
                                // instruction.setText("face detected: " + (endTime - startTime) + "ms => " +
                                // finalRet);

                                // try {
                                // File cacheFile = File.createTempFile("tmp", ".jpg");
                                // // clean up the last one
                                // if (cacheFile.exists())
                                // cacheFile.delete();
                                //
                                // // must create file first
                                // cacheFile.getParentFile().mkdirs();
                                // cacheFile.createNewFile();
                                //
                                // Log.e("trueface", "Saved file -> " + cacheFile.getPath());
                                // // 1 convert byte to file
                                // byte[] data2 = NV21toJPEG(finalData, size.getWidth(), size.getHeight(), 100);
                                //
                                // FileOutputStream fos = new FileOutputStream(cacheFile);
                                // fos.write(data2);
                                // fos.close();
                                //
                                Intent dataReturn = new Intent();
                                dataReturn.putExtra(EXTRA_FACE_UID_SUCCESS, finalUid);                                
                                setResult(RESULT_OK, dataReturn);
                                finish();
                                //
                                // } catch (IOException e) {
                                // e.printStackTrace();
                                // }

                            } else {
                                instruction.setText("face unknown : " + (endTime - startTime) + "ms");
                            }
                        }
                    });

                }
            }
        });

        camera.setLifecycleOwner(this);
        // camera.setLifecycleOwner(getApplicationContext());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // https://stackoverflow.com/questions/44022062/converting-yuv-420-888-to-jpeg-and-saving-file-results-distorted-image
    private static byte[] NV21toJPEG(byte[] nv21, int width, int height, int quality) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // YuvImage yuv = new YuvImage(nv21, ImageFormat.YUY2, width, height, null);
        YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        yuv.compressToJpeg(new Rect(0, 0, width, height), quality, out);
        return out.toByteArray();
    }

    private static byte[] YUV420toNV21(Image image) {
        Rect crop = image.getCropRect();
        int format = image.getFormat();
        int width = crop.width();
        int height = crop.height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];

        int channelOffset = 0;
        int outputStride = 1;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
            case 0:
                channelOffset = 0;
                outputStride = 1;
                break;
            case 1:
                channelOffset = width * height + 1;
                outputStride = 2;
                break;
            case 2:
                channelOffset = width * height;
                outputStride = 2;
                break;
            }

            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();

            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            for (int row = 0; row < h; row++) {
                int length;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
        }
        return data;
    }
}