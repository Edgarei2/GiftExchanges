package com.taiqiwen.base_framework


fun bubbleSort(list: MutableList<Int>) {
    for (i in 0 until list.size - 1) {
        for (j in (list.size - 2) downTo i) {
            if (list[j] > list[j + 1]) {
                val tmp = list[j]
                list[j] = list[j + 1]
                list[j + 1] = tmp
            }
        }
    }
}

fun main() {
    val list = mutableListOf(3, 5, 1, 2, 5, 10, 6)
    bubbleSort(list)
    println(list)
}