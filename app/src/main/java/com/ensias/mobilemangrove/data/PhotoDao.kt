package com.ensias.mobilemangrove.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PhotoDao {
    @Insert
    suspend fun insert(photo: Photo)

    @Query("SELECT * FROM photo")
    suspend fun getAllPhotos(): List<Photo>


    @Query("SELECT * FROM photo WHERE id =:photoId LIMIT 1")
    fun getPhotoById(photoId:Int): Photo?

    @Query("SELECT * FROM photo WHERE plant_id = :plantId LIMIT 1")
    suspend fun getPhotosByPlantId(plantId: Int): Photo?
}
