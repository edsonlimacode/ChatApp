package com.edsonlimadev.chatapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.edsonlimadev.chatapp.adapters.ViewPageMainAdapter
import com.edsonlimadev.chatapp.databinding.ActivityHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
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

        initializeToolBar()
        initializeTabsBar()
    }

    private fun initializeTabsBar() {

        val tabLayout = binding.tabMain
        val viewPage = binding.pvMain

        val listTabs = listOf("Conversas", "Contatos")

        viewPage.adapter = ViewPageMainAdapter(listTabs, supportFragmentManager, lifecycle)

        TabLayoutMediator(tabLayout, viewPage){ tab, position ->
            tab.text = listTabs[position]
        }.attach()

    }

    private fun initializeToolBar() {

        val toolbar = binding.includeHomeTb.tbMain

        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = "Chat"
        }

        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                   menuInflater.inflate(R.menu.main_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when(menuItem.itemId){
                        R.id.menuItemPerfil -> {
                            startActivity(Intent(applicationContext, ProfileActivity::class.java))
                        }
                        R.id.menuItemLogout -> {
                            auth.signOut()
                            startActivity(Intent(applicationContext, MainActivity::class.java))
                        }
                    }
                    return true
                }

            }
        )
    }
}