package com.emirhanuyunmaz.kotlintravelgallery.views

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.emirhanuyunmaz.kotlintravelgallery.R
import com.emirhanuyunmaz.kotlintravelgallery.databinding.ActivityAddImagesAndLocationBinding
import com.emirhanuyunmaz.kotlintravelgallery.room_database_travel.TravelAndLocation
import com.emirhanuyunmaz.kotlintravelgallery.room_database_travel.TravelAndLocationDao
import com.emirhanuyunmaz.kotlintravelgallery.room_database_travel.TravelAndLocationDatabase
import com.emirhanuyunmaz.kotlintravelgallery.selectedTravel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class AddImagesAndLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityAddImagesAndLocationBinding
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var db:TravelAndLocationDatabase
    private lateinit var dao: TravelAndLocationDao
    private lateinit var jobInsert:Job
    private var markerLocation :LatLng? =null
    private var selectURI:Uri?=null
    private var selectBitmap:Bitmap?=null
    private lateinit var getCityName:String
    private lateinit var manager:LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddImagesAndLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        register()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@AddImagesAndLocationActivity)


        manager=this@AddImagesAndLocationActivity.getSystemService(Context.LOCATION_SERVICE) as (LocationManager)

        db = Room.databaseBuilder(
            applicationContext,
            TravelAndLocationDatabase::class.java ,"TRAVEL_AND_LOCATION"
        ).build()

        dao=db.travelAndLocationDao()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if(!manager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
            buildAlertMessageNoGps()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        var getIntent=intent

        var info=getIntent.getStringExtra("info")
        if (info.equals("new")){
            getCityName=getIntent.getStringExtra("cityName")!!
            binding.buttonGoToImagesAndLocation.visibility=View.GONE
        }else{

            binding.imageView.isClickable=false
            binding.buttonAddImagesAndLocation.visibility=View.GONE
            var travel= selectedTravel.selected
            binding.imageView.setImageBitmap(travel!!.images.toBitmap())
            var saveLocation=LatLng(travel!!.latitude,travel!!.longitude)
            mMap.addMarker(MarkerOptions().position(saveLocation))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(saveLocation,15f))
        }


        if(!manager.isProviderEnabled( LocationManager.GPS_PROVIDER)){buildAlertMessageNoGps()}
        else{

        //Location request
            when {
                ContextCompat.checkSelfPermission(
                    this@AddImagesAndLocationActivity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {

                    //****************************
                    if(!manager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
                        buildAlertMessageNoGps()
                    }else{
                        /*fusedLocationClient.lastLocation
                            .addOnSuccessListener { location : Location? ->
                                val yourLocation = LatLng(location!!.latitude,location!!.longitude)
                                mMap.addMarker(MarkerOptions().position(yourLocation).title("Your location"))
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yourLocation,17f))
                            }*/
                        //Current location
                        mMap.isMyLocationEnabled=true

                    }

                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    //explain why you do this
                    Snackbar.make(binding.root,"Need permission",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
                        requestPermissionLauncher.launch(
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    }
                }
                else -> {
                    //Requires  permission.
                    requestPermissionLauncher.launch(
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

        }
        //*******************Long Click Add Marker*********************//

        mMap.setOnMapLongClickListener(){
            //Add marker
            mMap.clear()
            markerLocation = LatLng(it!!.latitude,it!!.longitude)
            mMap.addMarker(MarkerOptions().position(markerLocation!!))
        }
    }

    fun addImagesAndlocation_OnClick(view:View){
        if(markerLocation!=null){

            if(selectURI!=null){

                jobInsert= CoroutineScope(Dispatchers.IO).launch {
                    println("Selected URİ::"+selectURI?.path)

                        var byteArrayOutputStream=ByteArrayOutputStream()
                        selectBitmap?.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream)
                        var travel=TravelAndLocation(getCityName,markerLocation!!.longitude,markerLocation!!.latitude,selectBitmap!!.toByteArray())
                        //var travel=TravelAndLocation(getCityName,markerLocation!!.longitude,markerLocation!!.latitude,selectBitmap!!.toByteArray())
                        //var travel=TravelAndLocation(getCityName,markerLocation!!.longitude,markerLocation!!.latitude,compressedImageFile.readBytes())

                        dao.insert(travel)
                        withContext(Dispatchers.Main){
                            finish()
                        }
                }
            }
        }
    }

    fun gotoImagesAndLocation_OnClick(view:View){
        val uri = "https://www.google.com.tw/maps/place/${selectedTravel.selected!!.latitude},${selectedTravel.selected!!.longitude}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intent)
    }

    fun image_OnClick(view: View){
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }


    private fun register(){
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                var bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(uri.toString()))
                selectURI=uri
                binding.imageView.setImageBitmap(bitmap)
                selectBitmap=bitmap
                //binding.imageView.setImageURI(uri)

            } else {
                println("No media selected")
            }
        }
        //*********************************************************//


            requestPermissionLauncher =
                registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {

                        if(ContextCompat.checkSelfPermission(
                                this@AddImagesAndLocationActivity,
                                android.Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED){
                            fusedLocationClient.lastLocation
                                .addOnSuccessListener { location : Location? ->
                                    val yourLocation = LatLng(location!!.latitude,location!!.longitude)
                                    mMap.addMarker(MarkerOptions().position(yourLocation).title("Your location"))
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yourLocation,17f))
                                }
                        }

                        mMap.isMyLocationEnabled=true

                    } else {
                        requestPermissionLauncher.launch(
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }
    }

    fun Bitmap.toByteArray(): ByteArray {
        val stream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, id ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                finish()
                })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                    finish()
                })
        val alert: AlertDialog = builder.create()
        alert.show()
    }
    fun ByteArray.toBitmap(): Bitmap {
        return BitmapFactory.decodeByteArray(this, 0, this.size)
    }



}