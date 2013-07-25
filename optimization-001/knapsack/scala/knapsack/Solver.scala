package knapsack

import common.SimpleFileNameProvider
import common.ListFiles
import common.TimedRun
import common.FileLoader

object Solver extends App with FileLoader with SimpleFileNameProvider {
  val context = new Context(loadContent(fileName(args).get), false)
  new DepthFirstBranchAndBound(context).solve
}