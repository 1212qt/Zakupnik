package com.mat.zakupnik.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import com.mat.zakupnik.R
import com.mat.zakupnik.activities.ShoppingListsActivity
import com.mat.zakupnik.interfaces.IShoppingListNotifier
import com.mat.zakupnik.wrappers.Wrapper.ProductDetails
import java.util.*
import kotlin.collections.HashMap


class ShoppingListGroupAdapter(
    private val context : Context,
    private val expandableListTitle : MutableList<String>,
    private val expandableMapDetail : HashMap<String, ProductDetails>,
    private val checkedSet : MutableSet<String>,
    private val expansionNotifier : IShoppingListNotifier,
    private val parentPosition : Int)
    : BaseExpandableListAdapter() {

    private val TAG = "ShoppingListGroupAdapter"

    override fun getChild(groupPosition: Int, childPosition: Int): Any =
        expandableMapDetail[expandableListTitle[groupPosition]]
            ?: ProductDetails()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()
    override fun getChildrenCount(groupPosition: Int): Int = 1
    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean,
                              convertView: View?, parent: ViewGroup?) : View {
        val childData = getChild(groupPosition, childPosition) as ProductDetails
        val localConvertView = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_shopping_lists, null)

        localConvertView.setOnClickListener {expansionNotifier.notifyProductDetailsClicked(expandableListTitle[groupPosition])}

        val tvPrice = localConvertView.findViewById<TextView>(R.id.activity_shopping_lists_item_tv_price)
        val tvExpiryDate = localConvertView.findViewById<TextView>(R.id.activity_shopping_lists_item_tv_expiry_date)
        val tvQuantity = localConvertView.findViewById<TextView>(R.id.activity_shopping_lists_item_tv_quantity)
        val tvVolume = localConvertView.findViewById<TextView>(R.id.activity_shopping_lists_item_tv_volume)

        tvPrice.text = childData.price.toString()
        tvExpiryDate.text = childData.expirationDate.toString()
        tvQuantity.text = childData.quantity.toString()
        tvVolume.text = childData.volume.toString()

        return localConvertView
    }

    override fun onGroupExpanded(groupPosition: Int) {
        super.onGroupExpanded(groupPosition)
        expansionNotifier.notifyChildCollapsed(expandableListTitle[groupPosition])
    }

    override fun onGroupCollapsed(groupPosition: Int) {
        super.onGroupCollapsed(groupPosition)
        expansionNotifier.notifyChildCollapsed("")
    }

    override fun getGroup(groupPosition: Int): Any = expandableListTitle[groupPosition]
    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()
    override fun getGroupCount(): Int = expandableListTitle.size
    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val name = getGroup(groupPosition) as String

        val localConvertView = convertView ?: LayoutInflater.from(context).inflate(R.layout.child_group_item_shopping_lists, null)
        val tvHeader = localConvertView.findViewById<TextView>(R.id.activity_shopping_lists_child_group_item_tv_group_header)
        tvHeader.text = name
        tvHeader.setPadding(200, 0,0,0)

        val btnDelete = localConvertView.findViewById<Button>(R.id.activity_shopping_lists_child_group_item_btn_delete)
        btnDelete.setOnClickListener{
            Log.i(TAG, "Child item:${name} deleted.")
            expandableListTitle.remove(name)
            expansionNotifier.notifyChildDeleted(name)
        }

        val cbBought = localConvertView.findViewById<CheckBox>(R.id.activity_shopping_lists_child_group_item_cb_bought)
        val boughtTag = "$name|$parentPosition".replace(' ', '_')
        cbBought.tag = boughtTag
        cbBought.isChecked = checkedSet.contains(boughtTag)
        cbBought.setOnCheckedChangeListener {view, isChecked ->
            val cb = view as CheckBox
            val tag = cb.tag as String
            if (isChecked) {
                if (!checkedSet.contains(tag)) checkedSet.add(tag)
            } else {
                if (checkedSet.contains(tag)) checkedSet.remove(tag)
            }
        }

        return localConvertView
    }

    override fun hasStableIds(): Boolean = false
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
}