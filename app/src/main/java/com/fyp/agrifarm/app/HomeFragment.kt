package com.fyp.agrifarm.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.fyp.agrifarm.R
import com.fyp.agrifarm.app.crops.CameraActivity
import com.fyp.agrifarm.app.crops.PermissionsFragment.Companion.hasPermissions
import com.fyp.agrifarm.app.news.db.NewsEntity
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
import com.fyp.agrifarm.app.profile.model.User
import com.fyp.agrifarm.app.profile.ui.FirestoreUserRecyclerAdapter
import com.fyp.agrifarm.app.profile.ui.UserInformationActivity
import com.fyp.agrifarm.app.weather.model.WeatherViewModel
import com.fyp.agrifarm.app.youtube.VideoRecyclerAdapter
import com.fyp.agrifarm.app.youtube.viewmodel.VideoSharedViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ml.common.FirebaseMLException
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.content_main.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicReference

const val KEY_LOCATION_SET = "userDistrict"

class HomeFragment() : Fragment(), OnLocationItemClickListener {

    private val db = FirebaseFirestore.getInstance()
    private val userRef = db.collection("users")
    private var cloudSource: FirebaseCustomRemoteModel? = null
    private var downloadConditions: FirebaseModelDownloadConditions? = null

    private lateinit var pricesViewModel: PricesViewModel
    private lateinit var videoViewModel: VideoSharedViewModel
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var newsSharedViewModel: NewsSharedViewModel
    private lateinit var videoRecyclerAdapter: VideoRecyclerAdapter
    private lateinit var newsRecyclerAdapter: NewsRecyclerAdapter
    private lateinit var adapter: FirestoreUserRecyclerAdapter
    private var mListener: OnFragmentInteractionListener? = null


    companion object {
        @JvmField
        val TAG = "HomeFragment"
        val REQUEST_PICK_IMAGE = 1111
        val RC_LOCATION = 1021
        const val RC_LOCATION_DIALOG = 1002
        val KEY_MODEL_DOWNLOAD = "downloaded"
        val KEY_SHARED_PREFS_NAME = "agriFarm"
        private val OUTPUT_LABELS_FILE = "output_labels.txt"
        private val IMG_WIDTH = 256
        private val IMG_HEIGHT = 256
        private val OUTPUT_CLASSES = 15
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pricesViewModel = ViewModelProvider(this).get(PricesViewModel::class.java)
        newsSharedViewModel = ViewModelProvider(this).get(NewsSharedViewModel::class.java)
        videoViewModel = ViewModelProvider(this).get(VideoSharedViewModel::class.java)
        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        newsSharedViewModel.newsList.observe(viewLifecycleOwner, androidx.lifecycle.Observer { list ->
            newsRecyclerAdapter.changeDataSource(list)
        })

        videoViewModel.allVideos.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            videoRecyclerAdapter.updateList(it)
        })


        fabTakeImage.setOnClickListener { startAnActivity(CameraActivity::class.java) }

        // Inflating users
        val options = FirestoreRecyclerOptions.Builder<User>()
                .setQuery(userRef, User::class.java)
                .build()
        adapter = FirestoreUserRecyclerAdapter(options, context)
        rvUsers.setHasFixedSize(true)
        rvUsers.adapter = adapter
        adapter.notifyDataSetChanged()

        adapter.setOnItemClickListener { snap: DocumentSnapshot, position: Int ->
            val user = snap.toObject(User::class.java)
            val intent = Intent(context, UserInformationActivity::class.java)
            intent.putExtra("username", user!!.fullname)
            intent.putExtra("userphoto", user.getPhotoUri())
            intent.putExtra("docid", snap.id)
            startActivityForResult(intent, 20)
        }

        val priceList: MutableList<PriceItem> = ArrayList()
        priceList.add(PriceItem("Cherry", "97.22", PriceItem.CHERRY, PriceItem.ARROW_DOWN))
        priceList.add(PriceItem("Kinno", "60.00", PriceItem.ORANGE, PriceItem.ARROW_UP))
        priceList.add(PriceItem("Brinjal", "55.64", PriceItem.BRINJAL, PriceItem.ARROW_DOWN))
        priceList.add(PriceItem("Guava", "90.50", PriceItem.GUAVA, PriceItem.ARROW_DOWN))
        priceList.add(PriceItem("Apple", "120.50", PriceItem.APPLE, PriceItem.ARROW_UP))
        priceList.add(PriceItem("Carrot", "73.75", PriceItem.CARROT, PriceItem.ARROW_UP))

        val priceAdapter = PricesRecyclerAdapter(context, priceList)
        rvPrices.adapter = priceAdapter
        newsRecyclerAdapter = NewsRecyclerAdapter(context, OnNewsClinkListener { selectedNews: NewsEntity ->
            // SharedViewModel instance isn't shared across activities
            // That is why passing the attributes over intent for now
            val intent = Intent(activity, DetailsActivity::class.java)
            intent.putExtra(DetailsActivity.MODE, DetailsActivity.MODE_NEWS)
            intent.putExtra(DetailsActivity.KEY_ID, selectedNews.id)
            startActivity(intent)
        })
        rvNews.adapter = newsRecyclerAdapter
        videoRecyclerAdapter = VideoRecyclerAdapter(context)
        rvVideo.adapter = videoRecyclerAdapter

        // TODO: Remove unused MLKit
//        initMLKit()

        // First load update required
        pricesViewModel.location.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            it?.let { pricesViewModel.currentState.postValue(LoadState.LOADED) }
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

        getweatherinformation()

    }

    private fun getweatherinformation() {
        val weatherViewModel = ViewModelProvider(activity!!).get(WeatherViewModel::class.java)
        weatherViewModel.dailyforcast.observe(viewLifecycleOwner, Observer { weatherDailyForecast ->
            val temperatue = weatherDailyForecast.temperature + "°C"
            tvWeatherTemp.text = temperatue
            tvWeatherDescription.text = weatherDailyForecast.description
            tvWeatherDay.text = weatherDailyForecast.day
            tvWeatherHumidity.text = weatherDailyForecast.humidity
            var WeatherIconMap: Map<String, Int>? = null
            WeatherIconMap = HashMap()
            WeatherIconMap.put("01d", R.drawable.ic_wi_day_sunny)
            WeatherIconMap.put("02d", R.drawable.ic_wi_day_cloudy)
            WeatherIconMap.put("03d", R.drawable.ic_wi_cloud)
            WeatherIconMap.put("04d", R.drawable.ic_wi_cloudy)
            WeatherIconMap.put("09d", R.drawable.ic_wi_showers)
            WeatherIconMap.put("10d", R.drawable.ic_wi_day_rain_mix)
            WeatherIconMap.put("11d", R.drawable.ic_wi_thunderstorm)
            WeatherIconMap.put("13d", R.drawable.ic_wi_snow)
            WeatherIconMap.put("50d", R.drawable.ic_wi_fog)
            WeatherIconMap.put("04n", R.drawable.ic_wi_cloudy)


            val iconurl = weatherDailyForecast.iconurl
            if (weatherDailyForecast.iconurl == "01d") {
                ivWeatherIcon.setImageResource(WeatherIconMap.get("01d")!!)
            }
            if (weatherDailyForecast.iconurl == "02d") {
                ivWeatherIcon.setImageResource(WeatherIconMap.get("02d")!!)
            }
            if (weatherDailyForecast.iconurl == "03d") {
                ivWeatherIcon.setImageResource(WeatherIconMap.get("03d")!!)
            }
            if (weatherDailyForecast.iconurl == "04d") {
                ivWeatherIcon.setImageResource(WeatherIconMap.get("04d")!!)
            }
            if (weatherDailyForecast.iconurl == "04n") {
                ivWeatherIcon.setImageResource(WeatherIconMap.get("04n")!!)
            }
            if (weatherDailyForecast.iconurl == "50d") {
                ivWeatherIcon.setImageResource(WeatherIconMap.get("50d")!!)
            }
            if (weatherDailyForecast.iconurl == "09d") {
                ivWeatherIcon.setImageResource(WeatherIconMap.get("09d")!!)
            }
            if (weatherDailyForecast.iconurl == "10d") {
                ivWeatherIcon.setImageResource(WeatherIconMap.get("10d")!!)
            }
            if (weatherDailyForecast.iconurl == "11d") {
                ivWeatherIcon.setImageResource(WeatherIconMap.get("11d")!!)
            }
            if (weatherDailyForecast.iconurl == "13d") {
                ivWeatherIcon.setImageResource(WeatherIconMap.get("13d")!!)
            }

//            Picasso.get().load(iconurl).into(ivWeatherIcon)

//        val c = Calendar.getInstance().time
//
//        val df = SimpleDateFormat("yyyy_MM_dd")
//        val formattedDate = df.format(c)
//
//        val newsref = FirebaseFirestore.getInstance().collection("news").document("2020").collection(formattedDate).orderBy("date", Query.Direction.DESCENDING)
//        newsref.get().addOnSuccessListener { documentSnapshot ->
//            if (documentSnapshot.isEmpty) {
//                Toast.makeText(context, "No News for this data ", Toast.LENGTH_SHORT)
//            } else {
//                for (document in documentSnapshot.documents) {
//                    val newsarray = document.get("news") as List<Map<String, Any>>?
//                    val map = newsarray?.get(0)
//                    val data = document.data
//                    Log.d("MSIS",""+data)
//                }
//
//            }
//
//        }.addOnFailureListener { exception ->
//            exception.printStackTrace()
//        }
//

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

    private fun initMLKit() {
        cloudSource = FirebaseCustomRemoteModel.Builder(
                "PlantDisease-Detector").build()

//        FirebaseCustomLocalModel localModel = new FirebaseCustomLocalModel.Builder()
//                .setAssetFilePath("model.tflite")
//                .build();
        downloadConditions = FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build()
        FirebaseModelManager.getInstance().download(cloudSource!!, downloadConditions!!)
                .addOnCompleteListener {
                    requireContext().getSharedPrefs().edit()
                            .putBoolean(KEY_MODEL_DOWNLOAD, true).apply()
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_LOCATION_DIALOG -> getLocation()
            REQUEST_PICK_IMAGE -> if (data != null) {
//                    try {
//                        InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
//                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Picasso.get().load(data.data).into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {

                        // Before initialzing intreperator, confirm model is downloaded
                        FirebaseModelManager.getInstance().isModelDownloaded((cloudSource)!!)
                                .addOnSuccessListener { isDownloaded: Boolean ->
                                    var options: FirebaseModelInterpreterOptions? = null
                                    if (isDownloaded) {
                                        options = FirebaseModelInterpreterOptions.Builder((cloudSource)!!).build()
                                    } else {
                                        return@addOnSuccessListener
                                        // At this point we don't have local model in assets
//                                      options = new FirebaseModelInterpreterOptions.Builder(localModel).build();
                                    }
                                    try {
                                        val interpreter: FirebaseModelInterpreter? = FirebaseModelInterpreter.getInstance(options)
                                        val inputOutputOptions: FirebaseModelInputOutputOptions = FirebaseModelInputOutputOptions.Builder()
                                                .setInputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, IMG_HEIGHT, IMG_WIDTH, 3))
                                                .setOutputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, OUTPUT_CLASSES))
                                                .build()
                                        val inputs: FirebaseModelInputs = FirebaseModelInputs.Builder()
                                                .add(imgToArray(bitmap)) // add() as many input arrays as your model requires
                                                .build()
                                        assert(interpreter != null)
                                        interpreter!!.run(inputs, inputOutputOptions)
                                                .addOnSuccessListener(OnSuccessListener { result: FirebaseModelOutputs ->
                                                    // We are interested in just the first row for single input
                                                    val output: Array<FloatArray> = result.getOutput(0)
                                                    val probabilities: FloatArray = output.get(0)
                                                    val outputLabel: String? = mapOutputToLabel(probabilities)
                                                    Toast.makeText(getContext(), "Result: " + outputLabel, Toast.LENGTH_LONG).show()
                                                })
                                                .addOnFailureListener(OnFailureListener { e: Exception? -> log("getImagePrediction: Couldn't identify") })
                                    } catch (e: FirebaseMLException) {
                                        e.printStackTrace()
                                    }
                                }
                    }

                    override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
                    override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
                })
                //                } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
            }
        }
    }

    fun getImagePrediction(bitmap: Bitmap?): String {
        val outputLabel = AtomicReference("Couldn't Identify")
        return outputLabel.get()
    }

    fun imgToArray(img: Bitmap): Array<Array<Array<FloatArray>>> {
        val image = Bitmap.createScaledBitmap(img, IMG_WIDTH, IMG_HEIGHT, true)
        val batchNum = 0
        val input = Array(1) { Array(IMG_HEIGHT) { Array(IMG_WIDTH) { FloatArray(3) } } }
        for (x in 0..223) {
            for (y in 0..223) {
                val pixel = image.getPixel(x, y)
                // Normalize channel values to [-1.0, 1.0]. This requirement varies by
                // model. For example, some models might require values to be normalized
                // to the range [0.0, 1.0] instead.
//                input[batchNum][x][y][0] = (Color.red(pixel) - 127) / 128.0f;
//                input[batchNum][x][y][1] = (Color.green(pixel) - 127) / 128.0f;
//                input[batchNum][x][y][2] = (Color.blue(pixel) - 127) / 128.0f;
                input[batchNum][x][y][0] = Color.red(pixel).toFloat()
                input[batchNum][x][y][1] = Color.green(pixel).toFloat()
                input[batchNum][x][y][2] = Color.blue(pixel).toFloat()
            }
        }
        return input
    }

    private fun mapOutputToLabel(probabilities: FloatArray): String? {
        val sb = StringBuilder()
        for (probability: Float in probabilities) {
            sb.append("$probability, ")
        }
        log("Probs are : $sb")
        var label: String? = null
        try {
            requireContext().resources.openRawResource(R.raw.output_labels).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    for (probability: Float in probabilities) {
                        label = reader.readLine()
                        if (probability.toDouble() == 1.0) break
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        log("MLKit $label")
        return label
    }

    private fun saveLocationToSharedPrefs(loc: LocationListItem) {
        val location = setOf(loc.areaCode, loc.areaName)
        requireContext().getSharedPrefs().edit()
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

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onLocationSelected(item: LocationListItem) {
        saveLocationToSharedPrefs(item)
        pricesViewModel.setLocation(item)
    }

    interface OnFragmentInteractionListener {
        fun onForecastClick(v: View?)
    }

}