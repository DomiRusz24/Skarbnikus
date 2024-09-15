package me.domirusz24.skarbnikus

import androidx.room.Database
import androidx.room.RoomDatabase
import me.domirusz24.skarbnikus.dao.TaskDao
import me.domirusz24.skarbnikus.dao.UserDao
import me.domirusz24.skarbnikus.model.Task
import me.domirusz24.skarbnikus.model.User

@Database(entities = [User::class, Task::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
}