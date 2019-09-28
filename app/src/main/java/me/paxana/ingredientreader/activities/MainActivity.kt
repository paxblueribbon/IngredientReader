package me.paxana.ingredientreader.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.orhanobut.logger.Logger
import com.theartofdev.edmodo.cropper.CropImage
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.back
import io.fotoapparat.view.CameraView
import kotlinx.android.synthetic.main.activity_main.*
import me.paxana.ingredientreader.*
import me.paxana.ingredientreader.utils.ClientFactory
import me.paxana.ingredientreader.utils.FotoapparatState
import me.paxana.ingredientreader.utils.resultsActivityIntent
import me.paxana.ingredientreader.utils.setup
import java.io.File

class MainActivity : AppCompatActivity() {

    val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    private lateinit var fotoapparat: Fotoapparat
    private var fotoapparatState : FotoapparatState? = null
    val filename = "ingredientList.png"
    private val sd: File = Environment.getExternalStorageDirectory()
    val dest = File(sd, filename)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ClientFactory.init(this)

        if (hasNoPermissions()) {
            requestPermission()
        } else {
            createFotoapparat()
        }

        fab_camera.setOnClickListener {
            takePhoto()
        }
    }

    private fun createFotoapparat(){
        val cameraView = findViewById<CameraView>(R.id.camera_view)
        fotoapparat = Fotoapparat
            .with(this)
            .into(cameraView)
            .previewScaleType(ScaleType.CenterCrop)
            .lensPosition(back())
            .logger(loggers(logcat()))
            .cameraErrorCallback{error -> println("Recorder errors: $error")}
            .build()
    }

    override fun onStart() {
        super.onStart()
        if (hasNoPermissions()) {
            requestPermission()
        }else{
            fotoapparat.start()
            fotoapparatState = FotoapparatState.ON
        }
    }

    override fun onResume() {
        super.onResume()
        if(!hasNoPermissions() && fotoapparatState == FotoapparatState.OFF){
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        fotoapparat.stop()
        FotoapparatState.OFF
    }

    private fun hasNoPermissions(): Boolean{
        return ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, permissions,0)
        camera_view.setup()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val resultz = CropImage.getActivityResult(data)

            if (resultCode == RESULT_OK) {
                val resultUri = resultz.uri
                Logger.d("resultz URI: %s", resultUri.path)

                startActivity(resultsActivityIntent(resultUri.toString()))

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = resultz.error
                Log.e("Err: Line 248", error.message)
            }
        }
    }

    private fun takePhoto() {
        if (hasNoPermissions()) {
            requestPermission()
        } else{
            Log.d("Taking photo", filename)
            fotoapparat.takePicture().saveToFile(dest).whenAvailable {

                CropImage.activity(Uri.fromFile(dest))
                    .start(this)
            }
        }
    }
}
