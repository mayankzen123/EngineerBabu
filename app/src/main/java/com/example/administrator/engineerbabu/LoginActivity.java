package com.example.administrator.engineerbabu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import com.example.administrator.engineerbabu.Utility.Constants;
import com.example.administrator.engineerbabu.Utility.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Administrator on 9/23/2017.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    LoginButton mFacebookLogin;
    CallbackManager mCallbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_login);
        //FaceBook Login
        mFacebookLogin = (LoginButton) findViewById(R.id.btn_facebook);
        mCallbackManager = CallbackManager.Factory.create();
        mFacebookLogin.setOnClickListener(this);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            launchHomeScreen();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_facebook:
                fbLogin();
                break;
        }
    }

    private void fbLogin() {
        mFacebookLogin.setReadPermissions(Constants.FACEBOOK_EMAIL_KEY, Constants.FACEBOOK_PUBLIC_PROFILE_KEY);
        mFacebookLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            if (object != null && response.getConnection().getResponseCode() == 200) {
                                if (!object.has(Constants.FACEBOOK_EMAIL_KEY)) {
                                    onFacebookResponseReceived(Constants.RESPONSE_FAILED, getString(R.string.email_not_found), null);
                                } else
                                    onFacebookResponseReceived(Constants.RESPONSE_SUCCESS, "", object);
                            } else {
                                onFacebookResponseReceived(Constants.RESPONSE_FAILED, response.getError().getErrorMessage(), null);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString(Constants.FACEBOOK_FIELDS_KEY,
                        Constants.FACEBOOK_NAME_KEY + ","
                                + Constants.FACEBOOK_EMAIL_KEY + ","
                                + Constants.FACEBOOK_GENDER_KEY + ","
                                + Constants.FACEBOOK_PROFILE_KEY);
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                onFacebookResponseReceived(Constants.RESPONSE_FAILED, getString(R.string.denied), null);
            }

            @Override
            public void onError(FacebookException error) {
                onFacebookResponseReceived(Constants.RESPONSE_FAILED, error.getMessage(), null);
            }
        });
    }

    private void onFacebookResponseReceived(int responseSuccess, String msg, JSONObject object) {
        switch (responseSuccess) {
            case Constants.RESPONSE_SUCCESS:
                try {
                    Utils.setCredentialsInfo(this, object.getString(Constants.FACEBOOK_NAME_KEY), object.getString(Constants.FACEBOOK_EMAIL_KEY), object.getString(Constants.FACEBOOK_GENDER_KEY), object.getJSONObject(Constants.FACEBOOK_PICTURE_KEY).getJSONObject(Constants.FACEBOOK_DATA_KEY).getString(Constants.FACEBOOK_URL_KEY), "", false);
                    launchHomeScreen();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.RESPONSE_FAILED:
                Utils.showToast(this, msg);
                break;
        }
    }

    private void launchHomeScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}



