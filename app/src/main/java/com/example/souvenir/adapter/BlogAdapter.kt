package com.example.souvenir.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.souvenir.R
import com.example.souvenir.retrofit.Blog

class BlogAdapter(private val context: Context, private val blogs: List<Blog>) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    inner class BlogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val blogImageView: ImageView = itemView.findViewById(R.id.blogImageView)
        private val blogTitleTextView: TextView = itemView.findViewById(R.id.blogTitleTextView)

        fun bind(blog: Blog) {
            // 블로그 이미지와 제목을 설정
            Glide.with(context)
                .load(blog.urlImage)
                .into(blogImageView)

            blogTitleTextView.text = blog.urlTitle

            // 클릭 리스너 설정
            itemView.setOnClickListener {
                // 블로그 URL이 null이 아닐 때만 열기
                blog.url?.let { url ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_blog, parent, false)
        return BlogViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        holder.bind(blogs[position])
    }

    override fun getItemCount() = blogs.size
}
