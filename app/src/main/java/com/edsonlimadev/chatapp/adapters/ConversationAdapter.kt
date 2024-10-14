package com.edsonlimadev.chatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.edsonlimadev.chatapp.databinding.ItemConversationBinding
import com.edsonlimadev.chatapp.model.Conversation
import com.edsonlimadev.chatapp.model.User
import com.squareup.picasso.Picasso

class ConversationAdapter(
    private val onClick: (Conversation) -> Unit
) : Adapter<ConversationAdapter.ConversationViewHolder>() {

    private var conversationList = emptyList<Conversation>()

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

            binding.textNameConversation.text = conversation.receiverName
            binding.textLastMessage.text = conversation.message

            binding.clConversation.setOnClickListener {
                onClick(conversation)
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