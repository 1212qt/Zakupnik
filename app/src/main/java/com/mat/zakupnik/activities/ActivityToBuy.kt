package com.mat.zakupnik.activities

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mat.zakupnik.R
import com.mat.zakupnik.adapters.ToBuyAdapter
import com.mat.zakupnik.interfaces.IDeletionNotifier
import com.mat.zakupnik.handlers.FileHandler

class ActivityToBuy : AppCompatActivity(), IDeletionNotifier {
    private val TAG = "ActivityToBuy"
    private lateinit var rvToBuy : RecyclerView
    private lateinit var btnAdd : FloatingActionButton
    private lateinit var tvHeader : TextView
    private lateinit var rvAdapter: ToBuyAdapter
    private lateinit var adapterArray : MutableList<String>

    override fun notifyItemDeleted(index: Int) {
        Log.i(TAG, "Item ${adapterArray[index]} deleted.")
        adapterArray.removeAt(index)
        refreshRecycleView()
    }

    private fun addItemToRecycleView(name : String) {
        Log.i(TAG, "Item $name added.")
        adapterArray.add(name)
        refreshRecycleView()
    }

    private fun refreshRecycleView() {
        rvToBuy.recycledViewPool.clear()
        rvAdapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_to_buy)

        loadAdapterListFromFile()
        findViews()
        manageRecyclerView()
    }

    private fun loadAdapterListFromFile() {
        val path = "${applicationInfo.dataDir}/productsToBuy.JSON"
        val arrayData = FileHandler.read(path)

        if (arrayData.isEmpty()) FileHandler.writeJson<MutableList<String>>(mutableListOf(), path)

        adapterArray = FileHandler.readJson("${applicationInfo.dataDir}/productsToBuy.JSON")
    }

    private fun findViews() {
        rvToBuy = findViewById(R.id.activity_to_buy_rv_list)
        btnAdd = findViewById(R.id.activity_to_buy_fab_add)
        tvHeader = findViewById(R.id.activity_to_buy_tv_header)
    }

    private fun manageRecyclerView() {
        rvAdapter = ToBuyAdapter(adapterArray, this)
        rvToBuy.adapter = rvAdapter
        rvToBuy.layoutManager = LinearLayoutManager(this)
        btnAdd.setOnClickListener {onAddButtonClicked()}
    }

    override fun onStop() {
        super.onStop()
        exportArrayToFile()
    }

    private fun onAddButtonClicked() {
        val editText = EditText(this)
        editText.inputType = InputType.TYPE_CLASS_TEXT
        editText.hint = getString(R.string.activity_to_buy_product_name_dialog_text)

        AlertDialog.Builder(this)
            .setView(editText)
            .setPositiveButton("OK") { _, _ -> addItemToRecycleView(editText.text.toString()) }
            .setNegativeButton(R.string.activity_to_buy_negative_response_dialog_text) { dialog, _ -> dialog.cancel()}
            .show()
    }

    private fun exportArrayToFile() {
        Log.i(TAG, "Writing data to file.")
        val DIR = applicationInfo.dataDir + "/productsToBuy.JSON"
        Log.i(TAG, FileHandler.read(DIR))
        FileHandler.writeJson(adapterArray, DIR)
        Log.i(TAG, FileHandler.read(DIR))
    }


}