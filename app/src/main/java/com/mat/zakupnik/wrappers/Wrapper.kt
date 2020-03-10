package com.mat.zakupnik.wrappers

import java.time.LocalDate
import kotlin.collections.*

class Wrapper {
    data class ProductDetails(val price: Float = 0f, val expirationDate: LocalDate = LocalDate.now(),
                              val quantity: Int = 0, val volume: Int = 0)

    data class TwoLayerHashMap(val map: kotlin.collections.HashMap<
            String, kotlin.collections.HashMap<String, ProductDetails>> = hashMapOf())

    data class MutableSet(val set: kotlin.collections.MutableSet<String> = mutableSetOf())

    data class HashMap(val map: kotlin.collections.HashMap<String, ProductDetails> = hashMapOf())
}