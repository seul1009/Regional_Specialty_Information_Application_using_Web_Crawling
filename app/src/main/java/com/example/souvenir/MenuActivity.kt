package com.example.souvenir

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_navigation, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                // Home 버튼 클릭 시 동작
                Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.heart -> {
                // Like 버튼 클릭 시 동작
                Toast.makeText(this, "Like clicked", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.user -> {
                // User 버튼 클릭 시 동작
                Toast.makeText(this, "User clicked", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}