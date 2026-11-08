package com.comp3074.foodquest.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.comp3074.foodquest.R
import com.comp3074.foodquest.domain.QuestRules

class QuestListFragment : ScreenFragment(R.layout.fragment_quest_list) {
    private var query = ""
    private var selectedTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        query = savedInstanceState?.getString("query").orEmpty()
        selectedTag = savedInstanceState?.getString("tag")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.add_quest).setOnClickListener { host.open(QuestFormFragment()) }
        val search = view.findViewById<EditText>(R.id.search_query)
        val filter = view.findViewById<Spinner>(R.id.tag_filter)
        val count = view.findViewById<TextView>(R.id.result_count)
        val empty = view.findViewById<TextView>(R.id.empty_quests)
        val adapter = QuestAdapter { id -> host.open(QuestDetailFragment.newInstance(id)) }
        view.findViewById<RecyclerView>(R.id.quest_list).apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
        search.setText(query)

        fun showResults() {
            val all = model.quests.value.orEmpty()
            val matches = QuestRules.filter(all, query, selectedTag)
            adapter.submitList(matches)
            count.text = getString(R.string.quest_count, matches.size)
            empty.visibility = if (matches.isEmpty()) View.VISIBLE else View.GONE
            empty.setText(if (all.isEmpty()) R.string.no_quests else R.string.no_matches)
        }

        search.doAfterTextChanged {
            query = it.toString()
            showResults()
        }
        model.quests.observe(viewLifecycleOwner) { quests ->
            val tags = quests.flatMap { it.tags }.distinct().sorted()
            if (selectedTag !in tags) selectedTag = null
            filter.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                listOf(getString(R.string.all_tags)) + tags)
            filter.setSelection(if (selectedTag == null) 0 else tags.indexOf(selectedTag) + 1)
            showResults()
        }
        filter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, selectedView: View?, position: Int, id: Long) {
                selectedTag = if (position == 0) null else parent?.getItemAtPosition(position).toString()
                showResults()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("query", query)
        outState.putString("tag", selectedTag)
        super.onSaveInstanceState(outState)
    }
}
