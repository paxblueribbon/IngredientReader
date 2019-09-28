package me.paxana.ingredientreader.mvvm.aws

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amazonaws.amplify.generated.graphql.CreateIngFeedbackMutation
import com.amazonaws.amplify.generated.graphql.CreateIngredientMutation
import com.amazonaws.mobile.client.AWSMobileClient
import com.apollographql.apollo.api.Response
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import me.paxana.ingredientreader.models.Ingredient
import io.reactivex.observers.DisposableObserver
import me.paxana.ingredientreader.utils.ClientFactory
import org.apache.commons.collections4.map.ListOrderedMap
import type.GlutenFree
import type.Vegan

class AwsViewModel(private val awsInteractorImpl: AwsInteractorImpl): ViewModel() {

    private val disposable = CompositeDisposable()

    private var ingMap = ListOrderedMap<String, Ingredient?>()
    val ingredientsMap = MutableLiveData<ListOrderedMap<String, Ingredient?>>()
    
    private fun getIngredientHM(ingName: String) {
        disposable.add(
            awsInteractorImpl.getIngredient(ingName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Ingredient>(){
                    override fun onSuccess(t: Ingredient) {
                        ingMap[ingName] = t
                        Logger.d("ing is now %s, ingmap iss now %s", ingName, ingMap)
                        ingredientsMap.value = ingMap
                    }

                    override fun onError(e: Throwable) {
                        Logger.d("ing is now %s, ingmap iss now %s", ingName, ingMap)
                        Logger.e(e.localizedMessage)
                    }

                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun createIngredient(ingredient: Ingredient): Observable<Response<CreateIngredientMutation.Data>> {
        return awsInteractorImpl.createIngredient(ingredient).subscribeOn(Schedulers.io())
    }

    fun createFeedback(ingredient: Ingredient, shouldVegan: Vegan?, shouldGf: GlutenFree?): Observable<Response<CreateIngFeedbackMutation.Data>> {
        return awsInteractorImpl.createFeedback(ingredient, AWSMobileClient.getInstance().identityId, shouldVegan, shouldGf ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun removeIng(ingName: String) {
        ingMap.remove(ingName)
        ingredientsMap.value = ingMap
    }

    fun editIng(oldIngName: String, newIngName: String) {
        val index = ingMap.indexOf(oldIngName)
        ingMap.remove(oldIngName)
        ingMap.put(index, newIngName, null)
        getIngredientHM(newIngName)
        ingredientsMap.value = ingMap
    }

    fun fillInHashMap(listIng: List<String>) {
        val ingobs = Observable.fromIterable(listIng)
        disposable.add(
            ingobs.subscribeWith(object : DisposableObserver<String>(){
                override fun onComplete() {
                    Logger.d("OnComplete")
                }
                override fun onNext(t: String) {
                    ingMap[t] = null
                    getIngredientHM(t)
                }

                override fun onError(e: Throwable) {
                    Logger.e(e.localizedMessage)
                }

            })
        )
    }
}