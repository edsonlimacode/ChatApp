package com.edsonlimadev.chatapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.edsonlimadev.chatapp.MessagesActivity
import com.edsonlimadev.chatapp.R
import com.edsonlimadev.chatapp.adapters.ContactAdapter
import com.edsonlimadev.chatapp.constants.Constants
import com.edsonlimadev.chatapp.databinding.FragmentContactBinding
import com.edsonlimadev.chatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ContactFragment : Fragment() {

    private lateinit var binding: FragmentContactBinding

    private lateinit var contactAdapter: ContactAdapter

    private lateinit var listenerRegistration: ListenerRegistration

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val db by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onStart() {
        super.onStart()
        getContactList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentContactBinding.inflate(
            inflater,
            container,
            false
        )

        contactAdapter = ContactAdapter { user ->

            val intent = Intent(context, MessagesActivity::class.java)

            intent.putExtra("userReceiver", user)
            startActivity(intent)
        }

        binding.rvContacts.adapter = contactAdapter
        binding.rvContacts.layoutManager = LinearLayoutManager(context)

        val dividerDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.custom_divider)

        val itemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)

        dividerDrawable?.let {
            itemDecoration.setDrawable(it)
        }

        binding.rvContacts.addItemDecoration(itemDecoration)

        return binding.root

    }

    private fun getContactList() {

        val userLogged = auth.currentUser?.uid

        if (userLogged != null) {

            listenerRegistration = db.collection(Constants.USERS_COLLECTION)
                .addSnapshotListener { querySnapshot, _ ->

                    val documents = querySnapshot?.documents

                    val users = mutableListOf<User>()

                    documents?.forEach { documentSnapshot ->

                        val user = documentSnapshot.toObject(User::class.java)

                        if (user != null && user.id != userLogged) {
                            users.add(user)
                        }
                    }

                    if (users.isNotEmpty()) {
                        contactAdapter.setContacts(users)
                    }
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }

}