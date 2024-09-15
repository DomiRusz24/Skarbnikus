package me.domirusz24.skarbnikus.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "surname") var surname: String,
    @ColumnInfo(name = "money", defaultValue = "0") var money: Int
)


data class UserData (
    val name: String,
    val surname: String
)
