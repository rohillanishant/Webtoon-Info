package com.example.webtooninfo.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.webtooninfo.Titles

@Dao
interface FavouritesDao {
    @Insert
    fun insertTitle(favouritesEntity: FavouritesEntity)

    @Delete
    fun deleteTitle(favouritesEntity: FavouritesEntity)

    @Query("SELECT * FROM favourites")
    fun getAllFavourites():List<FavouritesEntity>

    @Query("SELECT * FROM favourites WHERE id=:titleId")
    fun getFavouriteById(titleId:Int):FavouritesEntity?
}