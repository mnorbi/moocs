package tsp

import scala.util.Random
import scala.annotation.tailrec

class Opt2SAV2(val context: Context) {
  def move = {
    var ret = false
    val v1 = Random.nextInt(context.nodeCount)
    val v3 = Random.nextInt(context.nodeCount)
    if (v3 != v1) {
      val v1Idx = context.tourIdx(v1)
      val v2Idx = context.next(v1Idx)
      val v3Idx = context.tourIdx(v3)
      if (v3Idx != v2Idx) {
        if (v3Idx != context.next(v2Idx)) {
          val v4Idx = context.prev(v3Idx)
          
          val node1 = context.nodes(v1)
          val node2 = context.nodes(context.tour(v2Idx))
          val node4 = context.nodes(context.tour(v4Idx))
          val node3 = context.nodes(v3)
          
          val dist12_x = Math.abs(node1(0) - node2(0))
          val dist12_y = Math.abs(node1(1) - node2(1))
          val dist43_x = Math.abs(node4(0) - node3(0))
          val dist43_y = Math.abs(node4(1) - node3(1))
          val dist14_x = Math.abs(node1(0) - node4(0))
          val dist14_y = Math.abs(node1(1) - node4(1))
          val dist23_x = Math.abs(node2(0) - node3(0))
          val dist23_y = Math.abs(node2(1) - node3(1))
          

          val sum_dist_12_43 = dist12_x + dist12_y + dist43_x + dist43_y
          val sum_dist_14_23 = dist14_x + dist14_y + dist23_x + dist23_y

          if (sum_dist_12_43 >= sum_dist_14_23 || context.expCoinFlip(sum_dist_14_23-sum_dist_12_43)) {
            val length_12_43 = Math.sqrt(dist12_x*dist12_x + dist12_y*dist12_y) + Math.sqrt(dist43_x*dist43_x + dist43_y*dist43_y)
            val length_14_23 = Math.sqrt(dist14_x*dist14_x + dist14_y*dist14_y) + Math.sqrt(dist23_x*dist23_x + dist23_y*dist23_y)
            context.flipXorTourSegment(v1Idx, v2Idx, v3Idx, v4Idx, length_14_23-length_12_43);
//            if (Math.abs(v2Idx-v4Idx) <= context.halfNodeCount){
//            	context.flipTourSegment(v2Idx, v4Idx, length_14_23-length_12_43);
//            } else {
//            	context.flipTourSegment(v4Idx, v2Idx, length_14_23-length_12_43);
//            }
            ret = true
          }
        }
      }
    }
    ret
  }
}