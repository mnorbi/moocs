package tsp

import scala.Array.canBuildFrom
import scala.annotation.tailrec
import scala.collection.mutable.BitSet
import scala.collection.mutable.WrappedArray

class Opt2(val context: Context) {
  /*
	- choose a vertex v1 and its edge x1 = (v1,v2)
	- choose an edge x2 = (v2,v3) with d(x2) < d(x1)
	– if none exist, restart with another 
		vertex
	– else we have a solution by removing the edge (t4,t3) and connecting (t1,t4)
  */
  val distCalc = context.distCalc

  @tailrec
  final def improve(tour: Array[Int] = context.tour, activeNodes: BitSet): Boolean = {
    if (!activeNodes.isEmpty) {
      val v1Idx = chooseVertex(tour, activeNodes)
      val v1 = tour(v1Idx)
      val v2Idx = next(v1Idx)
      val v2 = tour(v2Idx)
      activeNodes.remove(v1)
      findSmallerEdge(v1, v2, tour, activeNodes) match {
        case None => improve(tour, activeNodes)
        case Some(((v3Idx, v3), (v4Idx, v4))) => {
          activeNodes.add(v2)
          activeNodes.add(v3)
          activeNodes.add(v4)
          flipTourSegment(tour, v2Idx, v4Idx)
          true
        }
      }
    } else {
      false
    }
  }

  def findSmallerEdge(v1: Int, v2: Int, tour: Array[Int], activeNodes: BitSet): Option[((Int, Int), (Int, Int))] = {
    var min = Double.MaxValue
    val node1 = context.nodes(v1)
    val node2 = context.nodes(v2)
    val afterNode2 = tour(next(tour.indexOf(v2)))

    var edgeLength = distCalc.coordinateDistance(node1, node2)
    var ret: Option[((Int, Int), (Int, Int))] = None
    activeNodes.foreach(n => {
      if (n != v1 && n != v2 && n != afterNode2) {
        val node3 = context.nodes(n)
        val nIdx = tour.indexOf(n)
        val prevNIdx = prev(tour.indexOf(n))
        val prevN = tour(prevNIdx)
        val node4 = context.nodes(prevN)
        
        val dist1 =
          edgeLength +
            distCalc.coordinateDistance(node3, node4)

        val dist2 =
          distCalc.coordinateDistance(node2, node3) +
            distCalc.coordinateDistance(node1, node4)

        if (dist2 < min && dist2 < dist1) {
          min = dist2
          ret = Some(((nIdx, n), (prevNIdx, prevN)))
          return ret
        }
      }
    })
    ret
  }

  def next(idx: Int) = {
    (idx + 1) % context.nodeCount
  }

  def prev(idx: Int) = {
    if (idx == 0) context.nodeCount - 1
    else idx - 1
  }

  def chooseVertex(tour: Array[Int], activeNodes: BitSet): Int = {
    tour.indexOf(activeNodes.head)
  }

  def flipTourSegment(tour: Array[Int], start: Int, end: Int): Array[Int] = {
    var i1 = start
    var i2 = end
    var i = end - start
    if (i < 0) i+= context.nodeCount
    i = (i+1)/2
    
    while (i > 0 ) {
        val tmp = tour(i1)
        tour(i1) = tour(i2)
        tour(i2) = tmp
        i1 = next(i1)
        i2 = prev(i2)
        i -= 1
      }
    tour
  }

}

object TestKOpt extends App{
  val input = "5\n0 0\n0 0.5\n0 1\n1 1\n1 0"
  val kopt = new Opt2(new Context(input))
  val tour = kopt.flipTourSegment(Array.range(0, 5), 0, 4)
  assert((tour:WrappedArray[Int]) == (Array(4, 3, 2, 1, 0):WrappedArray[Int]))
  
  val tour1 = kopt.flipTourSegment(Array.range(0, 5), 1, 3)
  assert((tour1:WrappedArray[Int]) == (Array(0, 3, 2, 1, 4):WrappedArray[Int]))

  val tour2 = kopt.flipTourSegment(Array.range(0, 5), 3, 1)
  assert((tour2:WrappedArray[Int]) == (Array(4, 3, 2, 1, 0):WrappedArray[Int]))

  val tour3 = kopt.flipTourSegment(Array.range(0, 5), 1, 0)
  assert((tour3:WrappedArray[Int]) == (Array(1, 0, 4, 3, 2):WrappedArray[Int]))
  
  val tour4 = kopt.flipTourSegment(Array.range(0, 5), 2, 0)
  assert((tour4:WrappedArray[Int]) == (Array(2, 1, 0, 4, 3):WrappedArray[Int]))
  
  val tour5 = kopt.flipTourSegment(Array.range(0, 5), 4, 2)
  assert((tour5:WrappedArray[Int]) == (Array(1, 0, 4, 3, 2):WrappedArray[Int]))
  
  val tour6 = kopt.flipTourSegment(Array.range(0, 5), 4, 0)
  assert((tour6:WrappedArray[Int]) == (Array(4, 1, 2, 3, 0):WrappedArray[Int]))
}