package tasklist

import kotlin.system.exitProcess

object TaskManager {
    private const val HORIZONTAL_DIVIDER =
        "+----+------------+-------+---+---+--------------------------------------------+"
    private const val TABLE_HEADER = "| N  |    Date    | Time  | P | D |                   Task                     |"
    private const val TITLE_FORMAT = "| %s | %s | %s | %s | %s |%s|"
    private const val NON_TITLE_FORMAT = "|    |            |       |   |   |%s|"

    private val indexToTaskMap = mutableMapOf<String, Task>()

    fun addDataToIndexToTaskMap(anotherMap: Map<String, Task>){
        if(anotherMap.isNotEmpty()) {
            for (task in anotherMap) {
                indexToTaskMap[task.key] = task.value
            }
        }
    }

    fun processAction(action: String) {
        when (action) {
            "add" -> processAddAction()
            "print" -> processPrintAction()
            "edit" -> processEditAction()
            "delete" -> processDeleteAction()
            "end" -> {
                println("Tasklist exiting!")
                JSONSaver.serializeTasks(indexToTaskMap)
                exitProcess(0)
            }

            else -> {
                println("The input action is invalid")
            }
        }
    }

    private fun addTaskPriority(task: Task): Task {
        while (true) {
            println("Input the task priority (C, H, N, L):")

            val userInput = readln().uppercase().trim()
            if (userInput.matches("[CHNL]".toRegex())) {
                task.priority = userInput
                return task
            }
        }
    }

    private fun addTaskDate(task: Task): Task {
        while (true) {
            println("Input the date (yyyy-mm-dd):")

            val userInput = readln()
            val validationResult = TimeAndDateValidator.isValidDate(userInput)
            if (validationResult != null) {
                task.date = validationResult
                return task
            } else {
                println("The input date is invalid")
            }

        }
    }

    private fun addTaskTime(task: Task): Task {
        while (true) {
            println("Input the time (hh:mm):")

            val userInput = readln()
            val validationResult = TimeAndDateValidator.isValidTime(userInput)
            if (validationResult != null) {
                task.time = validationResult
                return task
            } else {
                println("The input time is invalid")
            }
        }
    }

    private fun processAddAction() {
        val newTask = Task()
        addTaskPriority(newTask)
        addTaskDate(newTask)
        addTaskTime(newTask)
        newTask.defineDueTag()

        println("Input a new task (enter a blank line to end):")
        while (true) {
            val userInput = readln()
            if (userInput.isNotBlank() && userInput.isNotEmpty()) {
                newTask.subList.add(userInput.trim())
            } else {
                if (newTask.subList.isEmpty()) {
                    println("The task is blank")
                } else {
                    indexToTaskMap["${indexToTaskMap.size + 1}"] = newTask
                }
                break
            }
        }
    }

    private fun processPrintAction() {
        if (indexToTaskMap.isEmpty()) {
            println("No tasks have been input")
        } else {
            printTable()

        }
    }

    private fun printTableHeader() {
        println(HORIZONTAL_DIVIDER)
        println(TABLE_HEADER)
        println(HORIZONTAL_DIVIDER)
    }

    private fun replacePriorityWithColor(priority: String): String {
        when (priority.uppercase()) {
            "C" -> return "\u001B[101m \u001B[0m"
            "H" -> return "\u001B[103m \u001B[0m"
            "N" -> return "\u001B[102m \u001B[0m"
            "L" -> return "\u001B[104m \u001B[0m"
        }
        return "Error"
    }

    private fun replaceDueTagWithColor(priority: String): String {
        when (priority.uppercase()) {
            "I" -> return "\u001B[102m \u001B[0m"
            "T" -> return "\u001B[103m \u001B[0m"
            "O" -> return "\u001B[101m \u001B[0m"
        }
        return "Error"
    }

    private fun printSingleTask(index: Int, date: String, time: String, priority: String, taskSublist: List<String>, dueTag: String) {
        val indexStr: String = if (index < 10) {
            "$index "
        } else {
            "$index"
        }

        for (task in taskSublist) {
            if (task == taskSublist.first()) {
                if (task.length > 44) {
                    val chunkedTasks = task.chunked(44)
                    for (i in chunkedTasks) {
                        when (i) {
                            chunkedTasks.first() -> {
                                println(String.format(TITLE_FORMAT, indexStr, date, time, replacePriorityWithColor(priority), replaceDueTagWithColor(dueTag), i))
                            }

                            chunkedTasks.last() -> {
                                println(String.format(NON_TITLE_FORMAT, i.padEnd(44)))
                            }

                            else -> {
                                println(String.format(NON_TITLE_FORMAT, i))
                            }
                        }
                    }
                } else {
                    println(String.format(TITLE_FORMAT, indexStr, date, time, replacePriorityWithColor(priority), replaceDueTagWithColor(dueTag), task.padEnd(44)))
                }
            } else {
                if (task.length > 44) {
                    val chunkedTasks = task.chunked(44)
                    for (i in chunkedTasks) {
                        if (i == chunkedTasks.last()) {
                            println(String.format(NON_TITLE_FORMAT, i.padEnd(44)))
                        } else {
                            println(String.format(NON_TITLE_FORMAT, i))
                        }
                    }
                } else {
                    println(String.format(NON_TITLE_FORMAT, task.padEnd(44)))
                }
            }
        }
    }

    private fun printTasks() {
        for (task in indexToTaskMap) {
            val index = task.key.toInt()
            val date = task.value.date
            val time = task.value.time
            val priority = task.value.priority
            val taskSublist = task.value.subList
            val dueTag = task.value.dueTag
            printSingleTask(index, date, time, priority, taskSublist, dueTag)
            println(HORIZONTAL_DIVIDER)
        }
    }

    fun printTable() {
        printTableHeader()
        printTasks()
    }

    private fun printTaskData(taskDate: String, taskTime: String, taskPriority: String, dueTag: String, index: Int) {
        if (index < 10) {
            println("$index  $taskDate $taskTime $taskPriority $dueTag")
        } else {
            println("$index $taskDate $taskTime $taskPriority $dueTag")
        }
    }

    private fun printTaskSublist(taskString: String, index: Int) {
        if (index < 10) {
            println("   $taskString")
        } else {
            println("   $taskString")
        }
    }

    private fun processEditAction() {
        if (indexToTaskMap.isNotEmpty()) {
            processPrintAction()
            while (true) {
                println("Input the task number (1-${indexToTaskMap.size}):")
                val userInput = readln()
                if (userInput.matches("[0-9]+".toRegex()) && userInput.toInt() in 1..indexToTaskMap.size) {
                    val task = indexToTaskMap[userInput]

                    while (true) {
                        println("Input a field to edit (priority, date, time, task):")
                        val userInput = readln()
                        when (userInput) {
                            "priority" -> {
                                editPriority(task!!)
                                break
                            }

                            "date" -> {
                                editDate(task!!)
                                break
                            }

                            "time" -> {
                                editTime(task!!)
                                break
                            }

                            "task" -> {
                                editTask(task!!)
                                break
                            }

                            else -> println("Invalid field")
                        }
                    }

                    break
                } else {
                    println("Invalid task number")
                }
            }

            println("The task is changed")
        } else {
            println("No tasks have been input")
        }
    }

    private fun editPriority(task: Task) {
        addTaskPriority(task)
    }

    private fun editDate(task: Task) {
        addTaskDate(task).defineDueTag()
    }

    private fun editTime(task: Task) {
        addTaskTime(task).defineDueTag()
    }

    private fun editTask(task: Task) {
        task.subList.clear()
        println("Input a new task (enter a blank line to end):")
        while (true) {
            val userInput = readln()
            if (userInput.isNotBlank() && userInput.isNotEmpty()) {
                task.subList.add(userInput.trim())
            } else {
                if (task.subList.isEmpty()) {
                    println("The task is blank")
                } else {
                    break
                }
                break
            }
        }
    }

    private fun processDeleteAction() {
        if (indexToTaskMap.isNotEmpty()) {
            processPrintAction()
            while (true) {
                println("Input the task number (1-${indexToTaskMap.size}):")
                val userInput = readln()
                if (userInput.matches("[0-9]+".toRegex()) && userInput.toInt() in 1..indexToTaskMap.size) {
                    indexToTaskMap.remove(userInput)
                    println("The task is deleted")
                    rearrangeMap()
                    break
                } else {
                    println("Invalid task number")
                }
            }
        } else {
            println("No tasks have been input")
        }
    }

    private fun rearrangeMap() {
        val rearrangedMap = mutableMapOf<String, Task>()
        var newKeyIndex = 1
        for ((_, value) in indexToTaskMap) {
            rearrangedMap[newKeyIndex.toString()] = value
            newKeyIndex++
        }

        indexToTaskMap.clear()
        indexToTaskMap.putAll(rearrangedMap)
    }

}