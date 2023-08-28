package com.emirhanuyunmaz.kotlintravelgallery.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.emirhanuyunmaz.kotlintravelgallery.databinding.ActivityCityAddBinding

class CityAddActivity : AppCompatActivity() {

    private lateinit var binding:ActivityCityAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //...Şehir ekleme işlemleri....


    }

    fun cityAdd_OnClick(view:View){
        //City add database
        var cityName=binding.editTextCityName.text.toString()

        if(!cityName.equals("")){
            val intent= Intent(this@CityAddActivity, GalleryActivity::class.java).let {
                it.putExtra("cityName",cityName)
            }
            startActivity(intent)
            finish()
        }else{
            Toast.makeText(this@CityAddActivity, "Please city name enter", Toast.LENGTH_SHORT).show()
        }

    }

}