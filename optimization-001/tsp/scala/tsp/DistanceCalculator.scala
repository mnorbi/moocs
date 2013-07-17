package tsp

import scala.collection.mutable.BitSet
import common.Memoize1
import scala.util.Random

class DistanceCalculator(val context: Context) {

  val points = context.nodes
  
  val memoizedCoordinateDistance = new Memoize1(_coordinateDistance)

  def coordinateDistance(state: Array[Int]): Double = {
    cost(state, (a, b) => coordinateDistance(a, b))
  }

  def fastCoordinateDistance(a: (Array[Double], Array[Double])) = {
    memoizedCoordinateDistance(a)
  }

  private def _coordinateDistance(a: (Array[Double], Array[Double])) = {
    coordinateDistance(a._1, a._2)
  }

  def squaredEuclideanDistance(a: Array[Double], b: Array[Double]) = {
	  Math.pow((a(0) - b(0)),2) + Math.pow((a(1) - b(1)),2)
  }
  
  def coordinateDistance(a: Array[Double], b: Array[Double]) = {
    Math.abs(a(0) - b(0)) + Math.abs(a(1) - b(1))
  }

  def euclideanDistance(state: Array[Int]): Double = {
    cost(state, (a, b) => euclideanDistance(a, b))
  }

  def euclideanDistance(a: Array[Double], b: Array[Double]) = {
    Math.pow(Math.pow(a(0) - b(0), 2) + Math.pow(a(1) - b(1), 2), .5)
  }

  def cost(state: Array[Int], costFunc: (Array[Double], Array[Double]) => Double) = {
    var ret = costFunc(points(state.last), points(state(0)))
    for (i <- 1 until state.length) yield {
      val a = points(state(i - 1))
      val b = points(state(i))
      ret = ret + costFunc(a, b)
    }
    ret
  }

  def findNNLimited(fromIdx: Int, activeNodes: BitSet): Int = {
    val remainingNeighbours = context.nnIdxs(fromIdx).intersect(activeNodes)
    if (remainingNeighbours.size > 0) {
//      remainingNeighbours.head
      var it = context.nn(fromIdx).iterator
      while(it.hasNext){
        val ret = it.next._2
        if (remainingNeighbours.contains(ret)){
          return ret
        }
      }
      -1
    }
    else activeNodes.head
  }
  
  def randomSelect(set: BitSet) = {
//    set.head
    var i = Random.nextInt(set.size) - 1
    val it = set.iterator
    while(i > 0){
      it.next
      i-=1
    }
    it.next
  }
  
  def findNearestNeighbour(fromIdx: Int, activeNodes: BitSet): Int = {
    var min = Double.MaxValue
    var nextNode = -1
    var dist = 0.0
    val from = points(fromIdx)
    for { activeNode <- activeNodes } yield {
      dist = coordinateDistance(from, points(activeNode))
      if (dist < min) {
        nextNode = activeNode
        min = dist
      }
    }
    nextNode
  }

}
