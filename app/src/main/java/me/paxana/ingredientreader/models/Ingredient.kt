package me.paxana.ingredientreader.models

import com.amazonaws.amplify.generated.graphql.ListIngredientsQuery
import type.GlutenFree
import type.Vegan

data class Ingredient(val id: String, val name: String, var vegan: Vegan, var gf: GlutenFree, val popularity: Int ) :
    ListIngredientsQuery.Item("Ingredient", id, name, vegan, gf, null)