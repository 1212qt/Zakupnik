package com.mat.zakupnik.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.TextView
import com.mat.zakupnik.R
import com.mat.zakupnik.activities.ShoppingListsActivity
import com.mat.zakupnik.interfaces.IDeletionNotifier
import com.mat.zakupnik.interfaces.IShoppingListNotifier
import com.mat.zakupnik.wrappers.Wrapper.ProductDetails

class ShoppingListAdapter(private val context : Context,
                          private val expandableListTitle : MutableList<String>,
                          private val expandableMapDetail : HashMap<String, HashMap<String, ProductDetails>>,
                          private val checkedSet : MutableSet<String>,
                          private val expansionNotifier : IShoppingListNotifier)
    : BaseExpandableListAdapter() {

    private val TAG = "ShoppingListAdapter"

    private class SecondLevelExpandableList(private val classContext : Context) : ExpandableListView(classContext) {
        override fun onMeasure(widthMeasureSpec : Int, heightMeasureSpec : Int) {
            val localHeightMeasureSpec = MeasureSpec.makeMeasureSpec(999999, MeasureSpec.AT_MOST)
            super.onMeasure(widthMeasureSpec, localHeightMeasureSpec)
        }

    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any =
        expandableMapDetail[expandableListTitle[groupPosition]]
            ?: hashMapOf<String, ProductDetails>()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()
    override fun getChildrenCount(groupPosition: Int): Int = 1
    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean,
                              convertView: View?, parent: ViewGroup?): View {
        val childData = getChild(groupPosition, childPosition) as HashMap<String, ProductDetails>
        val titles = childData.keys.toMutableList()
        val secondLevelELV = SecondLevelExpandableList(context)
        secondLevelELV.setAdapter(ShoppingListGroupAdapter(context, titles, childData, checkedSet, expansionNotifier, groupPosition))
        secondLevelELV.setIndicatorBoundsRelative(120, 210)
        return secondLevelELV
    }

    override fun onGroupCollapsed(groupPosition: Int) {
        super.onGroupCollapsed(groupPosition)
        expansionNotifier.notifyParentCollapsed("")
    }

    override fun onGroupExpanded(groupPosition: Int) {
        super.onGroupExpanded(groupPosition)
        expansionNotifier.notifyParentCollapsed(expandableListTitle[groupPosition])

    }

    override fun getGroup(groupPosition: Int): Any = expandableListTitle[groupPosition]
    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()
    override fun getGroupCount(): Int = expandableListTitle.size
    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {

        val name = getGroup(groupPosition) as String
        val localConvertView = convertView ?: LayoutInflater.from(context).inflate(R.layout.group_item_shopping_lists, null)

        val tvHeader = localConvertView.findViewById<TextView>(R.id.activity_shopping_lists_item_tv_group_header)
        tvHeader.text = name

        val btnDelete = localConvertView.findViewById<Button>(R.id.activity_shopping_lists_item_btn_delete)
        btnDelete.setOnClickListener{
            Log.i(TAG, "Item $name deleted.")
            expansionNotifier.notifyParentDeleted(name)
        }

        return localConvertView
    }

    override fun hasStableIds(): Boolean = false
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
}