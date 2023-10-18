package utils

fun <K, V> deepCopy(map: Map<K, V>): HashMap<K, V> {
    val copyMap = HashMap<K, V>()
    for (entry in map.entries) {
        copyMap[entry.key] = entry.value
    }
    return copyMap
}

fun <T> deepCopy(list: List<T>): MutableList<T> {
    val copyList = ArrayList<T>()
    for (entry in list) copyList.add(entry)
    return copyList
}