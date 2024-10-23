package com.edsonlimadev.chatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.edsonlimadev.chatapp.constants.Constants
import com.edsonlimadev.chatapp.databinding.ItemConversationBinding
import com.edsonlimadev.chatapp.model.Conversation
import com.edsonlimadev.chatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ConversationAdapter(
    private val onClick: (Conversation, String?) -> Unit
) : Adapter<ConversationAdapter.ConversationViewHolder>() {

    private val db by lazy {
        FirebaseFirestore.getInstance()
    }

    private var conversationList = emptyList<Conversation>()

    private var avatar: String = ""

    fun setConversations(list: List<Conversation>) {
        conversationList = list
        notifyDataSetChanged()
    }

    inner class ConversationViewHolder(
        private val binding: ItemConversationBinding
    ) : ViewHolder(
        binding.root
    ) {

        fun bind(conversation: Conversation) {

            if (conversation.avatarReceiver.isNotEmpty()) {
                Picasso.get().load(conversation.avatarReceiver).into(binding.imgConversation)
            }

            db.collection(Constants.USERS_COLLECTION)
                .document(conversation.userReceiverId)
                .addSnapshotListener { documentSnapshot, _ ->
                    val user = documentSnapshot?.toObject(User::class.java)
                    if (user != null && user.avatar.isNotEmpty()) {
                        avatar = user.avatar
                        Picasso.get().load(user.avatar).into(binding.imgConversation)
                    }
                }

            binding.textNameConversation.text = conversation.receiverName
            binding.textLastMessage.text = conversation.message

            binding.clConversation.setOnClickListener {
                onClick(conversation, avatar)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val inflate = LayoutInflater.from(parent.context)

        val itemView = ItemConversationBinding.inflate(inflate, parent, false)

        return ConversationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = conversationList[position]
        holder.bind(conversation)
    }


    override fun getItemCount(): Int {
        return conversationList.size
    }

}