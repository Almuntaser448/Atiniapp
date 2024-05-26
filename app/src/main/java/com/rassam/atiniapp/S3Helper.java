//package com.rassam.atiniapp;
//
//import android.content.Context;
//import android.net.Uri;
//import android.util.Log;
//
////import com.amazonaws.mobile.client.AWSMobileClient;
////import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
////import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
////import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
//
//import java.io.File;
//
//public class S3Helper {
//
//    public static void uploadFile(Context context, Uri uri, String bucketName, String key) {
//        TransferUtility transferUtility = TransferUtility.builder()
//                .context(context)
//                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
//                .build();
//
//        transferUtility.upload(bucketName, key, new File(uri.getPath()))
//                .setTransferListener(new TransferListener() {
//                    @Override
//                    public void onStateChanged(int id, TransferState state) {
//                        if (state == TransferState.COMPLETED) {
//                            Log.d("S3Helper", "File upload completed");
//                        } else if (state == TransferState.FAILED) {
//                            Log.e("S3Helper", "File upload failed");
//                        }
//                    }
//
//                    @Override
//                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//                        float percentDone = ((float) bytesCurrent / (float) bytesTotal) * 100;
//                        Log.d("S3Helper", "Progress: " + percentDone + "%");
//                    }
//
//                    @Override
//                    public void onError(int id, Exception ex) {
//                        Log.e("S3Helper", "Error: ", ex);
//                    }
//                });
//    }
//}
