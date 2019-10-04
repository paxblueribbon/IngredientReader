package me.paxana.ingredientreader.activities

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.amazonaws.amplify.generated.graphql.CreateIngFeedbackMutation
import com.apollographql.apollo.api.Response
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.orhanobut.logger.Logger
import io.reactivex.observers.DisposableObserver
import kotlinx.android.synthetic.main.activity_results.*
import me.paxana.ingredientreader.R
import me.paxana.ingredientreader.adapters.IngredientRecyclerAdapter
import me.paxana.ingredientreader.adapters.IngredientRecyclerClickAdapter
import me.paxana.ingredientreader.utils.getViewModel
import me.paxana.ingredientreader.models.Ingredient
import me.paxana.ingredientreader.mvvm.aws.AwsInteractorImpl
import me.paxana.ingredientreader.mvvm.aws.AwsViewModel
import me.paxana.ingredientreader.mvvm.firebase.FirebaseInteractorImpl
import me.paxana.ingredientreader.mvvm.firebase.FirebaseViewModel
import me.paxana.ingredientreader.mvvm.offline.OfflineInteractorImpl
import me.paxana.ingredientreader.mvvm.offline.OfflineViewModel
import org.apache.commons.collections4.map.ListOrderedMap
import type.GlutenFree
import type.Vegan

class ResultsActivity : AppCompatActivity() {

    private val ingredientRecyclerAdapter =
        IngredientRecyclerAdapter(object: IngredientRecyclerClickAdapter{
            override fun veganFeedback(ingredient: Ingredient) {
                //todo move this logic elsewhere
                val veganOptions = listOf("Vegan", "Not Vegan", "Could Go Either Way")
                var shouldVegan: Vegan = Vegan.UNKNOWN
                MaterialDialog(this@ResultsActivity).show{
                    title(text = ingredient.name)
                    message(text = "Hey, I think this ingredient is actually: ")
                    val selected = when (ingredient.vegan) {
                        Vegan.VEGAN -> 0
                        Vegan.NON_VEGAN -> 1
                        Vegan.UNKNOWN -> 2
                    }
                    listItemsSingleChoice(items = veganOptions, initialSelection = selected, waitForPositiveButton = false) {
                            _, index, _ ->

                        if (index == 0) {
                            shouldVegan = Vegan.VEGAN
                        }
                        else if (index == 1){
                            shouldVegan = Vegan.NON_VEGAN
                        }
                    }
                    positiveButton(text = "Ok") {
                        awsViewModel.createFeedback(ingredient, shouldVegan, null).subscribeWith(object: DisposableObserver<Response<CreateIngFeedbackMutation.Data>>(){
                            override fun onComplete() {
                            }

                            override fun onNext(t: Response<CreateIngFeedbackMutation.Data>) {
                                Logger.d("Success!  %s", t.data().toString())
                                Toast.makeText(this@ResultsActivity, "Thanks for your feedback!!", Toast.LENGTH_SHORT).show()
                            }

                            override fun onError(e: Throwable) {
                                Toast.makeText(this@ResultsActivity, "Sozz we were unable to submit your feedback :(", Toast.LENGTH_SHORT).show()
                                Logger.e("Vegan error: %s", e.localizedMessage)
                            }

                        })
                    }
                    negativeButton(text = "Nvm") {materialDialog -> materialDialog.dismiss() }
                }
            }

            override fun gfFeedback(ingredient: Ingredient) {
                //todo move this logic elsewhere
                val gfOptions = listOf("Gluten-Free", "Not Gluten-Free", "Could Go Either Way")
                var shouldGf = GlutenFree.UNKNOWN
                MaterialDialog(this@ResultsActivity).show{
                    title(text = ingredient.name)
                    message(text = "Hey, I think this ingredient is actually: ")
                    val selected = when (ingredient.glutenfree()) {
                        GlutenFree.GLUTENFREE -> 0
                        GlutenFree.CONTAINS_GLUTEN -> 1
                        GlutenFree.UNKNOWN -> 2
                        null -> 2
                    }
                    listItemsSingleChoice(items = gfOptions, initialSelection = selected, waitForPositiveButton = false) {
                            _, index, _ ->
                        if (index == 0) {
                            shouldGf = GlutenFree.GLUTENFREE
                        }
                        else if (index == 1){
                            shouldGf = GlutenFree.CONTAINS_GLUTEN
                        }
                    }
                    positiveButton(text = "Ok") {
                        awsViewModel.createFeedback(ingredient, null, shouldGf).subscribeWith(object: DisposableObserver<Response<CreateIngFeedbackMutation.Data>>(){
                            override fun onComplete() {
                            }

                            override fun onNext(t: Response<CreateIngFeedbackMutation.Data>) {
                                Logger.d("Success!  %s", t.data().toString())
                                Toast.makeText(this@ResultsActivity, "Thanks for your feedback!!", Toast.LENGTH_SHORT).show()
                            }

                            override fun onError(e: Throwable) {
                                Toast.makeText(this@ResultsActivity, "Sozz we were unable to submit your feedback :(", Toast.LENGTH_SHORT).show()
                                Logger.e("Vegan error: %s", e.localizedMessage)
                            }

                        })
                    }
                    negativeButton(text = "Nvm") {materialDialog -> materialDialog.dismiss() }
                }
            }

            override fun ingredientUpdated(oldIngredient: Ingredient, newIngredient: String) {
                awsViewModel.editIng(oldIngredient.name, newIngredient)
                Toast.makeText(applicationContext, "changing: ${oldIngredient.name} to $newIngredient", Toast.LENGTH_SHORT).show()
            }

            override fun ingredientRemoved(ingredient: Ingredient) {
                Toast.makeText(applicationContext, "Removing ingredient: $ingredient", Toast.LENGTH_SHORT).show()
                awsViewModel.removeIng(ingredient.name)
            }
        }, ListOrderedMap())
    private lateinit var linearLayoutManager: LinearLayoutManager

    private val offlineViewModel: OfflineViewModel by lazy{
        getViewModel{OfflineViewModel(OfflineInteractorImpl())}
    }

    private val awsViewModel: AwsViewModel by lazy{
        getViewModel{ AwsViewModel(AwsInteractorImpl()) }
    }

    private val firebaseViewModel: FirebaseViewModel by lazy{
        getViewModel{FirebaseViewModel(FirebaseInteractorImpl(), application)}
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private val ingredientStringObserver = Observer<List<String>> {
        it?.let {
            val map: LinkedHashMap<String, Ingredient?> = linkedMapOf()
            for (i in it) {
                map[i] = null
            }
            awsViewModel.fillInHashMap(it)
        }
    }

    private val mapObserver = Observer<ListOrderedMap<String, Ingredient?>> {
        it.let {
            runOnUiThread{
                ingredientRecyclerAdapter.updateIngs(it)
            }
            Logger.d("Hashmap is now %s", it)
        }
    }

    private val firebaseTextObserver = Observer<FirebaseVisionText> {
        it.let {
            offlineViewModel.parseTextToList(it.text)
        }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            121 -> item.groupId
        }
        return super.onContextItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        offlineViewModel.ingredientsString.observe(this, ingredientStringObserver)
        firebaseViewModel.visionText.observe(this, firebaseTextObserver)
        awsViewModel.ingredientsMap.observe(this, mapObserver)

        val fileUri = Uri.parse(intent.getStringExtra("INTENT_URI_ID"))

        croppedImageIV.setImageURI(fileUri)

        if (ingredientsListRV.adapter == null) {
            initRecyclerView()
        }

        if (firebaseViewModel.visionText.value == null) {
            firebaseViewModel.getCloudTextRecognition(fileUri)
        }
    }

    private fun initRecyclerView() {
        Logger.d("Running initRV")

        runOnUiThread {
            linearLayoutManager = LinearLayoutManager(this)
            ingredientsListRV.layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(this , linearLayoutManager.orientation)
            ingredientsListRV.addItemDecoration(dividerItemDecoration)
            ingredientsListRV.adapter = ingredientRecyclerAdapter
        }
    }
}
