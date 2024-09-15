package me.domirusz24.skarbnikus.users

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.domirusz24.skarbnikus.AppDatabase
import me.domirusz24.skarbnikus.R
import me.domirusz24.skarbnikus.model.User
import java.util.Locale

class UserAdapter(val db: AppDatabase) : RecyclerView.Adapter<UserAdapter.UserHolder>() {

    var users: List<User> = db.userDao().getAllUsers()

    class UserHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun getUsersFromDB() {
        users = db.userDao().getAllUsers()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        return UserHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.user_item,
            parent,
            false
        ))
    }

    override fun getItemCount(): Int {
        return users.size
    }

    private fun toFloat(value: Any?): Float {
        if (value is Float) {
            return value
        } else if (value is Number) {
            return value.toFloat()
        } else if (value is String) {
            try {
                return value.toFloat()
            } catch (ignored: NumberFormatException) {
            }
        }
        return 0f
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val user = users[position]

        holder.itemView.apply {
            val tvName: TextView = findViewById(R.id.tvName)
            val tvAmount: TextView = findViewById(R.id.tvAmount)
            val bRemove: ImageButton = findViewById(R.id.bRemove)
            val bAdd: ImageButton = findViewById(R.id.bAdd)
            val etMoney: EditText = findViewById(R.id.etMoney)

            tvAmount.setOnClickListener {
                val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)

                val view = LayoutInflater.from(holder.itemView.context).inflate(R.layout.edit_user_money, null, false)

                val moneyInput = view.findViewById<EditText>(R.id.setUserMoney)

                alertDialogBuilder.setView(view)

                val dialog = alertDialogBuilder
                    .setTitle("Modyfikuj ilość pieniędzy")
                    .setCancelable(false)
                    .setPositiveButton("Zmień") { _, _ ->
                        if (moneyInput.text.toString().isNotBlank()) {
                            val amount = toFloat(moneyInput.text.toString())
                            user.money = (amount * 100f).toInt()
                            db.userDao().updateMoney(user.uid, user.money)
                            notifyItemChanged(position)
                        }
                    }
                    .setNegativeButton("Wróć") { dialog, id ->
                        dialog.cancel()
                    }.create()

                dialog.show()
            }

            tvName.setOnClickListener {
                val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)

                val view = LayoutInflater.from(holder.itemView.context).inflate(R.layout.edit_user_dialog, null, false)

                val nameInput = view.findViewById<EditText>(R.id.changeUserName)
                val surnameInput = view.findViewById<EditText>(R.id.changeUserSurname)
                nameInput.setText(user.name)
                surnameInput.setText(user.surname)
                val deleteUserButton = view.findViewById<ImageButton>(R.id.deleteUserButton)

                alertDialogBuilder.setView(view)

                val dialog = alertDialogBuilder
                    .setTitle("Modyfikuj użytkownika")
                    .setCancelable(false)
                    .setPositiveButton("Zmień") { _, _ ->
                        if (nameInput.text.toString().isNotBlank()) {
                            user.name = nameInput.text.toString()
                            user.surname = surnameInput.text.toString()
                            db.userDao().updateName(user.uid, user.name)
                            db.userDao().updateSurname(user.uid, user.surname)
                            notifyItemChanged(position)
                        }
                    }
                    .setNegativeButton("Wróć") { dialog, id ->
                        dialog.cancel()
                    }.create()

                deleteUserButton.setOnClickListener {
                    db.userDao().deleteUser(user)
                    getUsersFromDB()
                    notifyItemRemoved(position)
                    dialog.cancel()
                }

                dialog.show()
            }

            tvName.text = "${position + 1}. ${user.name} ${user.surname.get(0)}";
            if (user.money > 0) {
                tvAmount.text = String.format(Locale.UK, "+%.2fzł", user.money / 100f);
                tvAmount.setTextColor(Color.GREEN)
            } else if (user.money < 0) {
                tvAmount.text = String.format(Locale.UK, "-%.2fzł", user.money / 100f * -1f);
                tvAmount.setTextColor(Color.RED)
            } else {
                tvAmount.text = String.format(Locale.UK, "0.00zł");
                tvAmount.setTextColor(Color.GRAY)
            }

            bAdd.setOnClickListener {
                val amount = (toFloat(etMoney.text.toString()) * 100).toInt()
                user.money+= amount
                db.userDao().updateMoney(user.uid, user.money)
                etMoney.setText("")
                notifyItemChanged(position)
            }

            bRemove.setOnClickListener {
                val amount = (toFloat(etMoney.text.toString()) * 100).toInt()
                user.money-= amount
                db.userDao().updateMoney(user.uid, user.money)
                etMoney.setText("")
                notifyItemChanged(position)
            }
        }
    }

}