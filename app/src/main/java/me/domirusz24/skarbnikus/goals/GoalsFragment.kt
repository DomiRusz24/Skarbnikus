package me.domirusz24.skarbnikus.goals

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
import com.google.android.material.materialswitch.MaterialSwitch
import kotlinx.coroutines.Runnable
import me.domirusz24.skarbnikus.AppDatabase
import me.domirusz24.skarbnikus.MainActivity
import me.domirusz24.skarbnikus.R
import me.domirusz24.skarbnikus.model.Task
import me.domirusz24.skarbnikus.model.TaskData
import me.domirusz24.skarbnikus.model.UserData
import me.domirusz24.skarbnikus.users.UserAdapter

class GoalsFragment : Fragment(R.layout.fragment_goals) {

    private val mainState: MainActivity.MainState by activityViewModels()

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

    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_goals, container, false)

        db = mainState.db.value!!

        val goalAdapter = GoalsAdapter(db)

        val recyclerView = view.findViewById<RecyclerView>(R.id.goalList)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = goalAdapter

        view.findViewById<FloatingActionButton>(R.id.newMoneyGoal).setOnClickListener {
            displayCreateGoalDialog(db, view, container) {
                goalAdapter.getGoalsFromDB()
                goalAdapter.notifyDataSetChanged()
            }
        }

        return view
    }

    fun displayCreateGoalDialog(db: AppDatabase, view: View, parent: ViewGroup?, onSuccess: Runnable) {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(view.context)

        val view = LayoutInflater.from(view.context).inflate(R.layout.add_goal_dialog, parent, false)

        val amountInput = view.findViewById<EditText>(R.id.goalAmountInput)
        val nameInput = view.findViewById<EditText>(R.id.goalNameInput)
        val splitGoalInput = view.findViewById<MaterialSwitch>(R.id.splitGoal)

        alertDialogBuilder.setView(view)

        alertDialogBuilder
            .setTitle("Podaj dane")
            .setCancelable(false)
            .setPositiveButton("Stwórz") { _, _ ->
                if (amountInput.text.toString().isNotBlank() && nameInput.text.toString().isNotBlank()) {
                    createGoal(
                        db,
                        nameInput.text.toString(),
                        (toFloat(amountInput.text.toString()) * 100).toInt(),
                        splitGoalInput.isChecked,
                    )
                    onSuccess.run()
                }
            }
            .setNegativeButton("Wróć") { dialog, id ->
                dialog.cancel()
            }.create().show()
    }

    fun createGoal(db: AppDatabase, name: String, amount: Int, split: Boolean) {
        db.taskDao().insertTask(TaskData(name, amount, split))

        val userCount = db.userDao().getUserCount()

        var perUser = 0

        if (split) {
            perUser = amount / userCount
        } else {
            perUser = amount
        }

        db.userDao().removeMoney(perUser)
    }
}