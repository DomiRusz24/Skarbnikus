package me.domirusz24.skarbnikus.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import me.domirusz24.skarbnikus.model.Task
import me.domirusz24.skarbnikus.model.TaskData
import me.domirusz24.skarbnikus.model.User
import me.domirusz24.skarbnikus.model.UserData

@Dao
interface TaskDao {
    @Query("SELECT * FROM task")
    fun getAllTasks(): List<Task>

    @Delete
    fun deleteTask(task: Task)

    @Insert(entity = Task::class)
    fun insertTask(taskData: TaskData)
}