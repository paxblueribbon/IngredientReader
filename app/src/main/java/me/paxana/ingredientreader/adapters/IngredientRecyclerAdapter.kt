package me.paxana.ingredientreader.adapters

import me.paxana.ingredientreader.models.Ingredient
import android.content.Context
import android.graphics.Color
import android.graphics.LightingColorFilter
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_item_row.view.*
import me.paxana.ingredientreader.R
import org.apache.commons.collections4.map.ListOrderedMap
import type.GlutenFree
import type.Vegan

class IngredientRecyclerAdapter(private val ingredientRecyclerClickAdapter: IngredientRecyclerClickAdapter, private var ingredients: ListOrderedMap<String, Ingredient?>) : RecyclerView.Adapter<IngredientRecyclerAdapter.IngHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_row, parent, false)
        return IngHolder(inflatedView, ingredientRecyclerClickAdapter)
    }

    fun updateIngs(newHashMap: ListOrderedMap<String, Ingredient?>) {
        ingredients = newHashMap
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return ingredients.size
    }

    override fun onBindViewHolder(holder: IngHolder, position: Int) {
            var ingredient = ingredients.toList()[position].second
       if (ingredient == null) {
                ingredient = Ingredient("1312", ingredients.toList()[position].first, Vegan.UNKNOWN, GlutenFree.UNKNOWN, 0  )
            }
        holder.bindIngredient(ingredient)
    }

    class IngHolder(v: View, recyclerClickAdapter: IngredientRecyclerClickAdapter) : RecyclerView.ViewHolder(v), View.OnCreateContextMenuListener {

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            menu?.setHeaderTitle(v?.ingredient_name?.text)
            val delete = menu?.add(this.adapterPosition, 121, 0, "Delete")
            delete?.setOnMenuItemClickListener(mDeleteClickListner)
            val edit = menu?.add(this.adapterPosition, 122, 1, "Edit")
            edit?.setOnMenuItemClickListener(mEditClickListener)
            val veganFeedback = menu?.add(this.adapterPosition, 123, 2, "Vegan Feedback")
            veganFeedback?.setOnMenuItemClickListener(mVeganFeedbackListener)
            val gfFeedback = menu?.add(this.adapterPosition, 124, 3, "GF Feedback")
            gfFeedback?.setOnMenuItemClickListener(mGfFeedbackListener)

        }

        private val mVeganFeedbackListener = MenuItem.OnMenuItemClickListener {
            recyclerClickAdapter.veganFeedback(ingredient!!)
            true
        }
        private val mGfFeedbackListener = MenuItem.OnMenuItemClickListener {
            recyclerClickAdapter.gfFeedback(ingredient!!)
            true
        }

        private val mDeleteClickListner= MenuItem.OnMenuItemClickListener {
            recyclerClickAdapter.ingredientRemoved(ingredient!!)
            true
        }
        private val mEditClickListener = MenuItem.OnMenuItemClickListener {
            v.editText.setText(v.ingredient_name.text.toString())
            v.ingredient_name.visibility = View.GONE
            v.editText.visibility = View.VISIBLE
            v.button2.visibility = View.VISIBLE

            v.button2.setOnClickListener {
                v.ingredient_name.text = v.editText.text.toString()
                v.editText.visibility = View.GONE
                v.button2.visibility = View.GONE
                v.ingredient_name.visibility = View.VISIBLE
                recyclerClickAdapter.ingredientUpdated(ingredient!!, v.editText.text.toString())
            }
            true
        }

        private var view : View = v
        private var context: Context = v.context
        private var ingredient : Ingredient? = null

        init {
            v.setOnCreateContextMenuListener(this)
        }


        companion object {
            private val INGREDIENT_KEY = "INGREDIENT"
        }

        fun bindIngredient(ingredient: Ingredient) {
            this.ingredient = ingredient
            view.ingredient_name.text = ingredient.name
            when (ingredient.vegan) {
                Vegan.VEGAN ->  view.veganImageView.colorFilter = LightingColorFilter(Color.parseColor("#0189ff"), Color.GREEN)
                Vegan.NON_VEGAN ->  view.veganImageView.colorFilter = LightingColorFilter(Color.parseColor("#0189ff"), Color.RED)
                Vegan.UNKNOWN ->  view.veganImageView.colorFilter = LightingColorFilter(Color.parseColor("#0189ff"), Color.GRAY)
            }
            when (ingredient.gf) {
                GlutenFree.GLUTENFREE ->  view.gfImageView.colorFilter = LightingColorFilter(Color.parseColor("#0189ff"), Color.GREEN)
                GlutenFree.CONTAINS_GLUTEN ->  view.gfImageView.colorFilter = LightingColorFilter(Color.parseColor("#0189ff"), Color.RED)
                GlutenFree.UNKNOWN ->  view.gfImageView.colorFilter = LightingColorFilter(Color.parseColor("#0189ff"), Color.GRAY)
            }

        }
    }

}

