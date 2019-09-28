package me.paxana.ingredientreader.mvvm.offline

import android.content.Context
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.back
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

class OfflineInteractorImpl: OfflineInteractor {

    override fun parseTextToList(firebaseVisionTextString: String): MutableList<String> {
        return firebaseVisionTextString.toLowerCase(Locale.getDefault()).removePrefix("ingredients:").removeSuffix(".").split(",").map { it.trim() }.toMutableList()
    }

    override fun readCsvFromAssetFolder(inputStream: InputStream): List<String> {
        val csvLine: ArrayList<String> = ArrayList()
        var content: Array<String>?
        try
        {
            val br = BufferedReader(InputStreamReader(inputStream))
            for (line in br.lines())
            {
                content = line.split((",").toRegex()).dropLastWhile{ it.isEmpty() }.toTypedArray()
                csvLine.add(content[0].substringBefore(";"))
            }
            br.close()
        }
        catch (e: IOException) {
            e.printStackTrace()
        }
        return csvLine
    }
}