package com.mat.zakupnik.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.mat.zakupnik.R
import com.mat.zakupnik.adapters.ShoppingListAdapter
import com.mat.zakupnik.dialogs.ProductDialog
import com.mat.zakupnik.handlers.FileHandler
import com.mat.zakupnik.interfaces.IShoppingListNotifier
import com.mat.zakupnik.wrappers.Wrapper
import com.mat.zakupnik.wrappers.Wrapper.ProductDetails
import java.time.LocalTime
import java.util.*

import kotlin.collections.HashMap

class ShoppingListsActivity : AppCompatActivity(), IShoppingListNotifier {

    private val TAG = "ShoppingListActivity"
    private val expandedParents = mutableListOf<String>()
    private lateinit var DIR : String
    private lateinit var HashMapPath : String
    private lateinit var SetPath : String
    private lateinit var shoppingListMap : Wrapper.TwoLayerHashMap
    private lateinit var checkedMutableSet : Wrapper.MutableSet
    private lateinit var shoppingListKeysList : MutableList<String>
    private lateinit var btnAdd : FloatingActionButton
    private lateinit var elvList : ExpandableListView
    private lateinit var elvAdapter : ShoppingListAdapter

    override fun notifyProductDetailsClicked(name : String) {
        triggerShoppingItemModificationDialog(name, shoppingListMap.map[expandedParents[0]]?.get(name))
    }

    override fun notifyChildDeleted(name: String) {
        removeChildItem(name)
        refreshElv()
    }

    override fun notifyParentDeleted(name : String) {
        shoppingListMap.map.remove(name)
        shoppingListKeysList.remove(name)
        refreshElv()
    }

    override fun notifyParentCollapsed(name: String) {
        expandedParents.remove(name)
        elvAdapter.setCurrentExpandedParent(if (expandedParents.isEmpty()) "" else expandedParents[0])
    }

    override fun notifyParentExpanded(name: String) {
        expandedParents.add(0, name)
        elvAdapter.setCurrentExpandedParent(if (expandedParents.isEmpty()) "" else expandedParents[0])
    }

    override fun notifyChildCollapsed(name: String) {

    }

    private fun removeChildItem(name : String) {
        shoppingListMap.map[expandedParents[0]]?.remove(name)
    }

    private fun refreshElv() {
        elvList.invalidateViews()
        elvAdapter.notifyDataSetChanged()
    }

    private fun checkFiles() {
        val hashMapFileData = FileHandler.read(HashMapPath)
        val setFileData = FileHandler.read(SetPath)

        if (hashMapFileData.isEmpty()) FileHandler.writeJson(Wrapper.TwoLayerHashMap(), HashMapPath)
        if (setFileData.isEmpty()) FileHandler.writeJson(Wrapper.MutableSet(), SetPath)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_shopping_lists)

        DIR = applicationInfo.dataDir
        HashMapPath = "$DIR/shoppingLists.JSON"
        SetPath = "$DIR/checkedSets.JSON"
        checkFiles()

        shoppingListMap = FileHandler.readJson(HashMapPath)
        checkedMutableSet = FileHandler.readJson(SetPath)
        shoppingListKeysList = shoppingListMap.map.keys.toMutableList()
        btnAdd = findViewById(R.id.activity_shopping_lists_fab_add)
        btnAdd.setOnClickListener{onAddButtonClicked()}
        elvAdapter = ShoppingListAdapter(this, shoppingListKeysList, shoppingListMap.map, checkedMutableSet.set, this)
        elvList = findViewById(R.id.activity_shopping_lists_elv_list)
        elvList.setAdapter(elvAdapter)
    }

    private fun addShoppingList(title : String, empty : Boolean) {
        shoppingListMap.map[title] = hashMapOf()
        shoppingListKeysList.add(title)
        if (empty) {
        } else {
            val namesArray = FileHandler.readJson<MutableList<String>>("$DIR/productsToBuy.JSON")
            namesArray.forEach {
                shoppingListMap.map[title]?.put(it, Wrapper.ProductDetails())
            }
        }
        refreshElv()
    }

    private fun addItemToShoppingList(itemTitle : String, item : ProductDetails) {
        removeChildItem(itemTitle)
        shoppingListMap.map[expandedParents[0]]?.put(itemTitle, item)
        refreshElv()
    }

    override fun onStop() {
        super.onStop()
        exportDataToFile()
    }

    private fun onAddButtonClicked() {
        if (expandedParents.isEmpty()) {
            triggerShoppingListAdditionDialog()
        } else {
            triggerShoppingItemAdditionDialog()
        }

    }

    private fun triggerShoppingListAdditionDialog() {
        val dialogLayout = LayoutInflater.from(this).inflate(R.layout.activity_shopping_new_shopping_list_dialog, null)
        val etShoppingList = dialogLayout.findViewById<EditText>(R.id.activity_shopping_lists_dialog_tv_name)
        val cbLoadFromToBuy = dialogLayout.findViewById<CheckBox>(R.id.activity_shopping_lists_dialog_cb_load_from_to_buy)

        val tvText = etShoppingList.text

        AlertDialog.Builder(this)
            .setView(dialogLayout)
            .setPositiveButton("OK") { dialog, _ ->
                if (shoppingListKeysList.contains(tvText.toString())) {
                    Toast.makeText(this, "Shopping list $tvText already exists", Toast.LENGTH_SHORT).show()
                    dialog.cancel()
                } else {
                    addShoppingList(tvText.toString(), !cbLoadFromToBuy.isChecked) }
                }
            .setNegativeButton(R.string.activity_to_buy_negative_response_dialog_text) { dialog, _ -> dialog.cancel()}
            .show()
    }

    private fun triggerShoppingItemAdditionDialog() {
        ProductDialog.show(this, ::addItemToShoppingList)
    }

    private fun triggerShoppingItemModificationDialog(name : String, details : ProductDetails?) {
        ProductDialog.show(this, ::addItemToShoppingList, name, details)
    }

    private fun exportDataToFile() {
        FileHandler.writeJson(shoppingListMap, HashMapPath)
        FileHandler.writeJson(checkedMutableSet, SetPath)
    }
}