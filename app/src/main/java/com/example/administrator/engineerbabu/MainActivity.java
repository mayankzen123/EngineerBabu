package com.example.administrator.engineerbabu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.administrator.engineerbabu.Utility.Utils;
import com.facebook.login.LoginManager;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    View header;
    RoundedImageView mProfile;
    TextView mName, mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        mProfile = (RoundedImageView) header.findViewById(R.id.img_profile);
        mEmail = (TextView) header.findViewById(R.id.txt_email);
        mName = (TextView) header.findViewById(R.id.txt_name);

    }

    public void setHeaderContents() {
        SharedPreferences sharedPreferences = Utils.getCredentialsInfo(this);
        if (sharedPreferences != null) {
            mEmail.setText(sharedPreferences.getString(Utils.PREF_EMAIL, "N/A"));
            mName.setText(sharedPreferences.getString(Utils.PREF_NAME, "N/A"));
            Picasso.with(this)
                    .load(sharedPreferences.getString(Utils.PREF_PROFILE, ""))
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(mProfile);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setHeaderContents();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            LoginManager.getInstance().logOut();
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_my_location) {
            MyLocationFragment myLocationFragment = new MyLocationFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, myLocationFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.nav_profile) {
            UpdateProfileFragment updateProfileFragment = new UpdateProfileFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, updateProfileFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.nav_about_us) {
            WebViewFragment webViewFragment = new WebViewFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, webViewFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.nav_logout) {
            LoginManager.getInstance().logOut();
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
