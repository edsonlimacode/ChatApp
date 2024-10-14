package com.edsonlimadev.chatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.edsonlimadev.chatapp.databinding.ItemContactsBinding
import com.edsonlimadev.chatapp.model.User
import com.squareup.picasso.Picasso

class ContactAdapter(
    private val onClick: (User) -> Unit
) : Adapter<ContactAdapter.ContactViewHolder>() {

    private var contactsList = emptyList<User>()

    fun setContacts(list: List<User>) {
        contactsList = list
        notifyDataSetChanged()
    }

    inner class ContactViewHolder(
        private val binding: ItemContactsBinding
    ) : ViewHolder(binding.root) {

        fun bind(user: User) {

            if (user.avatar.isNotEmpty()) {
                Picasso.get().load(user.avatar).into(binding.imgContact)
            }

            binding.textContact.text = user.name


            binding.clItemContact.setOnClickListener {
                onClick(user)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {

        val inflate = LayoutInflater.from(parent.context)

        val itemView = ItemContactsBinding.inflate(inflate, parent, false)

        return ContactViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactsList[position]
        holder.bind(contact)
    }


    override fun getItemCount(): Int {
        return contactsList.size
    }

}