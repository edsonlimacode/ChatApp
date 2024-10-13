package com.edsonlimadev.chatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.edsonlimadev.chatapp.R
import com.edsonlimadev.chatapp.databinding.FragmentConversationBinding


class ConversationFragment : Fragment() {

    private lateinit var binding: FragmentConversationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentConversationBinding.inflate(
            inflater,
            container,
            false
        )


        return binding.root
    }
}