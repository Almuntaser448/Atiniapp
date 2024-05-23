package com.rassam.atiniapp;

import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.rassam.atiniapp.models.Item;
import com.rassam.atiniapp.models.User;

import java.util.List;

public class DynamoDBHelper {

    private static final DynamoDBMapper dynamoDBMapper = AWSProvider.getDynamoDBMapper();

    public static void saveItem(final Item item) {
        new Thread(() -> {
            try {
                dynamoDBMapper.save(item);
                Log.d("DynamoDBHelper", "Item saved");
            } catch (Exception e) {
                Log.e("DynamoDBHelper", "Error saving item", e);
            }
        }).start();
    }

    public static void saveUser(final User user) {
        new Thread(() -> {
            try {
                dynamoDBMapper.save(user);
                Log.d("DynamoDBHelper", "User saved");
            } catch (Exception e) {
                Log.e("DynamoDBHelper", "Error saving user", e);
            }
        }).start();
    }

    public static List<Item> getUserFavorites(String userId) {
        try {
            User user = dynamoDBMapper.load(User.class, userId);
            return user != null ? user.getFavorites() : null;
        } catch (Exception e) {
            Log.e("DynamoDBHelper", "Error retrieving user favorites", e);
            return null;
        }
    }
}
