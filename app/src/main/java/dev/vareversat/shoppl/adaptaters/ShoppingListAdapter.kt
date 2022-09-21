package dev.vareversat.shoppl.adaptaters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import dev.vareversat.shoppl.R
import dev.vareversat.shoppl.databinding.ShoppingListItemBinding
import dev.vareversat.shoppl.models.ShoppingList

class ShoppingListAdapter(
    var context: Context,
    var shoppingListArray: ArrayList<Any>
) : BaseAdapter() {

    override fun getCount(): Int {
        return shoppingListArray.size
    }

    override fun getItem(p0: Int): ShoppingList {
        return shoppingListArray[p0] as ShoppingList
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ShoppingListItemBinding.inflate(layoutInflater)
        val shoppingItemNameView = binding.shoppingItemName
        val shoppingItemSizeView = binding.shoppingItemSize
        var size = 0
        for (key in getItem(p0).products.keys) {
            size += getItem(p0).products[key]!!.size
        }
        shoppingItemNameView.text = getItem(p0).name
        if (size == 0) {
            shoppingItemSizeView.text = context.getString(R.string.product_quantity_0)
        } else {
            shoppingItemSizeView.text = String.format(
                context.resources.getQuantityString(
                    R.plurals.products_quantity,
                    size
                ), size
            )
        }
        return binding.root
    }
}