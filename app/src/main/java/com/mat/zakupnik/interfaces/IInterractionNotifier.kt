package com.mat.zakupnik.interfaces

interface IInterractionNotifier : IDeletionNotifier {
    fun notifyProductDetailsClicked(name : String)
}