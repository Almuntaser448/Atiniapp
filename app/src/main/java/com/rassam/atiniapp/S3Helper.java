package com.rassam.atiniapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import java.io.File;

public class S3Helper {

    public static void uploadFile(Context context, Uri fileUri, String bucketName, String key) {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                "YOUR_IDENTITY_POOL_ID", // Identity Pool ID
                Regions.YOUR_REGION // Region
        );

        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
        TransferUtility transferUtility = TransferUtility.builder()
                .context(context)
                .s3Client(s3Client)
                .build();

        TransferObserver uploadObserver = transferUtility.upload(bucketName, key, new File(fileUri.getPath()));

        uploadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    // Handle a completed upload.
                    Log.d("S3Helper", "Upload completed");
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDone = ((float) bytesCurrent / (float) bytesTotal) * 100;
                Log.d("S3Helper", "ID:" + id + " bytesCurrent: " + bytesCurrent + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
                Log.e("S3Helper", "Error during upload", ex);
            }
        });
    }
}
