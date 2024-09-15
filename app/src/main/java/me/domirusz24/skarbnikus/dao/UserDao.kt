package me.domirusz24.skarbnikus.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import me.domirusz24.skarbnikus.model.Task
import me.domirusz24.skarbnikus.model.User
import me.domirusz24.skarbnikus.model.UserData


@Dao
interface UserDao {
    @Query("SELECT * FROM user ORDER BY surname")
    fun getAllUsers(): List<User>

    @Query("SELECT COUNT(*) FROM user")
    fun getUserCount(): Int

    @Query("UPDATE user SET money = :money WHERE uid = :uid")
    fun updateMoney(uid: Int, money: Int)

    @Query("UPDATE user SET name = :name WHERE uid = :uid")
    fun updateName(uid: Int, name: String)

    @Query("UPDATE user SET surname = :surname WHERE uid = :uid")
    fun updateSurname(uid: Int, surname: String)

    @Query("UPDATE user SET money = money - :amount")
    fun removeMoney(amount: Int)

    @Query("UPDATE user SET money = money + :amount")
    fun addMoney(amount: Int)

    @Insert(entity = User::class)
    fun insertUser(userData: UserData)

    @Delete
    fun deleteUser(user: User)
}