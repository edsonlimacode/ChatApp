package com.edsonlimadev.chatapp

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.edsonlimadev.chatapp.constants.Constants
import com.edsonlimadev.chatapp.databinding.ActivityProfileBinding
import com.edsonlimadev.chatapp.model.User
import com.edsonlimadev.whatappclone.extensions.message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val db by lazy {
        FirebaseFirestore.getInstance()
    }

    private val storage by lazy {
        FirebaseStorage.getInstance()
    }

    private var accessGallery = false

    private lateinit var openGallery: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initToolBar()
        setValues()
        initListeners()
        requestPermissions()
        gallery()
    }

    private fun requestPermissions() {

        accessGallery = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        val permissionsDenied = mutableListOf<String>()

        if (!accessGallery) permissionsDenied.add(Manifest.permission.READ_MEDIA_IMAGES)

        if (permissionsDenied.isNotEmpty()) {
            val managerPermissions = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                accessGallery = permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: accessGallery
            }

            managerPermissions.launch(permissionsDenied.toTypedArray())
        }
    }

    private fun initListeners() {
        binding.btnUpdateProfile.setOnClickListener {

            val name = binding.textInputNameProfile.text.toString().trim()

            setProfileName(name)
        }

        binding.btnFabProfile.setOnClickListener {
            if (accessGallery) {
                openGallery.launch("image/*")
            } else {
                Toast.makeText(this, "Accesso a galeria não permitido", Toast.LENGTH_SHORT).show()
                requestPermissions()
            }

        }
    }

    private fun setValues() {

        db.collection(Constants.USERS_COLLECTION)
            .document(auth.currentUser!!.uid)
            .addSnapshotListener { documentSnapshot, _ ->

                val user = documentSnapshot?.toObject(User::class.java)

                user?.let { user ->

                    if (user.avatar.isNotEmpty()) {
                        Picasso.get().load(user.avatar).into(binding.imgProfile)
                    }
                    binding.textInputNameProfile.setText(user.name)
                }
            }

    }

    private fun initToolBar() {

        val toolbar = binding.includeTbProfile.tbMain

        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }

    }

    private fun setProfileName(name: String) {
        db.collection(Constants.USERS_COLLECTION)
            .document(auth.currentUser!!.uid)
            .update("name", name)
            .addOnSuccessListener {
                Toast.makeText(this, "Nome atualizado com sucesso", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { error ->
                Toast.makeText(this, "Não foi possível atualizar o perfil", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun saveImagetoStorage(uri: Uri) {

        storage.getReference("images")
            .child("users")
            .child(auth.currentUser!!.uid)
            .putFile(uri)
            .addOnSuccessListener { taskSnapshot ->

                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->

                    val data = mapOf(
                        "avatar" to uri.toString()
                    )

                    db.collection(Constants.USERS_COLLECTION)
                        .document(auth.currentUser!!.uid)
                        .update(data)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Imagem salva com sucesso", Toast.LENGTH_SHORT)
                                .show()
                        }.addOnFailureListener {
                            Toast.makeText(
                                this,
                                "Erro ao tentar salva a imagem",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }

            }.addOnFailureListener {
                Toast.makeText(this, "Não foi possível enviar a imagem", Toast.LENGTH_SHORT).show()
            }
    }

    private fun gallery() {
        openGallery = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {
                binding.imgProfile.setImageURI(uri)

                saveImagetoStorage(uri)
            } else {
                message("Imagem não selecionada")
            }

        }
    }

}


