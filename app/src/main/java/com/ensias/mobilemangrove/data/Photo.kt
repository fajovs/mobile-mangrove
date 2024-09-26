package com.ensias.mobilemangrove.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "photo",
    foreignKeys = [
        ForeignKey(
            entity = Plant::class,
            parentColumns = ["id"],
            childColumns = ["plant_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["plant_id"])]
)
data class Photo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val plant_id: Int,
    val photo: ByteArray
)
