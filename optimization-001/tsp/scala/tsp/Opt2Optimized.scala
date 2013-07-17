package tsp

import scala.annotation.tailrec
import scala.collection.mutable.BitSet

class Opt2Optimized(val context: Context) {
  /*
	- choose a vertex v1 and its edge x1 = (v1,v2)
	- choose an edge x2 = (v2,v3) with d(x2) < d(x1)
	– if none exist, restart with another 
		vertex
	– else we have a solution by removing the edge (t4,t3) and connecting (t1,t4)
  */
  val distCalc = context.distCalc
  val nodeCount = context.nodeCount
  
  @tailrec
  final def improve(activeNodes: BitSet): Boolean = {
    if (!activeNodes.isEmpty) {
      val v1 = distCalc.randomSelect(activeNodes)
      val v1Idx = context.tourIdx(v1)
      val v2Idx = next(v1Idx)
      val v2 = context.tour(v2Idx)
      activeNodes.remove(v1)
      val findResp = findSmallerEdge(v1, v1Idx, v2, v2Idx);
      if (NO_IMPROVEMENT == findResp) {
        improve(activeNodes)
      } else {
        val v4Idx = findResp(0)
        val v4 = findResp(1)
        val v3Idx = findResp(2)
        val v3 = findResp(3)
        activeNodes.add(v1)
        activeNodes.add(v4)
        activeNodes.add(v3)
        activeNodes.add(v2)
        flipTourSegment(v2Idx, v4Idx)
        true
      }
    } else {
      false
    }
  }

  val NO_IMPROVEMENT = Array(-1)

  def findSmallerEdge(v1: Int, v1Idx: Int, v2: Int, v2Idx: Int): Array[Int] = {
    val node1 = context.nodes(v1)
    val node2 = context.nodes(v2)
    val afterV2 = context.tour(next(v2Idx))

    val v2neighbours = context.nn(v2)
    val edgeLength = distCalc.coordinateDistance(node1, node2)
    val iter = v2neighbours.iterator
    while (iter.hasNext) {
      val (dist23, v3) = iter.next
      if (v3 != v1 && v3 != v2 && v3 != afterV2) {
        val node3 = context.nodes(v3)
        val v3Idx = context.tourIdx(v3)
        val v4Idx = prev(v3Idx)
        val v4 = context.tour(v4Idx)
        val node4 = context.nodes(v4)
        val dist12_43 = edgeLength + distCalc.coordinateDistance(node3, node4)
        val dist14_23 = distCalc.coordinateDistance(node1, node4) + dist23
        if (dist14_23 < dist12_43) {
          return Array(v4Idx, v4, v3Idx, v3)
        } else {
          
        }
      }
    }
    NO_IMPROVEMENT
  }

  @deprecated("","")
  def findSmallerEdge(v1: Int, v1Idx: Int, v2: Int, v2Idx: Int, activeNodes: BitSet): Array[Int] = {
    val node1 = context.nodes(v1)
    val node2 = context.nodes(v2)
    val afterV2 = context.tour(next(v2Idx))

    val edgeLength = distCalc.coordinateDistance(node1, node2)
    val iter = activeNodes.iterator
    while (iter.hasNext) {
      val v3 = iter.next
      if (v3 != v1 && v3 != v2 && v3 != afterV2) {
        val node3 = context.nodes(v3)
        val v3Idx = context.tourIdx(v3)
        val v4Idx = prev(v3Idx)
        val v4 = context.tour(v4Idx)
        val node4 = context.nodes(v4)
        val dist12_43 = distCalc.coordinateDistance(node3, node4) + edgeLength
        val dist14_23 = distCalc.coordinateDistance(node2, node3) + distCalc.coordinateDistance(node1, node4)
        if (dist14_23 <= dist12_43) {
          return Array(v4Idx, v4, v3Idx, v3)
        }
      }
    }
    NO_IMPROVEMENT
  }

  @inline
  def next(idx: Int) = {
    if (idx + 1 == nodeCount) 0
    else idx + 1
  }

  @inline
  def prev(idx: Int) = {
    if (idx == 0) nodeCount - 1
    else idx - 1
  }

  def flipTourSegment(start: Int, end: Int) {
    var i1 = start
    var i2 = end
    var i = end - start
    if (i < 0) i += nodeCount
    i = (i + 1) / 2

    while (i > 0) {
      val tmp1 = context.tour(i1)
      val tmp2 = context.tour(i2)
      context.tour(i1) = tmp2
      context.tourIdx(tmp2) = i1
      context.tour(i2) = tmp1
      context.tourIdx(tmp1) = i2

      i1 = next(i1)
      i2 = prev(i2)
      i -= 1
    }
  }

}
