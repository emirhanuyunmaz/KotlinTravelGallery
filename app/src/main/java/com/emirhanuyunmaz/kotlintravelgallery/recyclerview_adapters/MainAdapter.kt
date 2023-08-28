package com.emirhanuyunmaz.kotlintravelgallery.recyclerview_adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emirhanuyunmaz.kotlintravelgallery.views.GalleryActivity
import com.emirhanuyunmaz.kotlintravelgallery.databinding.MainRowBinding

class MainAdapter(var cityArrayList:ArrayList<String>) :RecyclerView.Adapter<MainAdapter.MainVH>() {

    class MainVH(var binding: MainRowBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainVH {
        val mainBinding=MainRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MainVH(mainBinding)
    }

    override fun getItemCount(): Int {
        return cityArrayList.size
    }

    override fun onBindViewHolder(holder: MainVH, position: Int) {
        holder.binding.textViewMainRecyclerViewRow.text=cityArrayList[position]
        holder.itemView.setOnClickListener {
            val intent= Intent(holder.itemView.context, GalleryActivity::class.java).let {
                it.putExtra("cityName",cityArrayList[position])
            }
            holder.itemView.context.startActivity(intent)
        }
    }
}