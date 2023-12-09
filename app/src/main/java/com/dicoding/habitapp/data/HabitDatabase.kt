package com.dicoding.habitapp.data

import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dicoding.habitapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.Executors

//TODO 3 : DONE Define room database class and prepopulate database using JSON APAKAH INI DAH

@Database(entities = [Habit::class], version = 1, exportSchema = false)
abstract class HabitDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao

    companion object {

        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getInstance(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habit_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Executors.newSingleThreadExecutor().execute {
                                fillWithStartingData(context)
                            }
                        }
                    })
                    .build()

                INSTANCE = instance
                instance
            }
        }

        private fun fillWithStartingData(context: Context) {
            val habitDao = INSTANCE?.habitDao()
            habitDao?.let { dao ->
                try {
                    val jsonArray = loadJsonArray(context)
                    jsonArray?.let {
                        for (i in 0 until jsonArray.length()) {
                            val item = jsonArray.getJSONObject(i)
                            val habit = Habit(
                                item.getInt("id"),
                                item.getString("title"),
                                item.getLong("minutesFocus"), // Use consistent key
                                item.getString("startTime"),
                                item.getString("priorityLevel")
                            )
                            dao.insertAll(habit)
                        }
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
        }

        private fun loadJsonArray(context: Context): JSONArray? {
            val builder = StringBuilder()
            val `in` = context.resources.openRawResource(R.raw.habit)
            val reader = BufferedReader(InputStreamReader(`in`))
            var line: String?
            try {
                while (reader.readLine().also { line = it } != null) {
                    builder.append(line)
                }
                val json = JSONObject(builder.toString())
                return json.getJSONArray("habits")
            } catch (exception: IOException) {
                exception.printStackTrace()
            } catch (exception: JSONException) {
                exception.printStackTrace()
            } finally {
                `in`.close() // Close the InputStream when done
            }
            return null
        }
    }
}

