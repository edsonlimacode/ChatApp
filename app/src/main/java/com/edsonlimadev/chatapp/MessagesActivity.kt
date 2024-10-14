package com.edsonlimadev.chatapp

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.edsonlimadev.chatapp.adapters.MessageAdapter
import com.edsonlimadev.chatapp.constants.Constants
import com.edsonlimadev.chatapp.databinding.ActivityMessagesBinding
import com.edsonlimadev.chatapp.model.Conversation
import com.edsonlimadev.chatapp.model.Message
import com.edsonlimadev.chatapp.model.User
import com.edsonlimadev.whatappclone.extensions.message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class MessagesActivity : AppCompatActivity() {

    private var userReceiver: User? = null
    private var userSender: User? = null
    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var messageAdapter: MessageAdapter

    private val binding by lazy {
        ActivityMessagesBinding.inflate(layoutInflater)
    }

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val db by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onStart() {
        super.onStart()
        val userSenderId = auth.currentUser?.uid

        if (userSenderId != null) {
            db.collection(Constants.USERS_COLLECTION)
                .document(userSenderId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject(User::class.java)
                    userSender = user
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            window.apply {
                statusBarColor = ContextCompat.getColor(applicationContext, R.color.dark_900)
            }
        }

        getReceiverData()
        initializeToolBar()
        getMessages()
        initializeRecyclerView()

        binding.fabSendMessage.setOnClickListener {

            val textMessage = binding.textEditMessage.text.toString()

            if (userSender?.id != null && userReceiver?.id != null) {

                val message = Message(
                    userSender!!.id,
                    textMessage
                )

                if (textMessage.isNotEmpty()) {
                    saveMessage(userSender!!.id, userReceiver!!.id, message)

                    val senderConversation = Conversation(
                        userSender!!.id,
                        userReceiver!!.id,
                        userReceiver!!.avatar,
                        userReceiver!!.name,
                        textMessage
                    )

                    saveConversation(senderConversation)

                    saveMessage(userReceiver!!.id, userSender!!.id, message)

                    val receiverConversation = Conversation(
                        userReceiver!!.id,
                        userSender!!.id,
                        userSender!!.avatar,
                        userSender!!.name,
                        textMessage
                    )

                    saveConversation(receiverConversation)
                }

                binding.textEditMessage.setText("")
            }

        }

    }

    private fun saveConversation(conversation: Conversation) {

        db.collection(Constants.CONVERSATIONS_COLLECTION)
            .document(conversation.userSenderId)
            .collection(Constants.LAST_MESSAGE_COLLECTION)
            .document(conversation.userReceiverId)
            .set(conversation)
            .addOnFailureListener {
                message("Erro ao tentar salvar a conversa")
            }
    }

    private fun initializeRecyclerView() {

        messageAdapter = MessageAdapter()

        binding.rvMessages.adapter = messageAdapter
        binding.rvMessages.layoutManager = LinearLayoutManager(this)

    }

    private fun getMessages() {

        val userSenderId = auth.currentUser?.uid
        val userReceiverId = userReceiver?.id

        if (userSenderId != null && userReceiverId != null) {

            listenerRegistration = db.collection(Constants.MESSAGES_COLLECTION)
                .document(userSenderId)
                .collection(userReceiverId)
                .orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot, error ->

                    if (error != null) {
                        message("Erro ao tentar ler as mensagens")
                    }

                    val messagesList = mutableListOf<Message>()

                    val documents = querySnapshot?.documents
                    documents?.forEach { documentSnapshot ->

                        val message = documentSnapshot.toObject(Message::class.java)
                        if (message != null) {
                            messagesList.add(message)
                        }
                    }

                    if (messagesList.isNotEmpty()) {
                        messageAdapter.setMessages(messagesList)
                    }
                }
        }

    }

    private fun saveMessage(userSenderId: String, userReceiverId: String, message: Message) {
        db.collection(Constants.MESSAGES_COLLECTION)
            .document(userSenderId)
            .collection(userReceiverId)
            .add(message)
            .addOnFailureListener {
                message("Erro ao tentar enviar a mensagem")
            }
    }

    private fun getReceiverData() {

        val extras = intent.extras

        if (extras != null) {
            userReceiver = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras.getParcelable("userReceiver", User::class.java)
            } else {
                extras.getParcelable("userReceiver")
            }
        }
    }

    private fun initializeToolBar() {
        val toolbar = binding.tbMessages
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        binding.textNameMessage.text = userReceiver?.name
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }
}