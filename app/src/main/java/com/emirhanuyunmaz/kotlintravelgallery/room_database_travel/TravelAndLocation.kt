package com.emirhanuyunmaz.kotlintravelgallery.room_database_travel

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class TravelAndLocation(

    @ColumnInfo(name = "city")
    var city:String,

    @ColumnInfo(name = "longitude")
    var longitude:Double,

    @ColumnInfo(name = "latitude")
    var latitude:Double,

    @ColumnInfo(name = "images")
    var images: ByteArray

):Serializable{

    @PrimaryKey(autoGenerate = true)
    var id:Int=0

}
