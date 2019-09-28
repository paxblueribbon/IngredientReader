package me.paxana.ingredientreader.mvvm.firebase

import android.content.Context
import android.net.Uri
import com.google.firebase.ml.vision.text.FirebaseVisionText
import io.reactivex.Single

interface FirebaseInteractor {
    fun getCloudTextRecognition(uri: Uri, context: Context): Single<FirebaseVisionText>
    fun getOnDeviceTextRecognition(uri: Uri, context: Context): Single<FirebaseVisionText>
}