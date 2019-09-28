package me.paxana.ingredientreader.adapters

import me.paxana.ingredientreader.models.Ingredient

interface IngredientRecyclerClickAdapter {
    fun ingredientUpdated(oldIngredient: Ingredient, newIngredient: String)
    fun ingredientRemoved(ingredient: Ingredient)
    fun veganFeedback(ingredient: Ingredient)
    fun gfFeedback(ingredient: Ingredient)
}