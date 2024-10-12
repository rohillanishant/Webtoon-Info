package com.example.webtooninfo.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favourites")
data class FavouritesEntity(
    @PrimaryKey val id:Int,
    @ColumnInfo val name:String,
    @ColumnInfo val photo:String?,
    @ColumnInfo val brief:String,
    @ColumnInfo val reads:Float?,
    @ColumnInfo val content:String
)
