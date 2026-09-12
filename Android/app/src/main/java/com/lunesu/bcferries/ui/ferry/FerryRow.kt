package com.lunesu.bcferries.ui.ferry

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lunesu.bcferries.R

class FerryRow(view: View) : RecyclerView.ViewHolder(view) {
    val context: Context get() = itemView.context
    val textViewTime: TextView = itemView.findViewById(R.id.textView_time)
    val textViewDest: TextView = itemView.findViewById(R.id.textView_dest)
    val textViewWarn: TextView = itemView.findViewById(R.id.textView_warn)
    val textViewFare: TextView = itemView.findViewById(R.id.textView_fare)
}
