package com.edsonlimadev.chatapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.edsonlimadev.chatapp.fragments.ContactFragment
import com.edsonlimadev.chatapp.fragments.ConversationFragment

class ViewPageMainAdapter(
    private val listTabs: List<String>,
    private val fragment: FragmentManager,
    private val lifecycle: Lifecycle
) : FragmentStateAdapter(fragment, lifecycle) {

    override fun createFragment(position: Int): Fragment {

        when(position){
            1 -> return ContactFragment()
        }

        return ConversationFragment()
    }

    override fun getItemCount(): Int {
       return listTabs.size
    }

}