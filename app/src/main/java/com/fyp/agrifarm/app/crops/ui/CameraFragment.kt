package com.fyp.agrifarm.app.crops.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.fyp.agrifarm.R
import com.fyp.agrifarm.app.crops.CameraActivity
import com.fyp.agrifarm.app.crops.KEY_EVENT_ACTION
import com.fyp.agrifarm.app.crops.KEY_EVENT_EXTRA
import com.fyp.agrifarm.app.crops.PermissionsFragment
import com.fyp.agrifarm.app.crops.utils.ANIMATION_FAST_MILLIS
import com.fyp.agrifarm.app.crops.utils.ANIMATION_SLOW_MILLIS
import com.fyp.agrifarm.app.crops.utils.simulateClick
import com.fyp.agrifarm.app.log
import com.fyp.agrifarm.app.toastFrag
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.camera_ui_container.*
import kotlinx.android.synthetic.main.camera_ui_container.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Main fragment for this app. Implements all camera operations including:
 * - Viewfinder
 * - Photo taking
 */
class CameraFragment : Fragment() {

    private lateinit var container: ConstraintLayout
    private lateinit var viewFinder: PreviewView
    private lateinit var outputDirectory: File
    private lateinit var broadcastManager: LocalBroadcastManager
    private lateinit var displayManager: DisplayManager
    private lateinit var mainExecutor: Executor

    private val RESOLUTION = Size(720,1280)


    private lateinit var photoFile: File

    private var displayId: Int = -1
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var captureButton: ImageButton? = null
    private var retryCaptureButton: ImageButton? = null
    private var progress: ProgressBar? = null
    private var fabCheck: FloatingActionButton? = null
    private var imgPreview: ImageView? = null

    /** Volume down button receiver used to trigger shutter */
    private val volumeDownReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getIntExtra(KEY_EVENT_EXTRA, KeyEvent.KEYCODE_UNKNOWN)) {
                // When the volume down button is pressed, simulate a shutter button click
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    val shutter = container
                            .findViewById<ImageButton>(R.id.camera_capture_button)
                    shutter.simulateClick()
                }
            }
        }
    }

    /**
     * We need a display listener for orientation changes that do not trigger a configuration
     * change, for example if we choose to override config change in manifest or for 180-degree
     * orientation changes.
     */
    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit
        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@CameraFragment.displayId) {
                Log.d(TAG, "Rotation changed: ${view.display.rotation}")
                imageCapture?.targetRotation = view.display.rotation
            }
        } ?: Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainExecutor = ContextCompat.getMainExecutor(requireContext())
    }

    override fun onResume() {
        super.onResume()
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
        if (!PermissionsFragment.hasPermissions(requireContext())) {
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
                    CameraFragmentDirections.actionCameraToPermissions()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister the broadcast receivers and listeners
        broadcastManager.unregisterReceiver(volumeDownReceiver)
        displayManager.unregisterDisplayListener(displayListener)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_camera, container, false)



    /** Define callback that will be triggered after a photo has been taken and saved to disk */
    private val imageSavedListener = object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

//            Because the outputFileResults.savedUri returns null, we as a work around
//            I've stored the file as gloabal variable  `photoFile` which will be retrieved when
//            savedUri is null

//            val fileUri = outputFileResults.savedUri ?: return
            val fileUri = outputFileResults.savedUri ?: photoFile.toUri()


            Picasso.get().load(fileUri).into(imgPreview, object : Callback {
                override fun onSuccess() {
                    log("Success")
                    imgPreview?.visibility = View.VISIBLE
                    progress?.visibility = View.GONE
                    fabCheck?.visibility = View.VISIBLE
                    retry_capture_button?.visibility = View.VISIBLE
                }

                override fun onError(e: java.lang.Exception?) {
                    log("Error")
                    e?.printStackTrace()
                }

            })


            // If the folder selected is an external media directory, this is unnecessary
            // but otherwise other apps will not be able to access our images unless we
            // scan them using [MediaScannerConnection]
            // To get extension
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    context?.contentResolver?.getType(fileUri))
            MediaScannerConnection.scanFile(
                    context, arrayOf(fileUri.path), arrayOf(mimeType), null
            )
        }

        override fun onError(exception: ImageCaptureException) {
            exception.printStackTrace()
            toastFrag("Error")
            Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
        }

    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        container = view as ConstraintLayout
        viewFinder = container.findViewById(R.id.view_finder)
        broadcastManager = LocalBroadcastManager.getInstance(view.context)

        // Set up the intent filter that will receive events from our main activity
        val filter = IntentFilter().apply { addAction(KEY_EVENT_ACTION) }
        broadcastManager.registerReceiver(volumeDownReceiver, filter)

        // Every time the orientation of device changes, recompute layout
        displayManager = viewFinder.context
                .getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        displayManager.registerDisplayListener(displayListener, null)

        // Determine the output directory
        outputDirectory = CameraActivity.getOutputDirectory(requireContext())

        // Wait for the views to be properly laid out

        viewFinder.post {

            // Keep track of the display in which this view is attached
            displayId = viewFinder.display.displayId

            // Build UI controls
            updateCameraUi()

            // Bind use cases
            bindCameraUseCases()
        }
    }

    /**
     * Inflate camera controls and update the UI manually upon config changes to avoid removing
     * and re-adding the view finder from the view hierarchy; this provides a seamless rotation
     * transition on devices that support it.
     *
     * NOTE: The flag is supported starting in Android 8 but there still is a small flash on the
     * screen for devices that run Android 9 or below.
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateCameraUi()
    }

    /** Declare and bind preview, capture and analysis use cases */
    @SuppressLint("RestrictedApi")
    private fun bindCameraUseCases() {

        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
        Log.d(TAG, "Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

        val rotation = viewFinder.display.rotation

        // Bind the CameraProvider to the LifeCycleOwner
        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {

            // CameraProvider
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            preview = Preview.Builder()
                    // We request aspect ratio but no resolution
//                    .setTargetAspectRatio(screenAspectRatio)
                    // Set initial target rotation
                    .setTargetRotation(rotation)
                    .setTargetResolution(RESOLUTION)
                    .setDefaultResolution(RESOLUTION)
                    .build()

            // Default PreviewSurfaceProvider
            preview?.setSurfaceProvider(viewFinder.previewSurfaceProvider)
//            preview?.previewSurfaceProvider = viewFinder.previewSurfaceProvider

            // ImageCapture
            imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    // We request aspect ratio but no resolution to match preview config, but letting
                    // CameraX optimize for whatever specific resolution best fits requested capture mode
//                    .setTargetAspectRatio(screenAspectRatio)

                    // Set initial target rotation, we will have to call this again if rotation changes
                    // during the lifecycle of this use case
                    .setTargetRotation(rotation)
                    .setTargetResolution(RESOLUTION)
                    .setDefaultResolution(RESOLUTION)
                    .build()

            // Must unbind the use-cases before rebinding them.
            cameraProvider.unbindAll()

            try {
                // A variable number of use-cases can be passed here -
                // camera provides access to CameraControl & CameraInfo
                camera = cameraProvider.bindToLifecycle(
                        this as LifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, mainExecutor)
    }

    /**
     *  Detecting the most suitable ratio for dimensions provided in @params by counting absolute
     *  of preview ratio to one of the provided values.
     *
     *  @param width - preview width
     *  @param height - preview height
     *  @return suitable aspect ratio
     */
    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    /** Method used to re-draw the camera UI controls, called every time configuration changes. */
    private fun updateCameraUi() {

        // TODO: [Efficiency] updateCameraUi is also called for retryCapture that does the following overhead
        // Remove previous UI if any
        container.findViewById<ConstraintLayout>(R.id.camera_ui_container)?.let {
            container.removeView(it)
        }

        // Inflate a new view containing all UI for controlling the camera
        val controls = View.inflate(requireContext(), R.layout.camera_ui_container, container)
        captureButton = controls.findViewById(R.id.camera_capture_button)
        retryCaptureButton = controls.findViewById(R.id.retry_capture_button)
        progress = controls.progress_shutter
        fabCheck = controls.fab_shutter_check
        imgPreview = controls.img_preview

        /* Reset Controls when Retry is clicked
         * =========================
         * Start
         */

        imgPreview?.setImageResource(0)
        imgPreview?.visibility = View.GONE
        retry_capture_button?.visibility = View.GONE
        fabCheck?.visibility = View.GONE
        captureButton?.visibility = View.VISIBLE

        /* Reset Controls when Retry is clicked
         * =========================
         * End
         */

        // Listener for button used to capture photo
        photoFile = createFile(outputDirectory)

        captureButton?.setOnClickListener { captureButton ->
            // Get a stable reference of the modifiable image capture use case

            progress?.visibility = View.VISIBLE
            captureButton.visibility = View.GONE
            imageCapture?.let { imageCapture ->

                val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
//                        .setMetadata(metadata)
                        .build()
                // Setup image capture listener which is triggered after photo has been taken
                try {

                    imageCapture.takePicture(outputFileOptions, mainExecutor, imageSavedListener)
                } catch (e: Exception) {
                    log("Errror exception")
                    e.printStackTrace()
                }

                // We can only change the foreground Drawable using API level 23+ API
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    // Display flash animation to indicate that photo was captured
                    container.postDelayed({
                        container.foreground = ColorDrawable(Color.WHITE)
                        container.postDelayed(
                                {
                                    container.foreground = null
                                }, ANIMATION_FAST_MILLIS)
                    }, ANIMATION_SLOW_MILLIS)

                }
            }
//            uploadImageToFirebase(photoFile)
        }

        // Listener for button used to retake image
        retryCaptureButton?.setOnClickListener {
            updateCameraUi()
        }

        fabCheck?.setOnClickListener {
//            uploadImageToFirebase()
            imgPreview?.let { imgView ->
                val extras = FragmentNavigatorExtras(
                        imgView to "leafSubject"
                )
                val args = bundleOf(
                        CropsResultFragment.KEY_PHOTO_PATH to photoFile.absolutePath
                )
                findNavController().navigate(R.id.action_image_to_cropsResultFragment,
                        args, null, extras)
            }
        }
    }




    companion object {

        private const val TAG = "CameraFragment"
        // No more needed, this will just prolong file name,
        // instead of uid_timestamp as file name, we'll use uid (directory in fb storage) > timestamp
//        val user = FirebaseAuth.getInstance().currentUser?.uid.toString()
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0

        /** Helper function used to create a timestamped file */
        private fun createFile(baseFolder: File) =
                File(baseFolder, SimpleDateFormat(FILENAME, Locale.US)
                        .format(System.currentTimeMillis()) + PHOTO_EXTENSION)
    }
}