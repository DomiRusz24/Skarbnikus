package me.domirusz24.skarbnikus.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "amount") var amount: Int,
    @ColumnInfo(name = "split") val split: Boolean,
)

data class TaskData(
    val name: String,
    val amount: Int,
    val split: Boolean
)
