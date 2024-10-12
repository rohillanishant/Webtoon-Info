package com.example.webtooninfo.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [FavouritesEntity::class], version = 1)
abstract class FavouritesDatabase : RoomDatabase(){
    abstract fun FavouritesDao():FavouritesDao
}
