package dev.vareversat.shoppl.models

class ShoppingList(var name: String, val products: HashMap<String, ArrayList<Product>>) {

    override fun toString(): String {
        return "\nShoppingItem(name='$name', products=$products)"
    }
}