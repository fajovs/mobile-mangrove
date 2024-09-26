package com.ensias.mobilemangrove.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query



@Dao
interface PlantDao {
    @Insert
    suspend fun insert(plant: Plant)

    @Query("SELECT * FROM plant")
    suspend fun getAllPlants(): List<Plant>

    @Query("SELECT * FROM plant WHERE name =:plantName LIMIT 1")
    suspend fun getPlantByName(plantName: String): Plant?

    @Query("SELECT * FROM plant WHERE id =:plantId LIMIT 1")
    suspend fun getPlantById(plantId:Int): Plant?
}
