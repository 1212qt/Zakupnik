package com.mat.zakupnik.activities

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mat.zakupnik.R
import com.mat.zakupnik.adapters.GranaryElvAdapter
import com.mat.zakupnik.handlers.FileHandler
import com.mat.zakupnik.interfaces.IInterractionNotifier
import com.mat.zakupnik.wrappers.Wrapper
import com.mat.zakupnik.wrappers.Wrapper.ProductDetails
import com.mat.zakupnik.dialogs.ProductDialog
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class GranaryActivity : AppCompatActivity(), IInterractionNotifier {

    private val TAG = "GranaryActivity"
    private lateinit var SHOPPING_LISTS_PATH: String
    private lateinit var CHECKED_SETS_PATH: String
    private lateinit var GRANARY_ITEMS_PATH: String

    private lateinit var boughtProductsMap: Wrapper.HashMap
    private lateinit var productsKeysList: MutableList<String>
    private lateinit var freshnessMap : HashMap<Int, Int>
    private lateinit var elvListAdapter: GranaryElvAdapter
    private lateinit var elvList: ExpandableListView
    private lateinit var fabAdd: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_granary)

        assignPaths()
        fillBoughtProductsList()
        bindButton()
        setUpElvList()
        validateFreshness()
    }

    override fun onStop() {
        super.onStop()
        FileHandler.writeJson(boughtProductsMap, GRANARY_ITEMS_PATH)
    }

    override fun notifyItemDeleted(index: Int) {
        deleteItem(index)
    }

    override fun notifyProductDetailsClicked(name: String) {
        ProductDialog.show(this, ::addItem, name, boughtProductsMap.map[name])
    }

    private fun bindButton() {
        fabAdd = findViewById(R.id.activity_granary_fab_add)
        fabAdd.setOnClickListener {
            ProductDialog.show(this, ::addItem)
        }
    }

    private fun assignPaths() {
        val preferences = getSharedPreferences("FILES", Context.MODE_PRIVATE)
        SHOPPING_LISTS_PATH = preferences.getString("shoppingLists", "") ?: ""
        CHECKED_SETS_PATH = preferences.getString("checkedSets", "") ?: ""
        GRANARY_ITEMS_PATH = preferences.getString("granaryItems", "") ?: ""
    }

    private fun checkFiles() {
        val shoppingListMapData = FileHandler.read(SHOPPING_LISTS_PATH)
        val checkedItemsSetData = FileHandler.read(CHECKED_SETS_PATH)
        val granaryItemsData = FileHandler.read(GRANARY_ITEMS_PATH)

        if (shoppingListMapData.isEmpty()) FileHandler.writeJson(Wrapper.TwoLayerHashMap(), SHOPPING_LISTS_PATH)
        if (checkedItemsSetData.isEmpty()) FileHandler.writeJson(Wrapper.MutableSet(), CHECKED_SETS_PATH)
        if (granaryItemsData.isEmpty()) FileHandler.writeJson(Wrapper.HashMap(), GRANARY_ITEMS_PATH)
    }

    private fun fillBoughtProductsList() {
        checkFiles()

        val shoppingListsMap = FileHandler.readJson<Wrapper.TwoLayerHashMap>(SHOPPING_LISTS_PATH)
        val checkedItemsSet = FileHandler.readJson<Wrapper.MutableSet>(CHECKED_SETS_PATH)
        val granaryItemsMap = FileHandler.readJson<Wrapper.HashMap>(GRANARY_ITEMS_PATH)


        val checkedItemsList = extractPairsFromStringSet(checkedItemsSet.set)

        val onlyBoughtProductsList = getMutableHashMapFromHashMap(shoppingListsMap.map, checkedItemsList)

        boughtProductsMap = granaryItemsMap
        onlyBoughtProductsList.forEach {
            if (!boughtProductsMap.map.containsKey(it.key)) boughtProductsMap.map[it.key] = it.value
        }

    }

    private fun extractPairsFromStringSet(set: MutableSet<String>): MutableList<Pair<Int, String>> {
        val mutableList = mutableListOf<Pair<Int, String>>()

        val indexRegex = """\d$""".toRegex()
        val nameRegex = """^[^|]*""".toRegex()
        set.forEach {
            val index = indexRegex.find(it)?.value?.toInt() ?: -1
            val name = nameRegex.find(it)?.value ?: ""
            mutableList.add(Pair(index, name))
        }

        return mutableList
    }

    private fun getMutableHashMapFromHashMap(map: HashMap<String, HashMap<String, ProductDetails>>,
                                          set: MutableList<Pair<Int, String>>)
    : HashMap<String, ProductDetails> {
        if (map.isEmpty()) return hashMapOf()

        val localMap = hashMapOf<String, ProductDetails>()
        val keys = map.keys.toList()

        set.forEach {
            val name = if (localMap.containsKey(it.second)) "${it.second}-${keys[it.first]}" else it.second
            val product : ProductDetails = map[keys[it.first]]?.get(name) ?: ProductDetails()
            localMap[name] = product
        }

        return localMap
    }

    private fun refreshEvl() {
        elvList.invalidateViews()
        elvListAdapter.notifyDataSetChanged()
    }

    private fun setUpElvList() {
        elvList = findViewById(R.id.activity_granary_elv_list)
        productsKeysList = boughtProductsMap.map.keys.toMutableList()
        freshnessMap = hashMapOf()
        elvListAdapter = GranaryElvAdapter(boughtProductsMap.map, productsKeysList, freshnessMap ,this, this)

        elvList.setAdapter(elvListAdapter)
    }

    private fun addItem(itemTitle : String, details : ProductDetails) {
        if (productsKeysList.contains(itemTitle)) {
            deleteItem(productsKeysList.indexOf(itemTitle))
        }

        boughtProductsMap.map[itemTitle] = details
        productsKeysList.add(itemTitle)
        validateFreshness()
        refreshEvl()
    }

    private fun deleteItem(index: Int) {
        boughtProductsMap.map.remove(productsKeysList[index])
        freshnessMap.remove(productsKeysList[index].hashCode())
        productsKeysList.removeAt(index)
        refreshEvl()
    }

    private fun validateFreshness() {
        freshnessMap.clear()
        boughtProductsMap.map.forEach {
            val difference = ChronoUnit.DAYS.between(LocalDate.now(), it.value.expirationDate)
            if (difference > 2) freshnessMap[it.key.hashCode()] = Color.GREEN
            if (difference <= 2 && difference >= -1) freshnessMap[it.key.hashCode()] = Color.YELLOW
            if (difference < -1) freshnessMap[it.key.hashCode()] = Color.RED

            println()
        }
    }

}