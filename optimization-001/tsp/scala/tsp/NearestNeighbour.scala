package tsp

import scala.annotation.tailrec
import scala.collection.mutable.BitSet
import scala.util.Random

import common.TSearch

class NearestNeighbour(val context: Context) extends TSearch[Array[Int]] {
  val distCalc = context.distCalc
  override def search = {
    val node = Random.nextInt(context.nodeCount)
    context.tour(0) = node
    extendPathLimited(0, BitSet((0 until context.nodeCount): _*) - node)
    (0.0, Array.empty)
  }

  @tailrec
  private def extendPathLimited(counter: Int, remaining: BitSet) {
    if (!remaining.isEmpty) {
      val nodeId = context.tour(counter)
      
      val nextNode = distCalc.findNNLimited(nodeId, remaining)
      context.tour(counter + 1) = nextNode
      context.tourIdx(nextNode) = counter + 1
      extendPathLimited(counter + 1, remaining - nextNode)
    }
  }

  @tailrec
  private def extendPath(counter: Int, remaining: BitSet) {
    if (!remaining.isEmpty) {
      val nextNode = distCalc.findNearestNeighbour(context.tour(counter), remaining)
      context.tour(counter + 1) = nextNode
      context.tourIdx(nextNode) = counter + 1
      extendPath(counter + 1, remaining - nextNode)
    }
  }

}