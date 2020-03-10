package com.mat.zakupnik.dialogs

import android.app.AlertDialog
import android.app.AlertDialog.Builder
import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.mat.zakupnik.R
import com.mat.zakupnik.wrappers.Wrapper
import java.time.LocalDate
import java.util.*

class ProductDialog {
    companion object {
        fun show(context: Context, function : (String, Wrapper.ProductDetails) -> Unit, name : String? = null,
                 productToView : Wrapper.ProductDetails? = null) {
            val builder = Builder(context)
            val dialogLayout = LayoutInflater.from(context).inflate(R.layout.activity_shopping_lists_dialog_product_details, null)
            val etName = dialogLayout.findViewById<EditText>(R.id.activity_shopping_lists_item_dialog_et_name)
            val etPrice = dialogLayout.findViewById<EditText>(R.id.activity_shopping_lists_item_dialog_et_price)
            val etExpDate = dialogLayout.findViewById<TextView>(R.id.activity_shopping_lists_item_dialog_et_expiry_date)
            val etQuantity = dialogLayout.findViewById<EditText>(R.id.activity_shopping_lists_item_dialog_et_quantity)
            val etVolume = dialogLayout.findViewById<EditText>(R.id.activity_shopping_lists_item_dialog_et_volume)


            if (productToView != null && name != null) {
                etName.setText(name)
                etExpDate.text = productToView.expirationDate.toString()
                etPrice.setText(productToView.price.toString())
                etQuantity.setText(productToView.quantity.toString())
                etVolume.setText(productToView.volume.toString())
            }

            val calendar = Calendar.getInstance()

            val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                etExpDate.text =
                    "${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH)+1}-${calendar.get(
                        Calendar.YEAR)}"
            }

            etExpDate.setOnClickListener {
                DatePickerDialog(context, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show()
                    etQuantity.requestFocus()
                    println()
            }

            builder
                .setView(dialogLayout)
                .setPositiveButton("OK") { _, _ -> function(
                    etName.text.toString(),
                    Wrapper.ProductDetails(
                        if (etPrice.text.toString().isEmpty()) 0f else etPrice.text.toString().toFloat(),
                        LocalDate.of(calendar.get(
                            Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH)),
                        if (etQuantity.text.toString().isEmpty()) 0 else etQuantity.text.toString().toInt(),
                        if (etVolume.text.toString().isEmpty()) 0 else etVolume.text.toString().toInt()
                    )
                ) }
                .setNegativeButton(R.string.activity_to_buy_negative_response_dialog_text) { dialog, _ -> dialog.cancel()}
                .show()
        }

    }


}