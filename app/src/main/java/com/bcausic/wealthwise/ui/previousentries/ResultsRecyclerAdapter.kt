package com.bcausic.wealthwise.ui.previousentries

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bcausic.wealthwise.models.Results
import com.bcausic.wealthwise.databinding.ItemPreviousEntryBinding
import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

class ResultsRecyclerAdapter(private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<ResultsRecyclerAdapter.ResultsViewHolder>() {

    private val items = mutableListOf<Results>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsViewHolder {
        return ResultsViewHolder(
            ItemPreviousEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ResultsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addData(data: List<Results>) {
        if (this.items.isNotEmpty())
            this.items.clear()
        this.items.addAll(data)
        notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getLocalDateFromTimestamp(timestamp: Timestamp): LocalDate {
        val date = timestamp.toDate()
        val instant = date.toInstant()
        val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()

        return localDate
    }

    inner class ResultsViewHolder(private val binding: ItemPreviousEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: Results) {
            with(binding) {
                val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                mtvType.text = data.Type
                mtvAmount.text = data.Amount
                mtvDescription.text = data.Description
                mtvTimestamp.text = getLocalDateFromTimestamp(data.Timestamp).format(dateFormatter)

                root.setOnClickListener {
                    onItemClickListener.onItemClick(data)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(result: Results)
    }
}