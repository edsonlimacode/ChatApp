package com.edsonlimadev.chatapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.edsonlimadev.chatapp.adapters.ConversationAdapter
import com.edsonlimadev.chatapp.constants.Constants
import com.edsonlimadev.chatapp.databinding.FragmentConversationBinding
import com.edsonlimadev.chatapp.model.Conversation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration


class ConversationFragment : Fragment() {

    private lateinit var binding: FragmentConversationBinding

    private lateinit var snapshotListener: ListenerRegistration

    private lateinit var conversationAdapter: ConversationAdapter

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val db by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onStart() {
        super.onStart()
        getConversationsList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentConversationBinding.inflate(
            inflater,
            container,
            false
        )

        conversationAdapter = ConversationAdapter()

        binding.rvConversations.adapter = conversationAdapter
        binding.rvConversations.layoutManager = LinearLayoutManager(context)
        binding.rvConversations.addItemDecoration(
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        )

        return binding.root
    }

    private fun getConversationsList() {
        val userId = auth.currentUser?.uid

        if (userId != null) {

            snapshotListener = db.collection(Constants.CONVERSATIONS_COLLECTION)
                .document(userId)
                .collection(Constants.LAST_MESSAGE_COLLECTION)
                .addSnapshotListener { querySnapshot, _ ->

                    val documents = querySnapshot?.documents

                    val conversations = mutableListOf<Conversation>()

                    documents?.forEach { documentSnapshot ->

                        val conversation = documentSnapshot.toObject(Conversation::class.java)

                        if (conversation != null) {
                            conversations.add(conversation)
                        }
                    }

                    if (conversations.isNotEmpty()) {
                        conversationAdapter.setConversations(conversations)
                    }
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        snapshotListener.remove()
    }
}