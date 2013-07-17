package tsp

import scala.util.Random

import common.TSearch

class RandomSolver(val data:Context) extends TSearch[Array[Int]]{
	override def search = {
	  (0.0, Random.shuffle(List.range(0, data.nodeCount)).toArray)
	}
}