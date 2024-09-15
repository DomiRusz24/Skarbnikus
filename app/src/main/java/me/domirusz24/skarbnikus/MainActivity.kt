package me.domirusz24.skarbnikus

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room
import com.google.android.material.bottomnavigation.BottomNavigationView
import me.domirusz24.skarbnikus.goals.GoalsFragment
import me.domirusz24.skarbnikus.users.UserFragment


class MainActivity : AppCompatActivity() {

    class MainState() : ViewModel() {
        val db: MutableLiveData<AppDatabase> = MutableLiveData()
    }

    private val mainState: MainState by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db: AppDatabase = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "skarbnikus"
        ).allowMainThreadQueries().build();

        mainState.db.value = db

        val userFragment = UserFragment()
        val goalsFragment = GoalsFragment()

        setCurrentFragment(userFragment)

        findViewById<BottomNavigationView>(R.id.bottomNavBar).setOnItemSelectedListener {
            when(it.itemId){
                R.id.usersMenuOption->setCurrentFragment(userFragment)
                R.id.goalsMenuOption->{
                    if (db.userDao().getUserCount() > 0) {
                        setCurrentFragment(goalsFragment)
                    } else {
                        AlertDialog.Builder(this)
                            .setTitle("Uwaga!")
                            .setMessage("Stwórz użytkownika zanim stworzysz cel!")
                            .setPositiveButton("OK", null)
                            .show()
                        return@setOnItemSelectedListener false
                    }
                }
            }
            true
        }

    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply { replace(R.id.frameHost, fragment).commit() }
}