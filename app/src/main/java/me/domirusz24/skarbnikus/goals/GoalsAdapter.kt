package me.domirusz24.skarbnikus.goals

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import me.domirusz24.skarbnikus.AppDatabase
import me.domirusz24.skarbnikus.R
import me.domirusz24.skarbnikus.model.Task
import me.domirusz24.skarbnikus.model.UserData
import java.util.Locale

class GoalsAdapter(val db: AppDatabase) : RecyclerView.Adapter<GoalsAdapter.GoalsHolder>() {

    class GoalsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    var goals = db.taskDao().getAllTasks()
    var goalDebts = HashMap<Task, Int>()
    var users = db.userDao().getAllUsers()

    fun getGoalsFromDB() {
        goals = db.taskDao().getAllTasks()
        users = db.userDao().getAllUsers()

        goalDebts.clear()


        for (i in 0..goals.size - 1) {
            println(i)
            val goal = goals[goals.size - i - 1]

            val perUser = if (goal.split) {
                goal.amount / users.size
            } else {
                goal.amount
            }

            goalDebts.putIfAbsent(goal, 0)

            for (user in users) {
                if (user.money < 0) {
                    val amount = (user.money * -1).coerceAtMost(perUser)
                    goalDebts[goal] = goalDebts[goal]!! + amount
                    user.money += amount
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalsHolder {

        getGoalsFromDB()

        return GoalsHolder(
            LayoutInflater.from(parent.context).inflate(
            R.layout.goal_item,
            parent,
            false
        ))
    }

    override fun getItemCount(): Int {
        return goals.size
    }

    override fun onBindViewHolder(holder: GoalsHolder, position: Int) {
        val goal = goals[position]

        val isSplit = goal.split

        val goalName = holder.itemView.findViewById<TextView>(R.id.goalName)
        val goalAmount = holder.itemView.findViewById<TextView>(R.id.goalAmount)
        val goalBar = holder.itemView.findViewById<ProgressBar>(R.id.goalBar)

        val goalContainer = holder.itemView.findViewById<ConstraintLayout>(R.id.goalItemContainer)

        var toCollect = 0
        var perUser = 0

        if (isSplit) {
            toCollect = goal.amount
            perUser = goal.amount / users.size
        } else {
            toCollect = goal.amount * users.size
            perUser = goal.amount
        }

        val collected = toCollect - goalDebts[goal]!!

        goalName.text = goal.name
        goalAmount.text = String.format(Locale.UK, "%.2fzł / %.2fzł", collected.toFloat() / 100f, toCollect.toFloat() / 100f);
        goalBar.progress = ((collected.toFloat() / toCollect.toFloat()) * 100).toInt()



        goalContainer.setOnClickListener {

            val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)

            alertDialogBuilder
                .setTitle("Usuń ${goal.name}?")
                .setCancelable(false)
                .setPositiveButton("Usuń") { _, _ ->
                    db.userDao().addMoney(perUser)
                    db.taskDao().deleteTask(goal)
                    getGoalsFromDB()
                    notifyDataSetChanged()
                }
                .setNegativeButton("Wróć") { dialog, id ->
                    dialog.cancel()
                }.create().show()
        }


    }

}