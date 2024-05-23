package com.rassam.atiniapp;

import android.app.Application;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3Client;

public class AWSProvider extends Application {

    private static CognitoCachingCredentialsProvider credentialsProvider;
    private static DynamoDBMapper dynamoDBMapper;
    private static TransferUtility transferUtility;

    @Override
    public void onCreate() {
        super.onCreate();

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "YOUR_IDENTITY_POOL_ID", // Identity Pool ID
                Regions.YOUR_REGION // Region
        );

        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);
        dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
        transferUtility = TransferUtility.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .s3Client(s3Client)
                .build();
    }

    public static DynamoDBMapper getDynamoDBMapper() {
        return dynamoDBMapper;
    }

    public static TransferUtility getTransferUtility() {
        return transferUtility;
    }
}
