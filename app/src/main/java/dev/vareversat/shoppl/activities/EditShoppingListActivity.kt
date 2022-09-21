package dev.vareversat.shoppl.activities

import android.app.ActionBar
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.vareversat.shoppl.R
import dev.vareversat.shoppl.adaptaters.ProductAdapter
import dev.vareversat.shoppl.databinding.ConfirmDeleteShopingListDialogBinding
import dev.vareversat.shoppl.databinding.EditShoppingListActivityBinding
import dev.vareversat.shoppl.databinding.ProductDialogBinding
import dev.vareversat.shoppl.databinding.ShoppingListDialogBinding
import dev.vareversat.shoppl.models.Product
import dev.vareversat.shoppl.models.ShoppingList
import dev.vareversat.shoppl.services.SharedPreferencesService


class EditShoppingListActivity : AppCompatActivity() {

    private lateinit var shoppingList: ShoppingList
    private lateinit var binding: EditShoppingListActivityBinding
    private var index: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditShoppingListActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val actionBar: ActionBar? = actionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        index = intent.getIntExtra("shoppingListIndex", 0)
        getShoppingList()
        binding.shoppingListEditName.text = shoppingList.name

        val adapter =
            ProductAdapter(this, shoppingList.products.keys.toTypedArray(), shoppingList.products)
        binding.productList.setAdapter(adapter)
        binding.productList.expandGroup(0)
        binding.productList.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            showEditProductDialog(groupPosition, childPosition)
            true
        }
        binding.productList.setOnGroupClickListener { _, _, i, _ ->
            binding.productList.expandGroup(i)
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.edit_shopping_list_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_shopping_list -> {
                showConfirmDeleteShoppingList()
                true
            }
            R.id.edit_shopping_list -> {
                showEditShoppingListNameDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showConfirmDeleteShoppingList() {
        val dialog = Dialog(this)
        dialog.setTitle("Delete shopping list")
        val dialogBinding = ConfirmDeleteShopingListDialogBinding.inflate(layoutInflater)
        dialogBinding.confirmButton.setOnClickListener {
            val tinyDB = SharedPreferencesService(applicationContext)
            val list = tinyDB.getShoppingLists("shopping_list", ShoppingList::class.java)
            list.removeAt(index!!)
            tinyDB.saveShoppingLists("shopping_list", list)
            dialog.dismiss()
            finish()
        }
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(dialogBinding.root)
        dialog.show()
    }

    private fun getShoppingList() {
        val tinyDB = SharedPreferencesService(applicationContext)
        val list = tinyDB.getShoppingLists("shopping_list", ShoppingList::class.java)
        shoppingList = list[index!!] as ShoppingList
    }

    private fun saveShoppingList() {
        val tinyDB = SharedPreferencesService(applicationContext)
        val list = tinyDB.getShoppingLists("shopping_list", ShoppingList::class.java)
        list[index!!] = shoppingList
        tinyDB.saveShoppingLists("shopping_list", list)
        binding.productList.setAdapter(
            ProductAdapter(
                this,
                shoppingList.products.keys.toTypedArray(),
                shoppingList.products
            )
        )
    }

    private fun showEditShoppingListNameDialog() {
        val dialog = Dialog(this)
        dialog.setTitle("Edit shopping list name")
        dialog.setContentView(R.layout.shopping_list_dialog)
        val dialogBinding = ShoppingListDialogBinding.inflate(layoutInflater)
        dialogBinding.createShoppingListBtn.text = getString(R.string.update)
        dialogBinding.shoppingListInputText.setText(shoppingList.name)
        dialogBinding.createShoppingListBtn.setOnClickListener {
            shoppingList.name = dialogBinding.shoppingListInputText.text.toString()
            binding.shoppingListEditName.text = shoppingList.name
            saveShoppingList()
            dialog.dismiss()
            Toast.makeText(this, "Shopping list name updated", Toast.LENGTH_SHORT).show()
        }
        dialog.setContentView(dialogBinding.root)
        dialog.show()
    }

    fun showAddProductDialog(@Suppress("UNUSED_PARAMETER") view: View) {
        val dialog = Dialog(this)
        dialog.setTitle("New product")
        dialog.setContentView(R.layout.product_dialog)
        val dialogBinding = ProductDialogBinding.inflate(layoutInflater)
        dialogBinding.deleteProductBtn.visibility = View.GONE
        dialogBinding.createProductBtn.setOnClickListener {
            val quantity = dialogBinding.productQuantityInputText.text.toString()
            val category = dialogBinding.categoryInputText.text.toString()
            val checkedCategory = category.ifEmpty { "No category" }
            val quantityInt = try {
                quantity.toInt()
            } catch (e: NumberFormatException) {
                0
            }
            val productList = shoppingList.products[checkedCategory]
            if (productList.isNullOrEmpty()) {
                shoppingList.products[checkedCategory] = arrayListOf(
                    Product(
                        dialogBinding.productNameInputText.text.toString(),
                        quantityInt,
                        dialogBinding.productUnitInputText.text.toString()
                    )
                )
            } else {
                productList.add(
                    Product(
                        dialogBinding.productNameInputText.text.toString(),
                        quantityInt,
                        dialogBinding.productUnitInputText.text.toString()
                    )
                )
            }

            saveShoppingList()
            dialog.dismiss()
            Toast.makeText(
                this,
                dialogBinding.productNameInputText.text.toString() + " added to " + shoppingList.name,
                Toast.LENGTH_SHORT
            ).show()
        }
        dialog.setContentView(dialogBinding.root)
        dialog.show()
    }

    private fun showEditProductDialog(groupPosition: Int, childPosition: Int) {
        val dialog = Dialog(this)
        dialog.setTitle("Edit product")
        dialog.setContentView(R.layout.product_dialog)
        val dialogBinding = ProductDialogBinding.inflate(layoutInflater)
        dialogBinding.createProductBtn.text = getString(R.string.update)
        dialogBinding.productNameInputText.setText(
            shoppingList.products[shoppingList.products.keys.toTypedArray()[groupPosition]]?.get(
                childPosition
            )?.name
        )
        dialogBinding.productQuantityInputText.setText(
            shoppingList.products[shoppingList.products.keys.toTypedArray()[groupPosition]]?.get(
                childPosition
            )?.quantity.toString()
        )
        dialogBinding.productUnitInputText.setText(
            shoppingList.products[shoppingList.products.keys.toTypedArray()[groupPosition]]?.get(
                childPosition
            )?.unit
        )
        dialogBinding.categoryInputText.setText(
            shoppingList.products.keys.toTypedArray()[groupPosition]
        )
        dialogBinding.createProductBtn.setOnClickListener {
            val newCategory = dialogBinding.categoryInputText.text.toString()
            val checkedNewCategory = newCategory.ifEmpty { "No category" }
            val oldCategory = shoppingList.products.keys.toTypedArray()[groupPosition]
            val product = Product(
                dialogBinding.productNameInputText.text.toString(),
                0,
                dialogBinding.productUnitInputText.text.toString()
            )
            val quantity = dialogBinding.productQuantityInputText.text.toString()
            try {
                product.quantity = quantity.toInt()
            } catch (e: NumberFormatException) {
                product.quantity = 0
            }
            if (checkedNewCategory == oldCategory) {
                shoppingList.products[shoppingList.products.keys.toTypedArray()[groupPosition]]?.set(
                    childPosition,
                    product
                )
            } else {
                shoppingList.products[shoppingList.products.keys.toTypedArray()[groupPosition]]?.removeAt(
                    childPosition
                )
                if (shoppingList.products[shoppingList.products.keys.toTypedArray()[groupPosition]]?.isEmpty() == true) {
                    shoppingList.products.remove(shoppingList.products.keys.toTypedArray()[groupPosition])
                }
                val products = shoppingList.products[checkedNewCategory]
                if (products.isNullOrEmpty()) {
                    shoppingList.products[checkedNewCategory] = arrayListOf(product)
                } else {
                    products.add(product)
                }
            }
            saveShoppingList()
            dialog.dismiss()
            Toast.makeText(
                this,
                dialogBinding.productNameInputText.text.toString() + " updated",
                Toast.LENGTH_SHORT
            ).show()
        }
        dialogBinding.deleteProductBtn.setOnClickListener {
            shoppingList.products[shoppingList.products.keys.toTypedArray()[groupPosition]]?.removeAt(
                childPosition
            )
            saveShoppingList()
            dialog.dismiss()
            Toast.makeText(
                this,
                dialogBinding.productNameInputText.text.toString() + " deleted from " + shoppingList.name,
                Toast.LENGTH_SHORT
            ).show()
        }
        dialog.setContentView(dialogBinding.root)
        dialog.show()
    }

    fun saveCheckedProducts(@Suppress("UNUSED_PARAMETER") view: View) {
        saveShoppingList()
        Toast.makeText(
            this,
            "Checked product(s) saved",
            Toast.LENGTH_SHORT
        ).show()
    }
}