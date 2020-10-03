package com.fyp.agrifarm.app.crops.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.media.MediaScannerConnection
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
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.fyp.agrifarm.R
import com.fyp.agrifarm.app.crops.*
import com.fyp.agrifarm.app.crops.utils.ANIMATION_FAST_MILLIS
import com.fyp.agrifarm.app.crops.utils.ANIMATION_SLOW_MILLIS
import com.fyp.agrifarm.app.crops.utils.Recognition
import com.fyp.agrifarm.app.crops.utils.simulateClick
import com.fyp.agrifarm.app.gone
import com.fyp.agrifarm.app.visible
import com.fyp.agrifarm.ml.ImageDetectionmodel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.objects.ObjectDetector
import kotlinx.android.synthetic.main.camera_ui_container.*
import kotlinx.android.synthetic.main.camera_ui_container.view.*
import org.tensorflow.lite.support.image.TensorImage
import java.io.File
import java.io.FileOutputStream
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
    private lateinit var cropViewModel: CropsViewModel

    private val RESOLUTION = Size(720, 1280)

    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var objectDetector: ObjectDetector


    private lateinit var photoFile: File
    private val COMPRESS_QUALITY = 60

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
            savedInstanceState: Bundle?
    ): View {

        val root = inflater.inflate(R.layout.fragment_camera, container, false)
//                        disease_classifier . tflite

//        val localModel =
//                LocalModel.Builder()
//                        .setAssetFilePath("disease_classifier.tflite")
//                        // or .setAbsoluteFilePath(absolute file path to tflite model)
//                        .build()

//        val customObjectDetectorOptions = CustomObjectDetectorOptions.Builder(localModel)
//                .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
//                .enableClassification()
//                .setClassificationConfidenceThreshold(0.5f)
//                .setMaxPerObjectLabelCount(3)
//                .build()

//        val customObjectDetectorOptions =
//                CustomObjectDetectorOptions.Builder(localModel)
//                        .setDetectorMode(CustomObjectDetectorOptions.SINGLE_IMAGE_MODE)
//                        .enableMultipleObjects()
//                        .enableClassification()
//                        .setClassificationConfidenceThreshold(0.5f)
//                        .setMaxPerObjectLabelCount(3)
//                        .build()
//
//        objectDetector =
//                ObjectDetection.getClient(customObjectDetectorOptions)

        return root
    }


    private val imageCroppedAndSavedListener =
            object : ImageCapture.OnImageCapturedCallback() {

        @SuppressLint("UnsafeExperimentalUsageError")
        override fun onCaptureSuccess(imageProxy: ImageProxy) {
            try {
                val imageDetectionModel = ImageDetectionmodel.newInstance(requireContext())

                // ==================== MODEL WORK =========================

//                val mediaImage = imageProxy.image
//                if (mediaImage != null) {
//                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
//                    // Pass image to an ML Kit Vision API
//                    // ...
//
//
//                } else
//                    log("Image is null")


                // ============================================================

                val bitmap = CameraUtils.jpegImageToBitmap(imageProxy.image!!)

                val croppedBitmap = CameraUtils.cropCenter(bitmap)

                val tfImage : TensorImage = TensorImage.fromBitmap(croppedBitmap)

                val outputs = imageDetectionModel.process(tfImage).probabilityAsCategoryList
                        .apply {
                            sortByDescending { it.score }
                }.take(1)

                val items = arrayListOf<Recognition>()

                for (output in outputs)
                {
                    items.add(Recognition(output.label, output.score))
                }
                cropViewModel = ViewModelProvider(requireActivity()).get(CropsViewModel::class.java)
                cropViewModel.modelOutputTfLite.postValue(items[0].label)


                Log.d("ModelCheckWala", "onCaptureSuccess: $items")


                // IF in future it throws bounds exception, here is a fix
                // https://stackoverflow.com/questions/62988684/android-ml-kit-not-able-to-label-image

//                val newBitmap = croppedBitmap.copy(Bitmap.Config.ARGB_8888, croppedBitmap.isMutable())
//                val image = InputImage.fromBitmap(newBitmap, 0)

//                val image = InputImage.fromBitmap(croppedBitmap, imageProxy.imageInfo.rotationDegrees)
//
//                objectDetector.process(image)
//                        .addOnFailureListener {
//                            log("[Custom Model] = Failed ${it.message}")
//                            it.printStackTrace()
//                        }.addOnSuccessListener {
//                            log("[Custom Model] = Results [${it.size}]:")
//                            it.forEach {  item ->
//                                item.labels.forEach { label ->
//                                    log("Labels[${label.index}] ${label.text} c=${label.confidence}")
//                                }
//                            }
//                        }

                // TODO: Urgent Image will crop twice on thrid retry it will throw exception

                FileOutputStream(photoFile).use { fout ->
                    croppedBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, fout)

                    imgPreview?.setImageBitmap(croppedBitmap)
                    imgPreview?.visible()
                    progress?.gone()
                    fabCheck?.visible()
                    retry_capture_button?.visible()
                    fout.flush()
                    fout.close()
                }

                // If the folder selected is an external media directory, this is unnecessary
                // but otherwise other apps will not be able to access our images unless we
                // scan them using [MediaScannerConnection]
                // To get extension

                val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        context?.contentResolver?.getType(photoFile.toUri()))
                MediaScannerConnection.scanFile(
                        context, arrayOf(photoFile.toUri().path), arrayOf(mimeType), null
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onError(exception: ImageCaptureException) {
            exception.printStackTrace()
        }

    }


    /** Define callback that will be triggered after a photo has been taken and saved to disk */
//    private val imageSavedListener = object : ImageCapture.OnImageSavedCallback {
//        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//
////            Because the outputFileResults.savedUri returns null, we as a work around
////            I've stored the file as gloabal variable  `photoFile` which will be retrieved when
////            savedUri is null
//
////            val fileUri = outputFileResults.savedUri ?: return
//            val fileUri = outputFileResults.savedUri ?: photoFile.toUri()
//
//
//            Picasso.get().load(fileUri).into(imgPreview, object : Callback {
//                override fun onSuccess() {
//                    log("Success")
//                    imgPreview?.visible()
//                    progress?.gone()
//                    fabCheck?.visible()
//                    retry_capture_button?.visible()
//                }
//
//                override fun onError(e: java.lang.Exception?) {
//                    log("Error")
//                    e?.printStackTrace()
//                }
//
//            })
//
//
//            // If the folder selected is an external media directory, this is unnecessary
//            // but otherwise other apps will not be able to access our images unless we
//            // scan them using [MediaScannerConnection]
//            // To get extension
//            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
//                    context?.contentResolver?.getType(fileUri))
//            MediaScannerConnection.scanFile(
//                    context, arrayOf(fileUri.path), arrayOf(mimeType), null
//            )
//        }
//
//        override fun onError(exception: ImageCaptureException) {
//            exception.printStackTrace()
//            toastFrag("Error")
//            Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
//        }
//
//    }

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
    @SuppressLint("RestrictedApi", "UnsafeExperimentalUsageError")
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
                    .setTargetAspectRatio(screenAspectRatio)
                    // Set initial target rotation
                    .setTargetRotation(rotation)
                    .build()

            // Default PreviewSurfaceProvider
            preview?.setSurfaceProvider(viewFinder.previewSurfaceProvider)
//            preview?.previewSurfaceProvider = viewFinder.previewSurfaceProvider

            // Default ImageBufferFormat is JPEG
            // can be changed with imageCapture.Builder().setBufferFormat(ImageFormat.YUV_420_888)
            // YUV has high file size

            // ImageCapture
            imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    // We request aspect ratio but no resolution to match preview config, but letting
                    // CameraX optimize for whatever specific resolution best fits requested capture mode
                    .setTargetAspectRatio(screenAspectRatio)

                    // Set initial target rotation, we will have to call this again if rotation changes
                    // during the lifecycle of this use case
                    .setTargetRotation(rotation)
                    .build()

//            imageAnalysis = ImageAnalysis.Builder()
//                    .setTargetAspectRatio(screenAspectRatio)
////                    .setImageQueueDepth(1000)
//                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                    .build()
//
//
//            val imageAnalyzer = ImageAnalysis.Analyzer { imageProxy ->
//                val mediaImage = imageProxy.image
//                if (mediaImage != null) {
//                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
//                    // Pass image to an ML Kit Vision API
//                    // ...
//                    objectDetector.process(image)
//                            .addOnFailureListener {
//                                log("[Custom Model] = Failed ${it.message}")
//                                it.printStackTrace()
//                            }.addOnSuccessListener {
//                                log("[Custom Model] = Results [${it.size}]:")
//                                it.forEach {  item ->
//                                    item.labels.forEach { label ->
//                                        log("Labels[${label.index}] ${label.text} c=${label.confidence}")
//                                    }
//                                }
//                            }
//                } else
//                    log("Image is null")
//            }
//
//            imageAnalysis.setAnalyzer(mainExecutor, imageAnalyzer)

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
        imgPreview?.gone()
//        retry_capture_button?.gone()
        fabCheck?.gone()
        captureButton?.visible()

        /* Reset Controls when Retry is clicked
         * =========================
         * End
         */

        // Listener for button used to capture photo
        photoFile = createFile(outputDirectory)

        captureButton?.setOnClickListener { captureButton ->
            // Get a stable reference of the modifiable image capture use case

            progress?.visible()
            captureButton.gone()
            imageCapture?.let { imageCapture ->

//                imageCapture.setCropAspectRatio(Rational(300,300))
                val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                // Setup image capture listener which is triggered after photo has been taken
                try {
//                    imageCapture.takePicture(outputFileOptions, mainExecutor, imageSavedListener)
                    imageCapture.takePicture(mainExecutor, imageCroppedAndSavedListener)
                } catch (e: Exception) {
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