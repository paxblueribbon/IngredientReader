package me.paxana.ingredientreader.mvvm.offline

import java.io.InputStream

interface OfflineInteractor {
    fun parseTextToList(firebaseVisionTextString: String):MutableList<String>
    fun readCsvFromAssetFolder(inputStream: InputStream): List<String>
}