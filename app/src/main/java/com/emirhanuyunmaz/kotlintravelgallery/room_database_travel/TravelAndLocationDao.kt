package com.emirhanuyunmaz.kotlintravelgallery.room_database_travel

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TravelAndLocationDao {

    @Query("SELECT * FROM TravelAndLocation")
    fun getAllData():List<TravelAndLocation>

    @Query("SELECT * FROM TravelAndLocation WHERE id =:getId")
    fun getSelectData(getId:Int):TravelAndLocation

    @Query("SELECT city FROM TravelAndLocation")
    fun getCityName():List<String>

    @Query("SELECT * FROM TravelAndLocation WHERE city IN (:cityName)")
    fun getCityImages(cityName:String):List<TravelAndLocation>

    @Query("SELECT * FROM TravelAndLocation  GROUP BY city")
    fun getCityNames():List<TravelAndLocation>

    @Insert
    fun insert(vararg travelAndLocation: TravelAndLocation)

    @Delete
    fun delete(travelAndLocation: TravelAndLocation)

}