package com.ensias.mobilemangrove.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plant")
data class Plant(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val scientificName: String,
    val location: String,
    val description: String
)
