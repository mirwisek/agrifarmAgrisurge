package com.fyp.agrifarm.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.fyp.agrifarm.R
import com.fyp.agrifarm.app.crops.CameraActivity
import com.fyp.agrifarm.app.news.ui.NewsRecyclerAdapter
import com.fyp.agrifarm.app.news.ui.NewsRecyclerAdapter.OnNewsClinkListener
import com.fyp.agrifarm.app.news.viewmodel.NewsSharedViewModel
import com.fyp.agrifarm.app.prices.PricesViewModel
import com.fyp.agrifarm.app.prices.model.LoadState
import com.fyp.agrifarm.app.prices.model.LocationListItem
import com.fyp.agrifarm.app.prices.model.PriceItem
import com.fyp.agrifarm.app.prices.ui.LocationListFragment
import com.fyp.agrifarm.app.prices.ui.LocationListFragment.OnLocationItemClickListener
import com.fyp.agrifarm.app.prices.ui.PricesRecyclerAdapter
import com.fyp.agrifarm.app.weather.model.WeatherViewModel
import com.fyp.agrifarm.app.youtube.VideoRecyclerAdapter
import com.fyp.agrifarm.app.youtube.viewmodel.VideoSharedViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

const val KEY_LOCATION_SET = "userDistrict"
const val KEY_SHARED_PREFS_NAME = "agriFarm"

@ExperimentalCoroutinesApi
class HomeFragment : Fragment(), OnLocationItemClickListener {

    private lateinit var pricesViewModel: PricesViewModel
    private lateinit var videoViewModel: VideoSharedViewModel
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var newsSharedViewModel: NewsSharedViewModel
    private lateinit var videoRecyclerAdapter: VideoRecyclerAdapter
    private lateinit var newsRecyclerAdapter: NewsRecyclerAdapter
    private var mListener: OnFragmentInteractionListener? = null


    companion object {
        const val TAG = "HomeFragment"
        const val RC_LOCATION = 1021
        const val RC_LOCATION_DIALOG = 1002
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabTakeImage.shrink()

        pricesViewModel = ViewModelProvider(this).get(PricesViewModel::class.java)
        newsSharedViewModel = ViewModelProvider(this).get(NewsSharedViewModel::class.java)

        videoViewModel = ViewModelProvider(this).get(VideoSharedViewModel::class.java)
        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        newsSharedViewModel.newsList.observe(viewLifecycleOwner, androidx.lifecycle.Observer { list ->
            newsRecyclerAdapter.changeDataSource(list)
        })

        // Listen when network request finish, remove progressbar
        newsSharedViewModel.newsFetchComplete.observe(viewLifecycleOwner, Observer { isComplete ->
            if(isComplete) {
                progressNews.gone()
                newsSharedViewModel.newsFetchComplete.removeObservers(this)
            }
        })

        newsSharedViewModel.isOfflineResult.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {  isOffline ->
                tvHintNews.text = if(isOffline) "News - Offline Results" else "News"
            }
        })

        videoViewModel.allVideos.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            videoRecyclerAdapter.updateList(it)
        })

        fabTakeImage.setOnClickListener { startAnActivity(CameraActivity::class.java) }


        val priceList: MutableList<PriceItem> = ArrayList()
        priceList.add(PriceItem("Cherry", "97.22", PriceItem.CHERRY, PriceItem.ARROW_DOWN))
        priceList.add(PriceItem("Kinno", "60.00", PriceItem.ORANGE, PriceItem.ARROW_UP))
        priceList.add(PriceItem("Brinjal", "55.64", PriceItem.BRINJAL, PriceItem.ARROW_DOWN))
        priceList.add(PriceItem("Guava", "90.50", PriceItem.GUAVA, PriceItem.ARROW_DOWN))
        priceList.add(PriceItem("Apple", "120.50", PriceItem.APPLE, PriceItem.ARROW_UP))
        priceList.add(PriceItem("Carrot", "73.75", PriceItem.CARROT, PriceItem.ARROW_UP))

        val priceAdapter = PricesRecyclerAdapter(context, priceList)
        rvPrices.adapter = priceAdapter

        newsRecyclerAdapter = NewsRecyclerAdapter(context, OnNewsClinkListener { selectedNews ->
            // SharedViewModel instance isn't shared across activities
            // That is why passing the attributes over intent for now
            val intent = Intent(activity, DetailsActivity::class.java)
            intent.putExtra(DetailsActivity.MODE, DetailsActivity.MODE_NEWS)
            intent.putExtra(DetailsActivity.KEY_ID, selectedNews.guid)
            startActivity(intent)
        })
        rvNews.adapter = newsRecyclerAdapter


        videoRecyclerAdapter = VideoRecyclerAdapter(context)
        rvVideo.adapter = videoRecyclerAdapter

        // First load update required
        pricesViewModel.location.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            it?.let { loc ->
                pricesViewModel.currentState.postValue(LoadState.LOADED)
                tvHintPrices.text = "Latest market prices for ${loc.areaName}"
            }
            pricesViewModel.location.removeObservers(this)
        })

        // Observe the states and update UI accordingly
        // This patter packs the UI visibility triggers at one place otherwise they would be
        // disperesed all over the fragment
        pricesViewModel.currentState.observe(viewLifecycleOwner, androidx.lifecycle.Observer { state ->
            when (state!!) {
                LoadState.UNSET -> {
                    btnLocation.visible()
                    rvPrices.invisible()
                    progressPrices.gone()
                }
                LoadState.LOADING -> {
                    progressPrices.visible()
                    btnLocation.invisible()
                    rvPrices.invisible()
                }
                LoadState.LOADED -> {
                    btnLocation.gone()
                    progressPrices.gone()
                    rvPrices.visible()
                }
            }
        })

        btnLocation.setOnClickListener {
            if (hasPermissions(Manifest.permission.ACCESS_FINE_LOCATION)) {
                getLocation()
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), RC_LOCATION)
            }
        }

        tvWeatherForecast.setOnClickListener { v: View? -> mListener?.onForecastClick(v) }

        getWeatherInformation()

        Handler().postDelayed( {
            fabTakeImage?.extend()
        }, 1500L)

    }


    private fun getWeatherInformation() {

        weatherViewModel.dailyforcast.observe(viewLifecycleOwner, Observer { weatherDailyForecast ->
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val WeatherPref = sharedPref.getString("weatherUnit", "-1")

            if (WeatherPref.equals("Celsius")) {

                tvWeatherTemp.text = weatherDailyForecast.temperature + "°C"


            } else {

                tvWeatherTemp.text = weatherDailyForecast.temperature + "°F"


            }
//            tvWeatherTemp.text = temperatue
            tvWeatherDescription.text = weatherDailyForecast.description
            tvWeatherDay.text = weatherDailyForecast.day
            tvWeatherHumidity.text = weatherDailyForecast.humidity


            // Wrapped with catch incase resource ID not found
            try {
                val id = weatherDailyForecast.iconurl
                ivWeatherIcon.setImageResource(weatherViewModel.weatherIconsMap[id]!!)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        })
    }


    private fun getLocation() {
        enableGPS().addOnCompleteListener { task ->
            when {
                task.isSuccessful -> {
                    progressPrices.visible()
                    btnLocation.invisible()
                    pricesViewModel.fetchCurrentLocation()
                    pricesViewModel.locFetchFailureStatus.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                        it?.let { status ->
                            when (status) {
                                true -> { // Location Failure
                                    toastFrag("Location Not Found Please Select your location Manually from the Provided List")
                                    childFragmentManager.beginTransaction()
                                            .replace(R.id.locationListFragmentContainer, LocationListFragment())
                                            .commit()
                                }
                                false -> { // Location Success
                                    // Save the location for future use
                                    saveLocationToSharedPrefs(pricesViewModel.location.value!!)
                                }
                            } // We need it for one time usage
                            pricesViewModel.locFetchFailureStatus.removeObservers(this)
                        }
                    })
                }
                task.isCanceled -> {
                    toastFrag("Location access has been denied")
                }
                else -> {
                    task.exception?.let { e ->
                        if (e is ResolvableApiException) {
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            startIntentSenderForResult(e.resolution.intentSender,
                                    RC_LOCATION_DIALOG, null, 0,
                                    0, 0, null)
                        }
                    }
                }
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == RC_LOCATION) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Since we have the permission now, trigger the location button click again
                    btnLocation.performClick()
                }
            } else
                toastFrag("Location Permissions Denied")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_LOCATION_DIALOG -> getLocation()
        }
    }

    private fun saveLocationToSharedPrefs(loc: LocationListItem) {
        val location = setOf(loc.areaCode, loc.areaName)
        requireContext().sharedPrefs.edit()
                .putStringSet(KEY_LOCATION_SET, location).apply()
    }

    private fun hasPermissions(vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableGPS(): Task<LocationSettingsResponse> {
        val location = LocationRequest.create().apply {
            interval = 5_000
            fastestInterval = 5_000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(location)
        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

//        task.addOnFailureListener { exception ->
//
//        }
        return task
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is OnFragmentInteractionListener) {
            context
        } else {
            throw RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onLocationSelected(item: LocationListItem) {
        saveLocationToSharedPrefs(item)
        pricesViewModel.setLocation(item)
    }

    interface OnFragmentInteractionListener {
        fun onForecastClick(v: View?)
    }

}