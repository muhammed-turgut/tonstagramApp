package com.muhammedturgut.tonstagram.View

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.muhammedturgut.tonstagram.Fragments.AddFragment
import com.muhammedturgut.tonstagram.Fragments.HomeFragment
import com.muhammedturgut.tonstagram.Fragments.LikeFragment
import com.muhammedturgut.tonstagram.Fragments.ProfilFragment
import com.muhammedturgut.tonstagram.Fragments.SerachFragment
import com.muhammedturgut.tonstagram.R
import com.muhammedturgut.tonstagram.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var bottomNavView: BottomNavigationView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FirebaseAuth referansını al
        auth = FirebaseAuth.getInstance()

        // Drawer Layout ve NavigationView referanslarını al
        drawerLayout = binding.drawerLayout
        navView = binding.sliderBar
        bottomNavView = binding.bottomNavigation

        // İlk açılışta HomeFragment'i göster
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // BottomNavigationView item seçme dinleyicisi
        bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    binding.openDrawerButton.visibility = View.GONE
                    loadFragment(HomeFragment())
                    true
                }
                R.id.serch -> {
                    binding.openDrawerButton.visibility = View.GONE
                    loadFragment(SerachFragment())
                    true
                }
                R.id.add -> {
                    binding.openDrawerButton.visibility = View.GONE
                    loadFragment(AddFragment())
                    true
                }
                R.id.like -> {
                    binding.openDrawerButton.visibility = View.GONE
                    loadFragment(LikeFragment())
                    true
                }
                R.id.user -> {
                    binding.openDrawerButton.visibility = View.VISIBLE
                    loadFragment(ProfilFragment())
                    true
                }
                else -> false
            }
        }

        // Drawer açma butonuna tıklama olayını ayarla
        binding.openDrawerButton.setOnClickListener {
            if (!drawerLayout.isDrawerOpen(navView)) {
                drawerLayout.openDrawer(navView)
            } else {
                drawerLayout.closeDrawer(navView)
            }
        }

        // Kullanıcı bilgilerini güncelle
        updateHeaderView()

        // NavigationView item click listener
        navView.setNavigationItemSelectedListener(this)
    }

    // Fragment yükleme fonksiyonu
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }

    // Kullanıcı bilgilerini güncelleme fonksiyonu
    private fun updateHeaderView() {
        val headerView: View = navView.getHeaderView(0)
        val usernameTextView: TextView = headerView.findViewById(R.id.userName)

        // Kullanıcı giriş yapmışsa usernameTextView'i güncelle
        auth.currentUser?.let {
            usernameTextView.text = it.email
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.arcihv -> {}
            R.id.yourarchiv -> {}
            R.id.nametag -> {}
            R.id.saved -> {}
            R.id.closefriends -> {}
            R.id.discoverpeople -> {}
            R.id.openfacebook -> {}
            R.id.logout -> {
                auth.signOut()
                val intent = Intent(this@MainActivity, LoginScreen::class.java)
                startActivity(intent)
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.END)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Kaynakları serbest bırakma
        navView.setNavigationItemSelectedListener(null)
        bottomNavView.setOnItemSelectedListener(null)
    }
}