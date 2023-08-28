package com.emirhanuyunmaz.kotlintravelgallery.room_database_travel

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [TravelAndLocation::class], version = 1)
abstract class TravelAndLocationDatabase :RoomDatabase(){
    abstract fun travelAndLocationDao():TravelAndLocationDao
}