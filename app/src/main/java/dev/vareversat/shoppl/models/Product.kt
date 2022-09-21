package dev.vareversat.shoppl.models

class Product(var name: String, var quantity: Int, var unit: String, var checked: Boolean = false) {

    override fun toString(): String {
        return "Product(name='$name', quantity=$quantity, unit='$unit', checked=$checked)"
    }
}