package com.fyp.agrifarm.app

import android.Manifest
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.fyp.agrifarm.R
import com.fyp.agrifarm.app.HomeFragment.OnFragmentInteractionListener
import com.fyp.agrifarm.app.profile.model.User
import com.fyp.agrifarm.app.weather.ui.WeatherFragment
import com.fyp.agrifarm.app.youtube.VideoRecyclerAdapter
import com.fyp.agrifarm.app.youtube.YoutubeFragment
import com.fyp.agrifarm.app.youtube.YoutubeMakeRequest.MakeRequestTask
import com.fyp.agrifarm.app.youtube.YoutubeMakeRequest.MakeRequestTask.ResponseListener
import com.fyp.agrifarm.app.youtube.db.ShortVideo
import com.fyp.agrifarm.app.youtube.viewmodel.VideoSharedViewModel
import com.fyp.agrifarm.utils.FirebaseUtils
import com.fyp.agrifarm.utils.PicassoUtils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.navigation.NavigationView
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.youtube.YouTubeScopes
import com.google.firebase.auth.FirebaseAuth
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks
import java.lang.ref.WeakReference
import java.util.*

class MainActivity : AppCompatActivity(),
        PermissionCallbacks,
        NavigationView.OnNavigationItemSelectedListener,
        VideoRecyclerAdapter.OnItemClickListener, OnFragmentInteractionListener , PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private val apiAvailability = GoogleApiAvailability.getInstance()
    private lateinit var mCredential: GoogleAccountCredential
    private lateinit var videoViewModel: VideoSharedViewModel

    companion object {
        const val REQUEST_ACCOUNT_PICKER = 1000
        const val REQUEST_AUTHORIZATION = 1001
        const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
        const val REQUEST_PERMISSION_GET_ACCOUNTS = 1003
        private const val PREF_ACCOUNT_NAME = "accountName"
        private val SCOPES = arrayOf(YouTubeScopes.YOUTUBE_READONLY)
        const val TAG = "MainActivity"
        private var homeFragment: HomeFragment? = null
        private  var weatherFragment:WeatherFragment? = null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        var headerView = navigationView.getHeaderView(0)
        if (headerView == null) {
            headerView = navigationView.inflateHeaderView(R.layout.drawer_header)
        }
        val tvUserFullName = headerView!!.findViewById<TextView>(R.id.tvDrawerName)
        val tvUserOccupation = headerView.findViewById<TextView>(R.id.tvDrawerOccupation)
        val uProfilePhoto = headerView.findViewById<ImageView>(R.id.ivDrawerProfile)

        // Turned off for debugging purpose
        FirebaseUtils.fetchCurrentUserFromFirebase { user: User ->
            tvUserFullName.text = user.fullname
            tvUserOccupation.text = user.occupation
            //            uAge.setText(userAge);
//            uLocation.setText(userLocation);
            // TODO: The image is returned with a bit margin in left, TO FIX, ScaleType: CenterCrop clips the image instead
            PicassoUtils.loadCropAndSetImage(user.photoUri, uProfilePhoto, resources)
            resultsFromApi()
        }
        val fm = supportFragmentManager
        var fragment = fm.findFragmentByTag(HomeFragment.TAG)
        if (fragment == null) {
            if (homeFragment == null) homeFragment = HomeFragment()
            fragment = homeFragment
            fm.beginTransaction()
                    .disallowAddToBackStack()
                    .replace(R.id.fragmentHolder, fragment!!, HomeFragment.TAG)
                    .commit()
        }
        videoViewModel = ViewModelProvider(this).get(VideoSharedViewModel::class.java)
        mCredential = GoogleAccountCredential.usingOAuth2(this, Arrays.asList(*SCOPES))
                .setBackOff(ExponentialBackOff())

        // Set Youtube account to be that of the one logged in with
        val user = FirebaseAuth.getInstance().currentUser
        // Also make sure if user signs in through phone then don't set account without an email
        if (user != null && user.email != null && !user.email!!.isEmpty()) {
            mCredential.selectedAccountName = user.email
        } else {
            // Not called in OTP Login, so we have to call it to configure YouTube account
            resultsFromApi()
        }
    }

    // Cache into the database for ROOM
    private fun resultsFromApi() {
        if (!isGooglePlayServicesAvailable) {
            acquireGooglePlayServices()
        } else if (mCredential.selectedAccountName == null) {
            chooseAccount()
        } else if (!isDeviceOnline) {
            toast("No network connection available")
        } else {
            MakeRequestTask(
                    WeakReference(application), mCredential,
                    object : ResponseListener {
                        override fun onCancelled(mLastError: Exception) {
                            if (mLastError is GooglePlayServicesAvailabilityIOException) {
                                showGooglePlayServicesAvailabilityErrorDialog(
                                        mLastError
                                                .connectionStatusCode)
                            } else if (mLastError is UserRecoverableAuthIOException) {
                                startActivityForResult(
                                        mLastError.intent,
                                        REQUEST_AUTHORIZATION)
                            } else {
                                toast("The following error occurred: ${mLastError.message}")
                            }
                        }

                        override fun onVideosFetched(videoList: List<ShortVideo>) {
                            // Cache into the database for ROOM
                            val videoArr = videoList.toTypedArray()
                            videoViewModel.insert(*videoArr)
                        }
                    }
            ).execute()
        }
    }

    private val isDeviceOnline: Boolean
        get() {
            val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = Objects.requireNonNull(connMgr).activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

    private val isGooglePlayServicesAvailable: Boolean
        get() {
            val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this)
            return connectionStatusCode == ConnectionResult.SUCCESS
        }

    private fun acquireGooglePlayServices() {
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
        }
    }

    fun showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode: Int) {
        val dialog = apiAvailability.getErrorDialog(
                this@MainActivity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES)
        dialog.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private fun chooseAccount() {
        if (EasyPermissions.hasPermissions(
                        this, Manifest.permission.GET_ACCOUNTS)) {
            val accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null)
            if (accountName != null) {
                mCredential.selectedAccountName = accountName
                resultsFromApi()
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER)
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_GOOGLE_PLAY_SERVICES -> if (resultCode != Activity.RESULT_OK) {
                toast("This app requires Google Play Services. Please install " +
                        "Google Play Services on your device and relaunch this app.", Toast.LENGTH_LONG)
            } else {
                resultsFromApi()
            }
            REQUEST_ACCOUNT_PICKER -> if (resultCode == Activity.RESULT_OK && data != null &&
                    data.extras != null) {
                val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                if (accountName != null) {
                    val settings = getPreferences(Context.MODE_PRIVATE)
                    val editor = settings.edit()
                    editor.putString(PREF_ACCOUNT_NAME, accountName)
                    editor.apply()
                    mCredential.selectedAccountName = accountName
                    resultsFromApi()
                }
            }
            REQUEST_AUTHORIZATION -> if (resultCode == Activity.RESULT_OK) resultsFromApi()
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            // Change orientation back to Portrait, when back from fullscreen video
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_delete) {
            FirebaseUtils.deleteAccount({ o: Any? ->
                Log.i(TAG, "onOptionsItemSelected: Deleted")
                Toast.makeText(this, "Account Deleted!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, UserRegistrationActivity::class.java))
                finish()
            }
            ) { e: Exception? -> Toast.makeText(this, "Account could not be deleted!", Toast.LENGTH_SHORT).show() }
            return true
        } else if (id == R.id.action_sign_out) {
            FirebaseUtils.signOut(this) {
                startActivity(Intent(this@MainActivity, UserRegistrationActivity::class.java))
                finish()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        when (id) {

            R.id.nav_slideshow -> {
                FirebaseUtils.signOut(this) {
                    startActivity(Intent(this@MainActivity, UserRegistrationActivity::class.java))
                    finish()
                }

            }
            R.id.nav_manage -> {
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentHolder, SettingsFragment())
                        .addToBackStack(HomeFragment.TAG)
                        .commit()
            }
        }
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        // Do nothing
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        // Do nothing
    }

    override fun onVideoClicked(video: ShortVideo) {
        videoViewModel.selectVideo(video)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentHolder, YoutubeFragment(), YoutubeFragment.TAG)
                .addToBackStack(HomeFragment.TAG)
                .commit()
    }

    override fun onForecastClick(v: View?) {
        val fm = supportFragmentManager
        var fragment = fm.findFragmentByTag(WeatherFragment.TAG)
        if (fragment == null) {
            if (weatherFragment == null)
                weatherFragment = WeatherFragment()
            fragment = weatherFragment
        }
        fm.beginTransaction()
                .replace(R.id.fragmentHolder, fragment!!, WeatherFragment.TAG)
                .addToBackStack(HomeFragment.TAG)
                .commit()
    }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat?, pref: Preference?): Boolean {
        // Instantiate the new Fragment
        val args = pref!!.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
                classLoader,
                pref!!.fragment)
        fragment.arguments = args
        fragment.setTargetFragment(caller, 0)
        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentHolder, fragment)
                .addToBackStack(HomeFragment.TAG)
                .commit()
        return true
    }
}