package com.example.souvenir.adapter

import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.souvenir.R
import com.example.souvenir.retrofit.HouseModel

class HouseListAdapter : ListAdapter<HouseModel, HouseListAdapter.ItemViewHolder>(differ) {
    inner class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(houseModel: HouseModel) {
            val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
            val priceTextView = view.findViewById<TextView>(R.id.priceTextView)
            val addressTextView = view.findViewById<TextView>(R.id.addressTextView)
            val thumbnailImageView = view.findViewById<ImageView>(R.id.thumbnailImageView)


            titleTextView.text = houseModel.name
            addressTextView.text = houseModel.address
            priceTextView.text = houseModel.price

            Glide.with(thumbnailImageView.context)
                .load(houseModel.imgUrl)
                .transform(CenterCrop(), RoundedCorners(dp2px(thumbnailImageView.context, 12)))
                .into(thumbnailImageView)

            // 이미지 클릭 리스너 추가
            thumbnailImageView.setOnClickListener {
                val context = it.context
                val intent = Intent(context, HouseDetailActivity::class.java)
                intent.putExtra("houseName", houseModel.name) // 상품명
                intent.putExtra("houseAddress", houseModel.address) // 주소 전달
                intent.putExtra("houseImageUrl", houseModel.imgUrl) // 이미지 주소 전달
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(inflater.inflate(R.layout.item_house, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    private fun dp2px(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics
        ).toInt()
    }

    companion object {
        val differ = object : DiffUtil.ItemCallback<HouseModel>() {
            override fun areItemsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
