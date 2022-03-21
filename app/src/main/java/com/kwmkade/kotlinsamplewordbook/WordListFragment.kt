package com.kwmkade.kotlinsamplewordbook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WordListFragment : Fragment() {

    private val mViewModel: CardViewModel by activityViewModels()
    private lateinit var mAdapter: WordCardRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_word_list, container, false)

        mAdapter = WordCardRecyclerViewAdapter()

        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = mAdapter
                setHasFixedSize(true)
            }
        }

        mAdapter.setCard(Card("A"))
        mAdapter.setCard(Card("B"))
        mAdapter.setCard(Card("C"))

        mViewModel.CARDS.observe(viewLifecycleOwner) {
            mAdapter.setCard(it)
        }

        return view
    }
}