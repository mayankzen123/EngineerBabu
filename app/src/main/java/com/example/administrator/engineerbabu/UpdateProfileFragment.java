package com.example.administrator.engineerbabu;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.administrator.engineerbabu.Utility.Utils;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static android.R.attr.phoneNumber;

/**
 * Created by Administrator on 9/23/2017.
 */

public class UpdateProfileFragment extends Fragment implements View.OnClickListener {
    Button btnMobile, btnUpdate;
    TextInputLayout emailLayout, nameLayout, mobileLayout;
    EditText email, name, mobile;
    ImageView done;
    RoundedImageView profileImage;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    ProgressDialog mProgressDialog;
    boolean mobileVerified = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activty_update_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        btnMobile = (Button) view.findViewById(R.id.btn_verify);
        btnUpdate = (Button) view.findViewById(R.id.btn_update);
        emailLayout = (TextInputLayout) view.findViewById(R.id.input_layout_email);
        nameLayout = (TextInputLayout) view.findViewById(R.id.input_layout_name);
        mobileLayout = (TextInputLayout) view.findViewById(R.id.mobile_layout_name);
        profileImage = (RoundedImageView) view.findViewById(R.id.profile);
        done = (ImageView) view.findViewById(R.id.done);
        email = (EditText) view.findViewById(R.id.input_email);
        name = (EditText) view.findViewById(R.id.input_name);
        mobile = (EditText) view.findViewById(R.id.input_mobile);
        mProgressDialog = new ProgressDialog(getActivity());
        btnMobile.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        showProfileInfo();
    }

    private void showProfileInfo() {
        SharedPreferences sharedPreferences = Utils.getCredentialsInfo(getActivity());
        if (sharedPreferences != null) {
            Picasso.with(getActivity())
                    .load(sharedPreferences.getString(Utils.PREF_PROFILE, ""))
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(profileImage);
            name.setText(sharedPreferences.getString(Utils.PREF_NAME, "N/A"));
            email.setText(sharedPreferences.getString(Utils.PREF_EMAIL, "N/A"));
            mobile.setText(sharedPreferences.getString(Utils.PREF_MOBILE, ""));
            if (sharedPreferences.getBoolean(Utils.PREF_VERFIY, false)) {
                btnMobile.setVisibility(View.GONE);
                done.setVisibility(View.VISIBLE);
                mobile.setEnabled(false);
                mobileVerified = true;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_verify:
                if (!TextUtils.isEmpty(mobile.getText().toString().trim())) {
                    mobileLayout.setErrorEnabled(false);
                    mProgressDialog.setMessage("Please wait...");
                    mProgressDialog.show();
                    mProgressDialog.setCancelable(false);
                    verfiyMobileNumber(mobile.getText().toString().trim());
                } else {
                    mobileLayout.setError("Please enter Mobile Number");
                }
                break;
            case R.id.btn_update:
                updateProfile();
                break;
        }
    }


    private void updateProfile() {
        boolean isVaild = true;
        if (TextUtils.isEmpty(email.getText().toString().trim())) {
            emailLayout.setError("Please Enter Email Id");
            isVaild = false;
        }
        if (TextUtils.isEmpty(name.getText().toString().trim())) {
            nameLayout.setError("Please Enter Name");
            isVaild = false;
        }
        if (isVaild) {
            nameLayout.setErrorEnabled(false);
            emailLayout.setErrorEnabled(false);
            mobileLayout.setErrorEnabled(false);
            if (isValidEmail(email.getText().toString().trim())) {
                if (mobileVerified) {
                    SharedPreferences sharedPreferences = Utils.getCredentialsInfo(getActivity());
                    Utils.setCredentialsInfo(getActivity(), name.getText().toString(), email.getText().toString(), sharedPreferences.getString(Utils.PREF_GENDER, ""), sharedPreferences.getString(Utils.PREF_PROFILE, ""), mobile.getText().toString(), mobileVerified);
                    ((MainActivity) getActivity()).setHeaderContents();
                    Utils.showToast(getActivity(), "Profile Updated");
                } else {
                    mobileLayout.setError("Please Verify Mobile Number");
                }
            } else {
                emailLayout.setError("Please Enter Valid Email Id");
            }
        }


    }

    private void verfiyMobileNumber(final String mobileNumber) {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                mProgressDialog.dismiss();
                btnMobile.setVisibility(View.GONE);
                done.setVisibility(View.VISIBLE);
                mobile.setEnabled(false);
                mobileVerified = true;
                Utils.showToast(getActivity(), "Mobile Number Verified");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                mProgressDialog.dismiss();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Utils.showToast(getActivity(), "Invalid Number");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Utils.showToast(getActivity(), "Too many Request Sent");
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                mProgressDialog.dismiss();
                mProgressDialog.setMessage("Verifying... Please Wait ");
                mProgressDialog.show();
                Utils.showToast(getActivity(), "Code Sent");
            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobileNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks);
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
