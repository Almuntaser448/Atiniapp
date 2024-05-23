package com.rassam.atiniapp;

import android.os.AsyncTask;
import android.util.Log;
import com.rassam.atiniapp.models.User;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

public class DynamoDBHelper {

    public static void saveUser(final User user) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    DynamoDBMapper dynamoDBMapper = AWSProvider.getDynamoDBMapper();
                    dynamoDBMapper.save(user);
                } catch (Exception e) {
                    Log.e("DynamoDBHelper", "Failed to save user", e);
                }
                return null;
            }
        }.execute();
    }
}
