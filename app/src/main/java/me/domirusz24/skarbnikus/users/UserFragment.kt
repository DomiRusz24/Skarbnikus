package me.domirusz24.skarbnikus.users

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import me.domirusz24.skarbnikus.MainActivity
import me.domirusz24.skarbnikus.R
import me.domirusz24.skarbnikus.model.UserData

class UserFragment : Fragment(R.layout.fragment_users) {

    private val mainState: MainActivity.MainState by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_users, container, false)

        val db = mainState.db.value!!

        val userAdapter = UserAdapter(db)

        val recyclerView = view.findViewById<RecyclerView>(R.id.userList)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = userAdapter

        view.findViewById<FloatingActionButton>(R.id.addUserButton).setOnClickListener {
            val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(view.context)

            val view = LayoutInflater.from(view.context).inflate(R.layout.add_user_dialog, container, false)

            val nameInput = view.findViewById<EditText>(R.id.userNameInput)
            val surnameInput = view.findViewById<EditText>(R.id.userSurnameInput)


            alertDialogBuilder.setView(view)

            alertDialogBuilder
                .setTitle("Podaj imię")
                .setCancelable(false)
                .setPositiveButton("Stwórz") { _, _ ->
                    if (nameInput.text.toString().isNotBlank()) {
                        db.userDao().insertUser(UserData(nameInput.text.toString(), surnameInput.text.toString()))
                        userAdapter.getUsersFromDB()
                        userAdapter.notifyDataSetChanged()
                    }
                }
                .setNegativeButton("Wróć") { dialog, id ->
                    dialog.cancel()
                }.create().show()


        }


        return view
    }
}