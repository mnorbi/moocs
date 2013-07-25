package knapsack

class Context(input: String, val logTiming: Boolean) {
  val (Array(items, capacity), mapping, values, weights) = {
    val contentArr = input.split("\n")
    val seq = contentArr.slice(1, contentArr.size).map(x => x.split("\\s+").map(y => y.toInt))
    val sortedSeq = seq.sortWith((x: Array[Int], y: Array[Int]) => x(0).asInstanceOf[Float] / x(1) > y(0).asInstanceOf[Float] / y(1))
    ( contentArr(0).split("\\s+").map(x => x.toInt),
      sortedSeq.map(x => seq.indexOf(x)),
      sortedSeq.map(x => x(0)),
      sortedSeq.map(x => x(1)))
  }

}
