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
import android.view.Gravity
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
import com.fyp.agrifarm.app.weather.ui.WeatherFragment
import com.fyp.agrifarm.app.youtube.VideoRecyclerAdapter
import com.fyp.agrifarm.app.youtube.YoutubeDataRequest
import com.fyp.agrifarm.app.youtube.YoutubeFragment
import com.fyp.agrifarm.app.youtube.db.ExtendedVideo
import com.fyp.agrifarm.app.youtube.viewmodel.VideoSharedViewModel
import com.fyp.agrifarm.utils.FirebaseUtils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.navigation.NavigationView
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.youtube.YouTubeScopes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.drawer_header.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity(),
        PermissionCallbacks,
        NavigationView.OnNavigationItemSelectedListener,
        VideoRecyclerAdapter.OnItemClickListener, OnFragmentInteractionListener,
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback,
        SettingsFragment.OnPreferencesChangeListener {

    private val apiAvailability = GoogleApiAvailability.getInstance()
    private lateinit var videoViewModel: VideoSharedViewModel

    companion object {
        lateinit var mCredential: GoogleAccountCredential
        const val REQUEST_ACCOUNT_PICKER = 1000
        const val REQUEST_AUTHORIZATION = 1001
        const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
        const val REQUEST_PERMISSION_GET_ACCOUNTS = 1003
        private const val PREF_ACCOUNT_NAME = "accountName"
        private val SCOPES = arrayOf(
                YouTubeScopes.YOUTUBE_READONLY
        )
        const val TAG = "MainActivity"
        private var homeFragment: HomeFragment? = null
        private var weatherFragment: WeatherFragment? = null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)


        supportFragmentManager.addOnBackStackChangedListener {
            if(supportFragmentManager.backStackEntryCount == 0){
                icDrawerBg.visible()
                navigation_icon.visible()
            } else if(supportFragmentManager.backStackEntryCount > 0){
                icDrawerBg.gone()
                navigation_icon.gone()
            }
        }

        var isBack = false
        navigation_icon.speed = 3.5F
        navigation_icon.setOnClickListener {
            if(!isBack) {
                navigation_icon.setMinAndMaxProgress(0F, 0.5F)
                drawer.openDrawer(GravityCompat.START, true)
            } else {
                navigation_icon.setMinAndMaxProgress(0.5F, 1F)
                drawer.closeDrawer(GravityCompat.START, true)
            }
            navigation_icon.playAnimation()
            isBack = !isBack
        }


        drawer.addDrawerListener(object: DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {

            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

                navigation_icon.progress = slideOffset
            }

            override fun onDrawerClosed(drawerView: View) {
                navigation_icon.progress = 0F
                isBack = false
            }

            override fun onDrawerOpened(drawerView: View) {
                navigation_icon.progress = 0.5F
                isBack = true
            }

        })

//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        val toggle = ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
//        drawer.addDrawerListener(toggle)
//        toggle.syncState()
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        var headerView = navigationView.getHeaderView(0)
        if (headerView == null) {
            headerView = navigationView.inflateHeaderView(R.layout.drawer_header)
        }
//
//        // TODO: Add these to profile
        val tvUserFullName = headerView!!.findViewById<TextView>(R.id.tvDrawerName)
        val tvUserOccupation = headerView.findViewById<TextView>(R.id.tvDrawerOccupation)
        val uProfilePhoto = headerView.findViewById<ImageView>(R.id.ivDrawerProfile)

        FirebaseAuth.getInstance().currentUser?.let { user ->
            tvUserFullName.text = user.displayName
            FirebaseUtils.downloadUserProfileImage(this, user, uProfilePhoto, resources)
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
        if (user != null && user.email != null && user.email!!.isNotEmpty()) {
            mCredential.selectedAccountName = user.email
        } else {
            // Not called in OTP Login, so we have to call it to configure YouTube account
            resultsFromApi()
        }
        YoutubeDataRequest.instance.setCredentials(mCredential)
    }

    // Cache into the database for ROOM
    private fun resultsFromApi() {
        if (!isGooglePlayServicesAvailable) {
            acquireGooglePlayServices()
        } else if (mCredential.selectedAccountName == null) {
            chooseAccount()
        } else if (!isDeviceOnline) {
            toast("No network connection available")
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
                log("ffnet starting intent now")
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
            REQUEST_GOOGLE_PLAY_SERVICES -> {
                if (resultCode != Activity.RESULT_OK) {
                    toast("This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.", Toast.LENGTH_LONG)
                } else {
                    resultsFromApi()
                }
            }
            REQUEST_ACCOUNT_PICKER -> {
                if (resultCode == Activity.RESULT_OK && data != null &&
                        data.extras != null) {
                    log("ffnet :: REQUEST_ACC_PICKER")
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {

            R.id.nav_signout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
            R.id.nav_settings -> {
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

    override fun onVideoClicked(video: ExtendedVideo) {
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
                pref.fragment)
        fragment.arguments = args
        fragment.setTargetFragment(caller, 0)
        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentHolder, fragment)
                .addToBackStack(HomeFragment.TAG)
                .commit()

        return true
    }

    override fun onWeatherChanged(newValue: String?) {
        homeFragment?.onWeatherChanged(newValue)
    }
}