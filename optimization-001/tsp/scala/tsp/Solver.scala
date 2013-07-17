package tsp

import scala.annotation.tailrec
import scala.collection.mutable.BitSet

import common.FileLoader
import common.SimpleFileNameProvider
import common.TimedRun

object Solver extends App with FileLoader with SimpleFileNameProvider with TimedRun {
  override def main(args: Array[String]) {
    val aFileName = fileName(args).get
    val context = new Context(loadContent(aFileName))
    search(context)
  }

  def search(context: Context) {
    val opt2sav2 = new Opt2SAV2(context)
    while(true){
      context.allIterationCount+=1      
      if (opt2sav2.move){
        if (context.allFlipCount % 10000 == 0){
          context.printStats
        }
      }
    }
  }
  
  def improve(context: Context) {
    var improve = true
    val opt2sa = new Opt2SA(context)
    var remaining = BitSet((0 until context.nodeCount): _*)
    while (improve) {
      improve = opt2sa.improve(remaining) //BitSet((0 until data.nodeCount): _*)) //remaining)
      val cost = context.distCalc.euclideanDistance(context.tour)
      val temp = opt2sa.temperature
      val swapCount = context.allFlipCount
      if (context.allFlipCount % 10000 == 0){
      	println(s"$cost 0 temp[$temp] swaps[$swapCount]")
      }
      if (cost <= context.targetCost) {
        println(s"$cost 0$temp")
        context.printTour
      }
      //      GraphVisualizer.visualize(context)
    }
  }

}