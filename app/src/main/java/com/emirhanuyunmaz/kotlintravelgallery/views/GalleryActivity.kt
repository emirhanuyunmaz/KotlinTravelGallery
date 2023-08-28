package com.emirhanuyunmaz.kotlintravelgallery.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Room
import com.emirhanuyunmaz.kotlintravelgallery.databinding.ActivityGalleryBinding
import com.emirhanuyunmaz.kotlintravelgallery.recyclerview_adapters.GalleryAdapter
import com.emirhanuyunmaz.kotlintravelgallery.room_database_travel.TravelAndLocation
import com.emirhanuyunmaz.kotlintravelgallery.room_database_travel.TravelAndLocationDao
import com.emirhanuyunmaz.kotlintravelgallery.room_database_travel.TravelAndLocationDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext


class GalleryActivity : AppCompatActivity() {

    private lateinit var binding:ActivityGalleryBinding
    private lateinit var db:TravelAndLocationDatabase
    private lateinit var dao:TravelAndLocationDao
    private lateinit var jobDownload:Job
    private lateinit var adapter: GalleryAdapter
    private lateinit var imagesList:ArrayList<TravelAndLocation>
    private lateinit var getCityName :String
    private lateinit var cityImg:ArrayList<TravelAndLocation>
    private var lock= Mutex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarGallery)


        db = Room.databaseBuilder(
            applicationContext,
            TravelAndLocationDatabase::class.java ,"TRAVEL_AND_LOCATION"
        ).build()

        val getIntentData=intent
        getCityName=getIntentData.getStringExtra("cityName")!!
        println("City ::" + getCityName)
        binding.toolbarGallery.subtitle=getCityName

        dao=db.travelAndLocationDao()

        imagesList= ArrayList()
        cityImg=ArrayList()

        adapter= GalleryAdapter(cityImg,this@GalleryActivity)

        binding.recyclerViewGallery.layoutManager=GridLayoutManager(this@GalleryActivity,2)
        binding.recyclerViewGallery.adapter=adapter

        getData()

    }

    override fun onStart() {
        super.onStart()
        println("start")
        getData()
    }


    fun addImages_OnClick(view: View){
        val intent=Intent(this@GalleryActivity, AddImagesAndLocationActivity::class.java).let {
            it.putExtra("cityName",getCityName)
            it.putExtra("info","new")//add-update
        }
        startActivity(intent)
    }

    private fun getData(){

        binding.progressBar.visibility=View.VISIBLE

        jobDownload= CoroutineScope(Dispatchers.IO).launch {
            binding.recyclerViewGallery.visibility=View.INVISIBLE
            imagesList.clear()
            cityImg.clear()
            imagesList= ArrayList(dao.getCityImages(getCityName))
            println("image::"+imagesList.size)
            /*for (cityImage in imagesList){
                if(cityImage.city.equals(getCityName)){
                    cityImg.add(cityImage)
                }
            }*/
            //println(cityImg.size)
            withContext(Dispatchers.Main){
                binding.recyclerViewGallery.layoutManager=GridLayoutManager(this@GalleryActivity,2)
                adapter= GalleryAdapter(imagesList,this@GalleryActivity)
                binding.recyclerViewGallery.adapter=adapter

                binding.recyclerViewGallery.visibility=View.VISIBLE
                binding.progressBar.visibility=View.INVISIBLE


            }

        }
        //println("Control::"+jobDownload.isActive)







    }

    override fun onStop() {
        super.onStop()
        println("stop")
//        jobDownload.cancel()
    }

    private suspend fun download() : ArrayList<TravelAndLocation>{
        binding.recyclerViewGallery.visibility=View.INVISIBLE
        imagesList.clear()
        cityImg.clear()
        imagesList= ArrayList(dao.getCityImages(getCityName))
        return imagesList

    }



}