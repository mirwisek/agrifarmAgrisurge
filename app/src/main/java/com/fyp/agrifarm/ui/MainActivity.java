package com.fyp.agrifarm.ui;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.fyp.agrifarm.repo.DownloadNews;
import com.fyp.agrifarm.repo.NewsViewModel;
import com.fyp.agrifarm.R;
import com.fyp.agrifarm.YoutubeMakeRequest;
import com.fyp.agrifarm.beans.ShortVideo;
import com.fyp.agrifarm.ui.custom.VideoRecyclerAdapter;
import com.fyp.agrifarm.utils.FirebaseUtils;
import com.fyp.agrifarm.utils.PicassoUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.navigation.NavigationView;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks,
        NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener,
        WeatherFragment.OnFragmentInteractionListener,
        VideoRecyclerAdapter.OnItemClickListener {

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { YouTubeScopes.YOUTUBE_READONLY };

    GoogleAccountCredential mCredential;
    ProgressDialog mProgress;

    public static final String TAG = "MainActivity";
    private static HomeFragment homeFragment = null;
    private static WeatherFragment weatherFragment = null;
    private NewsViewModel newsViewModel;
    private static MainActivity mainActivityobj;
    private static Context appContext;
    private FrameLayout progressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivityobj=this;
        appContext = getApplicationContext();
        if (new NewsViewModel((Application) MainActivity.getAppContext()).getAllNotes()!=null){
            new NewsViewModel((Application)MainActivity.getAppContext()).deleteAllNotes();
        }
        new DownloadNews().execute();

        progressLayout = findViewById(R.id.progress_overlay);
        progressLayout.setVisibility(View.VISIBLE);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        if (headerView == null) {
            headerView = navigationView.inflateHeaderView(R.layout.drawer_header);
        }
        TextView tvUserFullName = headerView.findViewById(R.id.tvDrawerName);
        TextView tvUserOccupation = headerView.findViewById(R.id.tvDrawerOccupation);
        ImageView uProfilePhoto = headerView.findViewById(R.id.ivDrawerProfile);

        // Turned off for debugging purpose
        FirebaseUtils.fetchCurrentUserFromFirebase(user -> {
            tvUserFullName.setText(user.getFullname());
            tvUserOccupation.setText(user.getOccupation());
//            uAge.setText(userAge);
//            uLocation.setText(userLocation);
            // TODO: The image is returned with a bit margin in left, TO FIX, ScaleType: CenterCrop clips the image instead
            PicassoUtils.loadCropAndSetImage(user.getPhotoUri(), uProfilePhoto, getResources());
            progressLayout.setVisibility(View.GONE);
            getResultsFromApi();
        });

        mCredential = GoogleAccountCredential.usingOAuth2(
                this, Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());


    }
    //  TODO: Remove the following 2 methods causing memory leaks
    public static MainActivity getactivity(){
        return mainActivityobj;
    }
    public static Context getAppContext() { return appContext; }

    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Toast.makeText(getApplicationContext(), "No network connection available", Toast.LENGTH_SHORT).show();
        } else {

            new YoutubeMakeRequest.MakeRequestTask(
                    getApplicationContext(), progressLayout, mCredential,
                    new YoutubeMakeRequest.MakeRequestTask.ResponseListener() {
                        @Override
                        public void onCancelled(Exception mLastError) {
                            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                                showGooglePlayServicesAvailabilityErrorDialog(
                                        ((GooglePlayServicesAvailabilityIOException) mLastError)
                                                .getConnectionStatusCode());
                            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                                startActivityForResult(
                                        ((UserRecoverableAuthIOException) mLastError).getIntent(),
                                        MainActivity.REQUEST_AUTHORIZATION);
                            } else {
                                Log.e(TAG, "getResultsFromApi: " + mLastError.getMessage() );
                                Toast.makeText(getApplicationContext(), "The following error occurred:\n"
                                        + mLastError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onVideosFetched(List<ShortVideo> videoList) {
                            // Load HomeFragment upon fetching videos
                            FragmentManager fm = getSupportFragmentManager();
                            Fragment fragment = fm.findFragmentByTag(HomeFragment.TAG);
                            if (fragment == null) {
                                if (homeFragment == null)
                                    homeFragment = new HomeFragment();
                                fragment = homeFragment;
                                fm.beginTransaction()
                                        .disallowAddToBackStack()
                                        .replace(R.id.fragmentHolder, fragment, HomeFragment.TAG)
                                        .runOnCommit(() -> homeFragment.updateAdapter(videoList))
                                        .commit();

                            }
                        }
                    }
            ).execute();
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = Objects.requireNonNull(connMgr).getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }


    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.", Toast.LENGTH_LONG).show();
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // Change orientation back to Portrait, when back from fullscreen video
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            FirebaseUtils.deleteAccount( o -> {
                        Log.i(TAG, "onOptionsItemSelected: Deleted");
                        Toast.makeText(this, "Account Deleted!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, UserRegistrationActivity.class));
                        finish();
                    },
                    e -> Toast.makeText(this, "Account could not be deleted!", Toast.LENGTH_SHORT).show()
            );

            return true;
        } else if (id == R.id.action_sign_out) {
            FirebaseUtils.signOut(this, () -> {
                startActivity(new Intent(MainActivity.this, UserRegistrationActivity.class));
                finish();
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onForecastClick(View v) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(WeatherFragment.TAG);
        if (fragment == null) {
            if (weatherFragment == null)
                weatherFragment = WeatherFragment.newInstance("a", "b");
            fragment = weatherFragment;
        }

        fm.beginTransaction()
                .replace(R.id.fragmentHolder, fragment, WeatherFragment.TAG)
                .addToBackStack(HomeFragment.TAG)
                .commit();
    }

    @Override
    public void onVideoClicked(View v, final String videoUrl) {
        // TODO: Refactor YOUTUBE FRAGMENT: AndroidX
        YoutubeFragment youtubeFragment = YoutubeFragment.newInstance(videoUrl, "b");

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentHolder, youtubeFragment, YoutubeFragment.TAG)
                .addToBackStack(HomeFragment.TAG)
                .commit();
    }

    @Override
    public void onItemClick(View v) {

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

}
