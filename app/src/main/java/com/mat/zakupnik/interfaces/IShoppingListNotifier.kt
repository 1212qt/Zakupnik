package com.mat.zakupnik.interfaces

interface IShoppingListNotifier {
    fun notifyParentCollapsed(name : String)
    fun notifyChildCollapsed(name : String)
    fun notifyParentDeleted(name : String)
    fun notifyChildDeleted(name : String)
    fun notifyProductDetailsClicked(name : String)
}