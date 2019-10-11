package me.paxana.ingredientreader.utils

import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.amazonaws.amplify.generated.graphql.GetIngredientQuery
import com.orhanobut.logger.Logger
import com.pixplicity.easyprefs.library.Prefs
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.back
import io.fotoapparat.view.CameraView
import me.paxana.ingredientreader.models.Ingredient
import me.paxana.ingredientreader.mvvm.BaseViewModelFactory

fun CameraView.setup() {
    Fotoapparat.with(context)
        .into(this)
        .previewScaleType(ScaleType.CenterCrop)
        .lensPosition(back())
        .logger(loggers(logcat()))
        .cameraErrorCallback { Logger.e(it.localizedMessage) }
        .build()
}

enum class FotoapparatState{
    ON, OFF
}

fun List<String>.toIngredients(): List<Ingredient> {
    val ingList = mutableListOf<Ingredient>()
    this.forEach {
        val ing = Ingredient("id", it, type.Vegan.UNKNOWN, type.GlutenFree.UNKNOWN, 0)
        ingList.add(ing)
    }
    return ingList
}

fun GetIngredientQuery.GetIngredient.toIngredient(): Ingredient {
    return Ingredient(id()!!, name(), vegan()!!, glutenfree()!!, 0)
}

fun Switch.instantiate(key: String, default: Boolean) {
    this.isChecked = Prefs.getBoolean(key, default)
    this.setOnCheckedChangeListener { _, isChecked ->
        Prefs.putBoolean(key, isChecked)
    }

}

inline fun <reified T : ViewModel> Fragment.getViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null)
        ViewModelProviders.of(this).get(T::class.java)
    else
        ViewModelProviders.of(this, BaseViewModelFactory(creator)).get(T::class.java)
}

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null)
        ViewModelProviders.of(this).get(T::class.java)
    else
        ViewModelProviders.of(this, BaseViewModelFactory(creator)).get(T::class.java)
}