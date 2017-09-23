package com.example.administrator.engineerbabu.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 9/22/2017.
 */

public class Utils {
    public static SharedPreferences.Editor editor;
    public static final String PREF_NAME = "full_name";
    public static final String PREF_EMAIL = "email";
    public static final String PREF_GENDER = "gender";
    public static final String PREF_PROFILE = "profile_image";
    public static final String PREF_VERFIY = "verfiy";
    public static final String PREF_MOBILE = "mobile";


    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void setCredentialsInfo(Context context, String name, String emailId, String gender, String url, String mobile, boolean verify) {
        editor = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
        editor.putString(PREF_NAME, name);
        editor.putString(PREF_EMAIL, emailId);
        editor.putString(PREF_GENDER, gender);
        editor.putString(PREF_PROFILE, url);
        editor.putString(PREF_MOBILE, mobile);
        editor.putBoolean(PREF_VERFIY, verify);
        editor.commit();
    }

    public static SharedPreferences getCredentialsInfo(Context context) {
        return context.getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
    }
}
