package com.kwmkade.kotlinsamplewordbook

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kwmkade.kotlinsamplewordbook.databinding.WordCardBinding

class WordCardRecyclerViewAdapter :
    RecyclerView.Adapter<WordCardRecyclerViewAdapter.WordCardViewHolder>() {
    private val mCardList = mutableListOf<Card>()

    inner class WordCardViewHolder(binding: WordCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val contentView: TextView = binding.cardContent
    }

    fun setCard(card: Card) {
        mCardList.add(card)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordCardViewHolder {
        return WordCardViewHolder(
            WordCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WordCardViewHolder, position: Int) {
        val card = mCardList[position]
        holder.contentView.text = card.word
    }

    override fun getItemCount(): Int = mCardList.size
}