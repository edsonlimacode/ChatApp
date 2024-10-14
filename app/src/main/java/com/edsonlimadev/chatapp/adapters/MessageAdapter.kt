package com.edsonlimadev.chatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.edsonlimadev.chatapp.constants.Constants
import com.edsonlimadev.chatapp.databinding.ItemReceiverMessagesBinding
import com.edsonlimadev.chatapp.databinding.ItemSenderMessagesBinding
import com.edsonlimadev.chatapp.model.Message
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter : Adapter<ViewHolder>() {

    private var messagesList = emptyList<Message>()

    fun setMessages(list: List<Message>) {
        messagesList = list
        notifyDataSetChanged()
    }

    class SenderMessageViewHolder(
        private val binding: ItemSenderMessagesBinding
    ) : ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.textSenderMessages.text = message.message
        }

        companion object {

            fun layoutInflate(parent: ViewGroup): SenderMessageViewHolder {

                val inflate = LayoutInflater.from(parent.context)

                val itemView = ItemSenderMessagesBinding.inflate(
                    inflate,
                    parent,
                    false
                )

                return SenderMessageViewHolder(itemView)
            }

        }

    }

    class ReceiverMessageViewHolder(
        private val binding: ItemReceiverMessagesBinding
    ) : ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.textReceiverMessages.text = message.message
        }

        companion object {

            fun layoutInflate(parent: ViewGroup): ReceiverMessageViewHolder {

                val inflate = LayoutInflater.from(parent.context)

                val itemView = ItemReceiverMessagesBinding.inflate(
                    inflate,
                    parent,
                    false
                )

                return ReceiverMessageViewHolder(itemView)
            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        val message = messagesList[position]

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        return if (userId == message.userSenderId) {
            Constants.SENDER
        } else {
            Constants.RECEIVER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if (viewType == Constants.SENDER) {
            return SenderMessageViewHolder.layoutInflate(parent)
        }

        return ReceiverMessageViewHolder.layoutInflate(parent)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val message = messagesList[position]

        when(holder){
            is SenderMessageViewHolder -> {
                holder.bind(message)
            }
            is ReceiverMessageViewHolder -> {
                holder.bind(message)
            }
        }
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }
}