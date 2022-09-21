package dev.vareversat.shoppl.adaptaters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import dev.vareversat.shoppl.R
import dev.vareversat.shoppl.databinding.ProductCategoryBinding
import dev.vareversat.shoppl.databinding.ProductItemBinding
import dev.vareversat.shoppl.models.Product


class ProductAdapter(
    var context: Context,
    var productCategory: Array<String>,
    var expandableProductList: HashMap<String, ArrayList<Product>>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return expandableProductList.size
    }

    override fun getGroup(p0: Int): String {
        return productCategory[p0]
    }

    override fun getChildrenCount(p0: Int): Int {
        return expandableProductList[productCategory[p0]]!!.size
    }

    override fun getChild(p0: Int, p1: Int): Product {
        return expandableProductList[productCategory[p0]]!![p1]
    }

    override fun getGroupId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getChildId(p0: Int, p1: Int): Long {
        return p0.toLong()
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getChildView(p0: Int, p1: Int, p2: Boolean, p3: View?, p4: ViewGroup?): View {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ProductItemBinding.inflate(layoutInflater)

        val productNameView = binding.productName
        val productQuantityView = binding.productQuantity
        val productCheckedView = binding.productCheckBox
        productCheckedView.setOnCheckedChangeListener { _, checked ->
            getChild(p0, p1).checked = checked
        }
        productNameView.text = getChild(p0, p1).name
        productCheckedView.isChecked = getChild(p0, p1).checked
        productQuantityView.text = String.format(
            context.getString(R.string.string_with_spaces),
            getChild(p0, p1).quantity.toString(), getChild(p0, p1).unit
        )
        return binding.root
    }

    override fun getGroupView(p0: Int, p1: Boolean, p2: View?, p3: ViewGroup?): View {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ProductCategoryBinding.inflate(layoutInflater)

        val productCategoryView = binding.productCategory
        productCategoryView.text = getGroup(p0)
        return binding.root
    }


}
