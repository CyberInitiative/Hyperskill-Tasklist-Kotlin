package tasklist

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

object JSONSaver {
    private val jsonFile = File("tasklist.json")

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, Task::class.java)
    private val listAdapter = moshi.adapter<List<Task>>(listType)

    fun serializeTasks(indexToTaskMap: Map<String, Task>) {
        val tasks = indexToTaskMap.values.toList()

        val json = listAdapter.toJson(tasks)

        jsonFile.writeText(json)
    }

    fun deserializeTasks():MutableMap<String, Task>? {
        if (jsonFile.exists()) {
            val dataFromJsonFile = jsonFile.readText()
            val tasksList = listAdapter.fromJson(dataFromJsonFile)
            if(tasksList != null){
                val indexToTaskMap = tasksList.mapIndexed { index, task -> (index + 1).toString() to task }.toMap()
                return indexToTaskMap.toMutableMap()
            }
            else{
                return null
            }
        }
        return null
    }
}