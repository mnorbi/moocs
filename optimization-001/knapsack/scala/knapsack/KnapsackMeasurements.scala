package knapsack

import common.FileLoader
import common.SimpleFileNameProvider
import common.TimedRun
import common.ListFiles
import java.util.concurrent.TimeoutException

object SingleKnapsackMeasurements extends App with FileLoader with TimedRun with ListFiles {
  val timeout = 30

  val sorter =
    listFiles(args(0)) /*.filter(s => s.endsWith("ks_100_0"))*/ .sortWith((x: String, y: String) => if (x.length() < y.length()) true else x.compare(y) < 0).map(
      fileName => {
        println()
        println()
        println(fileName)
        val context = new Context(loadContent(fileName), true)

        try {
          timedRun(timeout)(new BestFirstBranchAndBound(context).solve)
        } catch {
          case te: TimeoutException =>
          case e: Exception => throw e
        }

//        try {
//          timedRun(timeout)(new DepthFirstBranchAndBound(context).solve)
//        } catch {
//          case _: TimeoutException =>
//          case e: Exception => throw e
//        }
        
//        try {
//          timedRun(timeout)(new BreadthFirstBranchAndBound(context).solve)
//        } catch {
//          case _: TimeoutException =>
//          case e: Exception => throw e
//        }
      })
}