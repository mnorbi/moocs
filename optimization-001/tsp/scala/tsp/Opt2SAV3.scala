package tsp

import scala.util.Random
import scala.annotation.tailrec

class Opt2SAV3(val context: Context) {
  def move = {
    var ret = false
//    val v1 = Random.nextInt(context.nodeCount)
//    val v3 = Random.nextInt(context.nodeCount)
//    if (v3 != v1) {
//      val v2Idx = context.next(context.tourIdx(v1))
//      val v3Idx = context.tourIdx(v3)
//      if (v3Idx != v2Idx) {
//        if (v3Idx != context.next(v2Idx)) {
//          val v4Idx = context.prev(v3Idx)
//          val v2 = context.tour(v2Idx)
//          val v4 = context.tour(v4Idx)
//
//          val length12 = context.length(v1, v2)
//          val length43 = context.length(v4, v3)
//          val length14 = context.length(v1, v4)
//          val length23 = context.length(v2, v3)
//          
//          val length12_43 = length12 + length43
//          val length14_23 = length14 + length23
//          
//          if (length12_43 >= length14_23 || context.expCoinFlip(length12_43-length14_23)) {
//            if (Math.abs(v2Idx-v4Idx) <= context.halfNodeCount){
//            	context.flipTourSegment(v2Idx, v4Idx, length14_23-length12_43);
//            } else {
//            	context.flipTourSegment(v4Idx, v2Idx, length14_23-length12_43);
//            }
//            ret = true
//          }
//        }
//      }
//    }
    ret
  }
}