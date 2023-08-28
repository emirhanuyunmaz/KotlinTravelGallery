package com.emirhanuyunmaz.kotlintravelgallery.recyclerview_adapters

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.emirhanuyunmaz.kotlintravelgallery.views.AddImagesAndLocationActivity
import com.emirhanuyunmaz.kotlintravelgallery.R
import com.emirhanuyunmaz.kotlintravelgallery.databinding.GalleryRowBinding
import com.emirhanuyunmaz.kotlintravelgallery.databinding.ImagesLargeAlertBinding
import com.emirhanuyunmaz.kotlintravelgallery.room_database_travel.TravelAndLocation
import com.emirhanuyunmaz.kotlintravelgallery.room_database_travel.TravelAndLocationDatabase
import com.emirhanuyunmaz.kotlintravelgallery.selectedTravel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryAdapter(var imagesList:ArrayList<TravelAndLocation>,val context: Context) :RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    private val db= Room.databaseBuilder(
        context,
        TravelAndLocationDatabase::class.java ,"TRAVEL_AND_LOCATION"
    ).build()

    private val dao=db.travelAndLocationDao()
    private lateinit var job: Job

    class GalleryViewHolder(var binding:GalleryRowBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val inflateBinding=GalleryRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return GalleryViewHolder(inflateBinding)
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        var img=imagesList[position].images
        //var d=Drawable.createFromStream(ByteArrayInputStream(img),null)
        holder.binding.imageViewGallery.setImageBitmap(img.toBitmap())
        //holder.binding.imageViewGallery.setImageDrawable(d)

        holder.itemView.setOnClickListener {
            var dialogAlert=Dialog(holder.itemView.context)
            val inflater = holder.itemView.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var dialog=ImagesLargeAlertBinding.inflate(inflater)
            dialogAlert.setContentView(dialog.root)
            dialog.imageViewLarheImage.setImageBitmap(img.toBitmap())
            dialogAlert.show()
        }

        holder.itemView.setOnLongClickListener {
            showPopup(it,position)
            true
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    fun showPopup(view : View, position: Int){
        val popup=PopupMenu(context, view)
        popup.inflate(R.menu.images_popup_menu)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
            when(it.itemId){
                R.id.popupDelete -> {

                    job= CoroutineScope(Dispatchers.IO).launch {
                        dao.delete(imagesList[position])
                        withContext(Dispatchers.Main){
                            imagesList.remove(imagesList[position])
                            notifyItemRemoved(position)
                            notifyDataSetChanged()
                        }
                    }
                    Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show()
                }

                R.id.popupDetail -> {
                    val intent=Intent(context, AddImagesAndLocationActivity::class.java).let {
                        it.putExtra("info","old")
                    }
                    selectedTravel.selected=imagesList[position]
                    context.startActivity(intent)
                    Toast.makeText(context, "Detail", Toast.LENGTH_SHORT).show()
                }
            }

            true
        })
        popup.show()
    }

    fun ByteArray.toBitmap(): Bitmap {
        return BitmapFactory.decodeByteArray(this, 0, this.size)
    }

}