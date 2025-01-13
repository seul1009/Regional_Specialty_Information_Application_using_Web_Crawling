package com.example.souvenir

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.souvenir.adapter.HouseListAdapter
import com.example.souvenir.adapter.HouseViewPagerAdapter
import com.example.souvenir.retrofit.HouseDto
import com.example.souvenir.retrofit.HouseModel
import com.example.souvenir.retrofit.RetrofitClient
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import com.naver.maps.map.widget.LocationButtonView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Intent
import android.webkit.WebView // 3D 모델링 보기 위해서
import android.webkit.WebViewClient // 3D 모델링 보기 위해서
import android.view.View
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback

class MapActivity : AppCompatActivity(), OnMapReadyCallback, Overlay.OnClickListener {

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    // Lazy initialization of views
    private val mapView: MapView by lazy { findViewById(R.id.mapView) }
    private val viewPager: ViewPager2 by lazy { findViewById(R.id.houseViewPager) }
    private val viewPagerAdapter = HouseViewPagerAdapter(itemClicked = {
        onHouseModelClicked(houseModel = it)
    })

    private val recyclerView: RecyclerView by lazy { findViewById(R.id.recyclerView) }
    private val recyclerViewAdapter = HouseListAdapter()
    private val currentLocationButton: LocationButtonView by lazy { findViewById(R.id.currentLocationButton) }
    private val bottomSheetTitleTextView: TextView by lazy { findViewById(R.id.bottomSheetTitleTextView) }

    // Add lazy initialization for the WebView
    private lateinit var webView: WebView

    private lateinit var selectedLocation: String  // 선택된 지역 저장

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // WebView 초기화
        webView = findViewById(R.id.webView)

        // WebView 설정
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()  // 웹뷰 클라이언트 설정

        // Intent로부터 선택된 지역 정보 받기
        selectedLocation = intent.getStringExtra("SELECTED_LOCATION") ?: ""

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        initHouseViewPager()
        initHouseRecyclerView()

        // Hide the WebView initially
        webView.visibility = View.GONE

        // Set click listener for the button
        findViewById<Button>(R.id.showModelButton).setOnClickListener {
            // Toggle the visibility of the WebView
            if (webView.visibility == View.GONE) {
                webView.visibility = View.VISIBLE
                // Load the URL into WebView
                webView.loadUrl("https://sketchfab.com/models/0b434af1d2704f848dfd8a5936b7305a/embed")
            } else {
                webView.visibility = View.GONE
            }
        }

        // BottomSheetBehavior 초기화
        val bottomSheet = findViewById<View>(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // BottomSheet가 완전히 펼쳐졌을 때 버튼을 숨김
                        findViewById<Button>(R.id.showModelButton).visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_COLLAPSED,
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        // BottomSheet가 축소되었거나 숨겨졌을 때 버튼을 보임
                        findViewById<Button>(R.id.showModelButton).visibility = View.VISIBLE
                    }
                    else -> {
                        // 기타 상태에서는 버튼을 보이게 함
                        findViewById<Button>(R.id.showModelButton).visibility = View.VISIBLE
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // 슬라이드 이벤트를 선택적으로 처리할 수 있음
            }
        })
    }


    override fun onMapReady(map: NaverMap) {
        naverMap = map
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0

        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.497801, 127.027591))
        naverMap.moveCamera(cameraUpdate)

        val uiSetting = naverMap.uiSettings
        uiSetting.isLocationButtonEnabled = false

        currentLocationButton.map = naverMap

        locationSource = FusedLocationSource(this@MapActivity, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        // RetrofitClient 사용하여 데이터 가져오기
        getHouseListFromAPI()
    }

    private fun getHouseListFromAPI() {
        RetrofitClient.houseService.getHouseList().enqueue(object : Callback<HouseDto> {
            override fun onResponse(call: Call<HouseDto>, response: Response<HouseDto>) {
                if (!response.isSuccessful) {
                    Log.d("Retrofit", "실패1")
                    return
                }

                response.body()?.let { dto ->
                    // 선택된 지역으로 필터링
                    val filteredItems = dto.items.filter { it.region == selectedLocation }
                    updateMarker(filteredItems)  // 필터링된 항목으로 마커 업데이트
                    viewPagerAdapter.submitList(filteredItems)  // 필터링된 항목으로 ViewPager 업데이트
                    recyclerViewAdapter.submitList(filteredItems)  // 필터링된 항목으로 RecyclerView 업데이트
                    bottomSheetTitleTextView.text = "${filteredItems.size}개의 기념품"  // 필터링된 항목 수로 텍스트 업데이트
                }
            }

            override fun onFailure(call: Call<HouseDto>, t: Throwable) {
                Log.d("Retrofit", "실패2")
                Log.d("Retrofit", t.stackTraceToString())
            }
        })
    }

    private fun initHouseViewPager() {
        viewPager.adapter = viewPagerAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val selectedHouseModel = viewPagerAdapter.currentList[position]
                val cameraUpdate = CameraUpdate.scrollTo(LatLng(selectedHouseModel.lat, selectedHouseModel.lng))
                    .animate(CameraAnimation.Easing)
                naverMap.moveCamera(cameraUpdate)
            }
        })
    }

    private fun initHouseRecyclerView() {
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun updateMarker(houses: List<HouseModel>) {
        houses.forEach { house ->
            val marker = Marker()
            marker.position = LatLng(house.lat, house.lng)
            marker.onClickListener = this
            marker.map = naverMap
            marker.tag = house.id
            marker.icon = MarkerIcons.BLACK
            marker.iconTintColor = Color.RED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE)
            return

        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) {
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
    }

    override fun onClick(overlay: Overlay): Boolean {
        val selectedModel = viewPagerAdapter.currentList.firstOrNull {
            it.id == overlay.tag
        }
        selectedModel?.let {
            val position = viewPagerAdapter.currentList.indexOf(it)
            viewPager.currentItem = position
        }
        return true
    }

    private fun onHouseModelClicked(houseModel: HouseModel) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "${houseModel.title} ${houseModel.price} 사진 보기(${houseModel.imgUrl})"
            )
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, null))
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}