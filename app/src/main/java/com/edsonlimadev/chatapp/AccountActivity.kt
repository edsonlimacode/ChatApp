package com.edsonlimadev.chatapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.edsonlimadev.chatapp.constants.Constants
import com.edsonlimadev.chatapp.databinding.ActivityAccountBinding
import com.edsonlimadev.chatapp.model.User
import com.edsonlimadev.whatappclone.extensions.message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class AccountActivity : AppCompatActivity() {

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String

    private val binding by lazy {
        ActivityAccountBinding.inflate(layoutInflater)
    }

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val db by lazy {
        FirebaseFirestore.getInstance()
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

        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_900)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.dark_900)

        initializeToolBar()

        binding.btnSignUp.setOnClickListener {

            name = binding.textEditNameSignUp.text.toString()
            email = binding.textEditEmailSignUp.text.toString()
            password = binding.textEditPasswordSignUp.text.toString()

            if (validate()) createNewAccount(name, email, password)

        }

    }

    private fun initializeToolBar() {
        val toolbar = binding.includeSignUpTb.tbMain
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun validate(): Boolean {

        if (name.isEmpty()) {
            binding.textEditNameSignUp.error = "Nome é obrigatório"
            return false
        } else if (email.isEmpty()) {
            binding.textEditNameSignUp.error = null
            binding.textEditEmailSignUp.error = "E-mail é obrigatório"
            return false
        } else if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textEditNameSignUp.error = null
            binding.textEditEmailSignUp.error = "E-mail não é válido"
            return false
        } else if (password.isEmpty()) {
            binding.textEditEmailSignUp.error = null
            binding.textEditPasswordSignUp.error = "Senha é obrigatória"
            return false
        } else if (password.isNotEmpty() && password.length < 6) {
            binding.textEditEmailSignUp.error = null
            binding.textEditPasswordSignUp.error = "Senha deve conter no minino 6 caracteres"
            return false
        } else {
            binding.textEditPasswordSignUp.error = null
            return true
        }

    }

    private fun createNewAccount(name: String, email: String, password: String) {

        auth.createUserWithEmailAndPassword(
            email, password
        ).addOnCompleteListener { authResult ->

            val user = authResult.result.user

            if (user != null) {
                saveUserToDatabase(User(user.uid, name, email))
            }

        }.addOnFailureListener { error ->
            try {
                throw error
            } catch (ex: FirebaseAuthUserCollisionException) {
                message("Usuário já existe")
            } catch (ex: FirebaseAuthWeakPasswordException) {
                message("Senha muito fraca, forneça uma senha mais forte")
            }
        }

    }

    private fun saveUserToDatabase(user: User) {
        db.collection(Constants.USERS_COLLECTION)
            .document(user.id)
            .set(user)
            .addOnCompleteListener {
                message("Usuário cadastrado com sucesso")
                startActivity(Intent(this, HomeActivity::class.java))
            }
    }
}