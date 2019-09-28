package me.paxana.ingredientreader.mvvm.aws

import android.util.Log
import com.amazonaws.amplify.generated.graphql.CreateIngFeedbackMutation
import com.amazonaws.amplify.generated.graphql.CreateIngredientMutation
import com.amazonaws.amplify.generated.graphql.GetIngredientQuery
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.Single
import me.paxana.ingredientreader.utils.ClientFactory
import me.paxana.ingredientreader.models.Ingredient
import me.paxana.ingredientreader.utils.toIngredient
import type.*
import javax.annotation.Nonnull

class AwsInteractorImpl: AwsInteractor {
    override fun createFeedback(ingredient: Ingredient, userId: String, shouldVegan: Vegan?, shouldGf: GlutenFree?): Observable<Response<CreateIngFeedbackMutation.Data>> {
        return Observable.create{emitter ->
            val createIngFeedbackMutation = CreateIngFeedbackInput.builder()
                .userId(userId)
                .name(ingredient.name)
                .shouldGf(shouldGf)
                .shouldVegan(shouldVegan)
                .build()
            ClientFactory.appSyncClient()!!.mutate(CreateIngFeedbackMutation.builder().input(createIngFeedbackMutation).build())
                .enqueue(object : GraphQLCall.Callback<CreateIngFeedbackMutation.Data>(){
                    override fun onFailure(e: ApolloException) {
                        Logger.e("Error: %s", e.localizedMessage)
                    }

                    override fun onResponse(response: Response<CreateIngFeedbackMutation.Data>) {
                        Logger.d("Onresponse: %s", response.data().toString() )
                        emitter.onNext(response)
                    }
                })
        }
    }

    override fun getIngredient(ingName: String): Single<Ingredient> {
        return Single.create {
            Log.d("Getting Ingredients", "now")
            val getIngredientCallback = object : GraphQLCall.Callback<GetIngredientQuery.Data>() {
                override fun onResponse(response: Response<GetIngredientQuery.Data>) {
                    if (response.data() != null && response.data()!!.ingredient != null) {
                        it.onSuccess(response.data()!!.ingredient!!.toIngredient())
                        Log.d("Found ingredient", response.data()!!.ingredient?.name())
                    }
                    else {
                        it.onSuccess(Ingredient("1312", ingName, Vegan.UNKNOWN, GlutenFree.UNKNOWN, 0 ))
                        Logger.d("Oh we got a nully boy right here")
                    }
                }
                override fun onFailure(e: ApolloException) {
                    Logger.e("getIngredientCallback error = %s, on ingredient %s", e.message!!, ingName)
                }
            }
            //change network_only to cache_first after testing
            ClientFactory.appSyncClient()!!.query(GetIngredientQuery.builder().name(ingName).build())
                .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
                .enqueue(getIngredientCallback)
        }


    }
    override fun createIngredient(ingredient: Ingredient): Observable<Response<CreateIngredientMutation.Data>> {
        return Observable.create { emitter ->
            val mutationCallback = object: GraphQLCall.Callback<CreateIngredientMutation.Data>() {
                override fun onResponse(response: Response<CreateIngredientMutation.Data>) {
                    emitter.onNext(response)
                }

                override fun onFailure(@Nonnull e: ApolloException) {
                    Logger.e("Err: %s", e.localizedMessage)
                }
            }

            fun createIngredient(ingredient: Ingredient) {
                val createIngredientInput = CreateIngredientInput.builder().name(ingredient.name).build()
                ClientFactory.appSyncClient()!!.mutate(CreateIngredientMutation.builder().input(createIngredientInput).build())
                    .enqueue(mutationCallback)
            }

        }
    }

}