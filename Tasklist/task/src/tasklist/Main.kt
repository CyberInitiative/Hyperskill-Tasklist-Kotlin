package tasklist

fun main() {

    val dataFromFile = JSONSaver.deserializeTasks()
    if(dataFromFile!=null){
        TaskManager.addDataToIndexToTaskMap(dataFromFile)
    }

    while (true) {
        println("Input an action (add, print, edit, delete, end):")
        val userInput = readln()
        TaskManager.processAction(userInput)
    }

}