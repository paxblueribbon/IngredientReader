package me.paxana.ingredientreader.mvvm.firebase

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.orhanobut.logger.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

class FirebaseViewModel(private val firebaseInteractorImpl: FirebaseInteractorImpl, application: Application): AndroidViewModel(application) {

    private val subscriptions: CompositeDisposable by lazy { CompositeDisposable() }

    val visionText = MutableLiveData<FirebaseVisionText>()

    fun getCloudTextRecognition(uri: Uri) {
        Logger.d("getting cloud text recognition: %s", uri.toString())
        subscriptions.add(firebaseInteractorImpl.getCloudTextRecognition(uri, getApplication())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<FirebaseVisionText>(){
                override fun onSuccess(t: FirebaseVisionText) {
                    visionText.value = t
                }

                override fun onError(e: Throwable) {
                    Logger.e("err: %s", e.message)
                }

            }))
    }
    fun getOnDeviceTextRecognition(uri: Uri){
        subscriptions.add( firebaseInteractorImpl.getOnDeviceTextRecognition(uri, getApplication())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<FirebaseVisionText>(){
                override fun onSuccess(t: FirebaseVisionText) {
                    visionText.value = t
                }

                override fun onError(e: Throwable) {
                    Logger.e("err: %s", e.message)
                }

            })

        )

    }

    protected fun Disposable.autoClear() {
        subscriptions += this
    }

    public override fun onCleared() {
        subscriptions.dispose()
    }
}