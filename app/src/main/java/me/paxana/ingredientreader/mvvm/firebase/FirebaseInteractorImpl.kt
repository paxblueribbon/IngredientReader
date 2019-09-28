package me.paxana.ingredientreader.mvvm.firebase

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import io.reactivex.Observable
import io.reactivex.Single
import java.io.IOException
import java.net.URI

class FirebaseInteractorImpl: FirebaseInteractor {

    override fun getCloudTextRecognition(uri: Uri, context: Context): Single<FirebaseVisionText> {
        return Single.create {emitter ->
            try {
                val image = FirebaseVisionImage.fromFilePath(context, uri)
                val detector = FirebaseVision.getInstance().cloudTextRecognizer
                detector.processImage(image).
                    addOnSuccessListener {
                        emitter.onSuccess(it)
                    }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun getOnDeviceTextRecognition(uri: Uri, context: Context): Single<FirebaseVisionText> {
        return Single.create { emitter ->
            Log.d("getOnDev", "running")

            try {
                val image = FirebaseVisionImage.fromFilePath(context, uri)
                val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
                detector.processImage(image).
                    addOnSuccessListener {
                        emitter.onSuccess(it)
                    }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}