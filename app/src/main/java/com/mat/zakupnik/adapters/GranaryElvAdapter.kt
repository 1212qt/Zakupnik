package com.mat.zakupnik.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.mat.zakupnik.R
import com.mat.zakupnik.interfaces.IInterractionNotifier
import com.mat.zakupnik.wrappers.Wrapper.ProductDetails
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit
import javax.xml.datatype.DatatypeConstants.DAYS
import kotlin.collections.HashMap

class GranaryElvAdapter(private val productsMap: HashMap<String, ProductDetails>,
                        private val keysList: MutableList<String>,
                        private val freshnessList : HashMap<Int, Int>,
                        private val context: Context,
                        private val notifier: IInterractionNotifier)
    : BaseExpandableListAdapter() {

    private fun evaluateFreshnessOfProduct(product : ProductDetails): Int {
        val difference = ChronoUnit.DAYS.between(product.expirationDate, LocalDate.now())
        Log.i("TAG", "difference:$difference")
        return 0
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()
    override fun getChildrenCount(groupPosition: Int): Int = 1
    override fun getChild(groupPosition: Int, childPosition: Int): Any =
        productsMap[keysList[groupPosition]] ?: ProductDetails()
    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val product = getChild(groupPosition, childPosition) as ProductDetails
        val localConvertView = convertView ?: LayoutInflater.from(context).inflate(R.layout.activity_granary_elv_item, null)

        val tvPrice = localConvertView.findViewById<TextView>(R.id.activity_granary_item_tv_price)
        val tvExpiryDate = localConvertView.findViewById<TextView>(R.id.activity_granary_item_tv_expiry_date)
        val tvQuantity = localConvertView.findViewById<TextView>(R.id.activity_granary_item_tv_quantity)
        val tvVolume = localConvertView.findViewById<TextView>(R.id.activity_granary_item_tv_volume)
        evaluateFreshnessOfProduct(product)
        tvPrice.text = product.price.toString()
        tvExpiryDate.text = product.expirationDate.toString()
        tvQuantity.text = product.quantity.toString()
        tvVolume.text = product.volume.toString()

        localConvertView.setOnClickListener {notifier.notifyProductDetailsClicked(keysList[groupPosition])}

        return localConvertView
    }

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()
    override fun getGroupCount(): Int = keysList.size
    override fun getGroup(groupPosition: Int): Any = keysList[groupPosition]
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val name = getGroup(groupPosition) as String
        val localConvertView = convertView ?: LayoutInflater.from(context).inflate(R.layout.activity_granary_elv_group_item, null)
        localConvertView.setBackgroundColor(freshnessList[name.hashCode()] ?: Color.WHITE)

        Log.i("GranaryAdapter", "item:$name  color:${freshnessList[groupPosition]}")

        val tvHeader = localConvertView.findViewById<TextView>(R.id.activity_granary_elv_group_item_tv_title)
        val btnDelete = localConvertView.findViewById<Button>(R.id.activity_granary_elv_group_item_btn_delete)

        tvHeader.text = name
        btnDelete.setOnClickListener {notifier.notifyItemDeleted(groupPosition)}

        return localConvertView
    }

    override fun hasStableIds(): Boolean = false
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
}