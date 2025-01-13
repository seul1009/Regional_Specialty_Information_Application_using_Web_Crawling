package com.example.souvenir

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LocalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_local)
        setContentView(R.layout.activity_menu) // 레이아웃 변경

        // Spinner 초기화
        val locationSpinner = findViewById<Spinner>(R.id.location_spinner)
        val locations = listOf("경주", "부산", "전주", "진주", "춘천") // 예시 지역 리스트

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        locationSpinner.adapter = adapter

        val selectButton = findViewById<Button>(R.id.select_button)
        selectButton.setOnClickListener {
            val selectedLocation = locationSpinner.selectedItem.toString()
            onLocalSelected(selectedLocation)
        }
    }

    private fun onLocalSelected(location: String) {
        // 지역 선택 로직을 구현하세요. 예를 들어, 선택된 지역 정보를 저장하거나 전달.
        // 여기에 토스트 메시지로 지역 선택 완료를 표시
        Toast.makeText(this, "선택된 지역: $location", Toast.LENGTH_SHORT).show()

        // MapActivity로 전환
        val intent = Intent(this, MapActivity::class.java)
        intent.putExtra("SELECTED_LOCATION", location)  // 선택된 지역 정보를 Intent에 추가
        startActivity(intent)
        finish()
    }
}
