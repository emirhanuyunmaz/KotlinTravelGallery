package com.emirhanuyunmaz.kotlintravelgallery.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.emirhanuyunmaz.kotlintravelgallery.R
import com.emirhanuyunmaz.kotlintravelgallery.databinding.ActivityMainBinding
import com.emirhanuyunmaz.kotlintravelgallery.recyclerview_adapters.MainAdapter
import com.emirhanuyunmaz.kotlintravelgallery.room_database_travel.TravelAndLocationDao
import com.emirhanuyunmaz.kotlintravelgallery.room_database_travel.TravelAndLocationDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var db: TravelAndLocationDatabase
    private lateinit var dao:TravelAndLocationDao
    private lateinit var job:Job
    private lateinit var cityList:ArrayList<String>
    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain)

        db = Room.databaseBuilder(
            applicationContext,
            TravelAndLocationDatabase::class.java ,"TRAVEL_AND_LOCATION"
        ).build()

        dao=db.travelAndLocationDao()

        cityList=ArrayList()

        adapter= MainAdapter(cityList)

        binding.recyclerViewMain.layoutManager=LinearLayoutManager(this@MainActivity)
        binding.recyclerViewMain.adapter=adapter


    }

    override fun onStart() {
        super.onStart()
        getData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater=MenuInflater(this@MainActivity).inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId== R.id.menu_add_city){
            val intent=Intent(this@MainActivity, CityAddActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }


    private fun getData(){

        cityList.clear()
        job= CoroutineScope(Dispatchers.IO).launch {
            var data=ArrayList(dao.getCityNames())
            data.forEach {
                cityList.add(it.city)
            }

            withContext(Dispatchers.Main){
                adapter= MainAdapter(cityList)

                binding.recyclerViewMain.layoutManager=LinearLayoutManager(this@MainActivity)
                binding.recyclerViewMain.adapter=adapter
            }
        }


    }




}