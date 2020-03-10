package com.mat.zakupnik.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mat.zakupnik.R
import com.mat.zakupnik.handlers.FileHandler
import com.mat.zakupnik.wrappers.Wrapper

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        bindButtons()
        addPathsToSharedPreferences()
    }

    override fun onResume() {
        super.onResume()
        setText()
    }

    private fun startOtherActivity(pActivityClass : Class<out AppCompatActivity>) {
        startActivity(Intent(this, pActivityClass))
    }

    private fun setText() {
        val path = "${applicationInfo.dataDir}/granaryItems.JSON"
        val granaryItemsData = FileHandler.read(path)

        if (granaryItemsData.isNotEmpty()) {
            val granaryItems = FileHandler.readJson<Wrapper.HashMap>(path)
            findViewById<TextView>(R.id.activity_start_tv_textView).text =
                "Przedmiotów w spichlerzu:${granaryItems.map.size}"
        }
    }

    private fun bindButtons() {
        findViewById<Button>(R.id.activity_start_btn_toBuy)
            ?.setOnClickListener{startOtherActivity(ActivityToBuy::class.java)}
        findViewById<Button>(R.id.activity_start_btn_shoppingLists)
            ?.setOnClickListener {startOtherActivity(ShoppingListsActivity::class.java)}
        findViewById<Button>(R.id.activity_start_btn_granary)
            ?.setOnClickListener {startOtherActivity(GranaryActivity::class.java)}
    }

    private fun addPathsToSharedPreferences() {
        val appDirPath = applicationInfo.dataDir
        val preferences = getSharedPreferences("FILES", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("shoppingLists", "$appDirPath/shoppingLists.JSON")
        editor.putString("checkedSets", "$appDirPath/checkedSets.JSON")
        editor.putString("granaryItems", "$appDirPath/granaryItems.JSON")
        editor.apply()
    }
}