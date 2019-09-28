package me.paxana.ingredientreader.mvvm.aws

import com.amazonaws.amplify.generated.graphql.CreateIngFeedbackMutation
import com.amazonaws.amplify.generated.graphql.CreateIngredientMutation
import com.apollographql.apollo.api.Response
import io.reactivex.Observable
import io.reactivex.Single
import me.paxana.ingredientreader.models.Ingredient
import type.GlutenFree
import type.Vegan

interface AwsInteractor {
    fun getIngredient(ingName: String): Single<Ingredient>
    fun createIngredient(ingredient: Ingredient): Observable<Response<CreateIngredientMutation.Data>>
    fun createFeedback(ingredient: Ingredient, userId: String, shouldVegan: Vegan?, shouldGf: GlutenFree?): Observable<Response<CreateIngFeedbackMutation.Data>>
}