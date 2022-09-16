package com.example.homeassignment.domain.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.homeassignment.R
import com.example.homeassignment.domain.model.BarCode

class BarcodeAdapter(private val context: Context): RecyclerView.Adapter<BarcodeAdapter.BarCodeViewHolder>() {

    private var list: List<BarCode> = emptyList()

    class BarCodeViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var barCodeValue: TextView

        init {
            barCodeValue = view.findViewById(R.id.barcode_value)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarCodeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false)
        return BarCodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BarCodeViewHolder, position: Int) {
        holder.barCodeValue.text = list[position].barCodeValue

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setUpList(list: List<BarCode>) {
        this.list = list
    }

}