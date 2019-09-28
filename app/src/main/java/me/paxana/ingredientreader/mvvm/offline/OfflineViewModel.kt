package me.paxana.ingredientreader.mvvm.offline

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.orhanobut.logger.Logger
import java.io.InputStream

class OfflineViewModel(private val offlineInteractor: OfflineInteractor): ViewModel() {

    val ingredientsString by lazy { MutableLiveData<List<String>>() }
//TODO: SET UP DAGGER

//    init {
//        DaggerOfflineViewModelComponent.builder()
//            .offlineInteractorImplModule(OfflineInteractorImplModule())
//            .build()
//            .inject(this)
//    }
    fun parseTextToList(firebaseVisionTextString: String) {
        Logger.d("Parsing text to list, fbvts = %s", firebaseVisionTextString)
        ingredientsString.value = offlineInteractor.parseTextToList(firebaseVisionTextString)
    }

    fun readCsvFromAssetFolder(inputStream: InputStream): List<String>{
        return offlineInteractor.readCsvFromAssetFolder(inputStream)
    }
}