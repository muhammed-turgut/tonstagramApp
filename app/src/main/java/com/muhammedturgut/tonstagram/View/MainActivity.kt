package com.muhammedturgut.tonstagram.View

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.muhammedturgut.tonstagram.Fragments.AddFragment
import com.muhammedturgut.tonstagram.Fragments.HomeFragment
import com.muhammedturgut.tonstagram.Fragments.LikeFragment
import com.muhammedturgut.tonstagram.Fragments.ProfilFragment
import com.muhammedturgut.tonstagram.Fragments.SerachFragment
import com.muhammedturgut.tonstagram.R
import com.muhammedturgut.tonstagram.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // İlk açılışta HomeFragment'i göster
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // BottomNavigationView item seçme dinleyicisi
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.serch -> {
                    loadFragment(SerachFragment())
                    true
                }
                R.id.add -> {
                    loadFragment(AddFragment())
                    true
                }
                R.id.like -> {
                    loadFragment(LikeFragment())
                    true
                }
                R.id.user -> {
                    loadFragment(ProfilFragment())
                    true
                }
                else -> false
            }
        }
    }

    // Fragment yükleme fonksiyonu
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}