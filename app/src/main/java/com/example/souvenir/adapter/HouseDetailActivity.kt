package com.example.souvenir.adapter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.souvenir.R
import com.example.souvenir.retrofit.Blog
import com.example.souvenir.retrofit.BlogResponse
import com.example.souvenir.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HouseDetailActivity : AppCompatActivity() {

    private lateinit var matchingBlogs: List<Blog>  // 일치하는 블로그 목록을 저장할 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_house_detail)

        val houseTitleTextView = findViewById<TextView>(R.id.houseTitleTextView)
        val houseAddressTextView = findViewById<TextView>(R.id.houseAddressTextView)
        val houseImageView = findViewById<ImageView>(R.id.houseImageView)
        val blogRecyclerView = findViewById<RecyclerView>(R.id.blogRecyclerView)

        // Intent로부터 데이터 가져오기
        val houseTitle = intent.getStringExtra("houseName")
        val houseAddress = intent.getStringExtra("houseAddress")
        val houseImageUrl = intent.getStringExtra("houseImageUrl")

        houseTitleTextView.text = houseTitle
        houseAddressTextView.text = houseAddress

        Glide.with(this)
            .load(houseImageUrl)
            .into(houseImageView)

        // RecyclerView 설정
        blogRecyclerView.layoutManager = LinearLayoutManager(this)

        // Retrofit을 통해 Blog 데이터 가져오기
        RetrofitClient.houseService.getBlogs().enqueue(object : Callback<BlogResponse> {
            override fun onResponse(call: Call<BlogResponse>, response: Response<BlogResponse>) {
                if (response.isSuccessful) {
                    val blogList = response.body()?.items
                    Log.d("HouseDetailActivity", "Blogs fetched successfully: $blogList")

                    // houseTitleTextView의 텍스트와 일치하는 Blog 데이터를 찾기
                    val houseTitleString = houseTitle ?: ""
                    matchingBlogs = blogList?.filter { it.name == houseTitleString } ?: emptyList()

                    // RecyclerView에 어댑터 설정
                    blogRecyclerView.adapter = BlogAdapter(this@HouseDetailActivity, matchingBlogs)
                } else {
                    Log.e("HouseDetailActivity", "Failed to fetch blogs: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<BlogResponse>, t: Throwable) {
                Log.e("HouseDetailActivity", "API call failed", t)
            }
        })
    }
}