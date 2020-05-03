package com.fyp.agrifarm.app.prices

import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.os.Looper
import androidx.lifecycle.*
import com.fyp.agrifarm.app.KEY_LOCATION_SET
import com.fyp.agrifarm.app.prices.model.LocationListItem
import com.fyp.agrifarm.app.getSharedPrefs
import com.fyp.agrifarm.app.log
import com.fyp.agrifarm.app.prices.model.LoadState
import com.fyp.agrifarm.utils.AssetUtils
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

class PricesViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context = application.applicationContext
    val locFetchFailureStatus = MutableLiveData<Boolean>()

    val location = MutableLiveData<LocationListItem>().apply {
        // If sharedPreferences has a value then initialize the location value accordingly
        context.getSharedPrefs().getStringSet(KEY_LOCATION_SET, null)?.let { loc ->
            val locItem = LocationListItem(loc.elementAt(0), loc.elementAt(1))
            postValue(locItem)
        }
    }

    val currentState = MutableLiveData<LoadState>().apply {
        if(location.value == null)
            postValue(LoadState.UNSET)
        else
            postValue(LoadState.LOADED)
    }

    fun setFailureStatus(status: Boolean) {
        locFetchFailureStatus.postValue(status)
        if(status) currentState.postValue(LoadState.UNSET)
    }

    fun setLocation(loc: LocationListItem) {
        location.postValue(loc)
        currentState.postValue(LoadState.LOADED)
    }

    fun fetchCurrentLocation() {
        currentState.postValue(LoadState.LOADING)
        val locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 3000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        LocationServices.getFusedLocationProviderClient(context)
                .requestLocationUpdates(locationRequest, object : LocationCallback() {

                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        if (locationResult.locations.size > 0) {
                            val latestLocationIndex = locationResult.locations.size - 1
                            val latitude = locationResult.locations[latestLocationIndex].latitude
                            val longitude = locationResult.locations[latestLocationIndex].longitude
                            // It'll post the location value inside
                            decodeToAddress(latitude, longitude)
                        }
                        LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(this)
                    }
                }, Looper.getMainLooper())
    }

    fun decodeToAddress(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val firstAddress = geocoder.getFromLocation(latitude, longitude, 1)[0]
                val area = firstAddress.subAdminArea

                // Run asynchronously and await for the result to publish
                val list = viewModelScope.async(Dispatchers.IO) {
                    AssetUtils.findAddress(context.resources, area)
                }

                val loc = list.await() ?: throw LocationFailureException("Area couldn't be decoded")
                setLocation(loc)
                // Set the status that we've successfully fetched the location
                setFailureStatus(false)
            } catch (e: IOException) {
                // Set the status that location decode failed and we've to switch to manual selection
                setFailureStatus(true)
                e.printStackTrace()
            } catch (e: LocationFailureException) {
                setFailureStatus(true)
            }
        }
    }

    inner class LocationFailureException(msg: String): Exception(msg)
}