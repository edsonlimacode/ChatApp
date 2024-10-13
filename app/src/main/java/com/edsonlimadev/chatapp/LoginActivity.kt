package com.edsonlimadev.chatapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.edsonlimadev.chatapp.databinding.ActivityLoginBinding
import com.edsonlimadev.whatappclone.extensions.message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class LoginActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val auth by lazy {
        FirebaseAuth.getInstance()
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

        binding.btnSingIn.setOnClickListener {

            email = binding.textEditEmailSignIn.text.toString()
            password = binding.textEditPasswordSignIn.text.toString()

            if(validate()) siginIn(email, password)
        }
    }

    private fun siginIn(email: String, password: String) {

        auth.signInWithEmailAndPassword(
            email, password
        ).addOnSuccessListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }.addOnFailureListener { error ->
            try {
                throw error
            }catch (ex: FirebaseAuthInvalidUserException){
                message("Email/ou senha inválido")
            }catch (ex: FirebaseAuthInvalidCredentialsException){
                message("Email/ou senha inválido")
            }
        }
    }

    private fun validate(): Boolean {

        if(email.isEmpty()){
            binding.textInputEmailSignIn.error = "E-mail é obrigatório"
            return false
        }else if(email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.textInputEmailSignIn.error = "E-mail não é válido"
            return false
        }else if (password.isEmpty()){
            binding.textInputEmailSignIn.error = null
            binding.textInputPasswordSignIn.error = "Senha é obrigatória"
            return false
        }else if(password.isNotEmpty() && password.length < 6){
            binding.textInputEmailSignIn.error = null
            binding.textInputPasswordSignIn.error = "Senha deve conter no minimo 6 caracteres"
            return false
        }else {
            binding.textInputPasswordSignIn.error = null
            return true
        }

    }

}