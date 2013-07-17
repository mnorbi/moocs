package tsp

import scala.Array.canBuildFrom
import scala.collection.mutable.BitSet
import scala.collection.mutable.TreeSet
import scala.util.Random

class Context(input: String) {
  val storedNeighboursLimit = 20
  val targetCost = 323001
  //val targetCost = 78478868
  val alpha = 0.99999
  val initialTemperature = 1000.0
  val lowestTemperature = initialTemperature * 0.005
  val reheatTemperature = initialTemperature * 0.20
  val flipPerTemperature = 100

  var temperature = initialTemperature
  var allFlipCount = 0
  var flipPerTemperatureCount = 0
  var allIterationCount = 0
  var solution = Array.empty[Int]
  var solutionCost: Double = -1.0
  var solutionAtFlipCount = -1
  var solutionAtIterationCount = -1
  val contentArr = input.split("\n")
  val nodeCount = contentArr(0).toInt
  val nodes = parseVertices(contentArr.slice(1, contentArr.size))
  val xorLinkedList = Array.range(0, nodeCount)map( i => prev(i) ^ next(i) )
  val xorLinkedListNeighbour = Array.range(0,nodeCount).map(i => prev(i))
  val tour = Array.range(0,nodeCount)
  val tourIdx = Array.range(0,nodeCount)
      
  val distCalc = new DistanceCalculator(this)

  var cost = distCalc.euclideanDistance(Array.range(0, nodeCount))

  def parseVertices(contentArr: Array[String]) = {
    for { row <- contentArr } yield {
      row.split("\\s+").map(x => x.toDouble)
    }
  }

  def printTour {
    println(s"$cost 0")
    for (n <- tour) yield print(s"$n ")
    println
  }

  def printSolution {
    if (!solution.isEmpty) {
      println(s"==Solution stats==\nsolution cost [$solutionCost], solution flip count [$solutionAtFlipCount], solution iteration count[$solutionAtIterationCount]")
      for (n <- solution) yield print(s"$n ")
      println
    }
  }

  def printStats {
//    println(s"==Current stats==\ncurrent cost [$cost], current flip count [$allFlipCount], current iteration count[$allIterationCount]")
    println(s" $allIterationCount $allFlipCount $temperature $cost")
    printSolution
  }

  def annealTemperature = {
//    flipPerTemperatureCount += 1
//    if (flipPerTemperatureCount >= flipPerTemperature) {
      temperature = temperature * alpha
//      if (temperature <= lowestTemperature) {
//        temperature = reheatTemperature
//      }
//      flipPerTemperatureCount = 0
//    }
  }

  @inline
  def next(idx: Int) = {
    var ret = idx + 1
    if (ret == nodeCount) 0
    else ret
  }

  @inline
  def prev(idx: Int) = {
    if (idx == 0) nodeCount - 1
    else idx - 1
  }

  def flipXorTourSegment(v1Idx: Int, v2Idx: Int, v3Idx: Int, v4Idx: Int, costDelta: Double) {
    xorLinkedList(v1Idx) = xorLinkedList(v1Idx) ^ v2Idx ^ v4Idx
    xorLinkedList(v2Idx) = xorLinkedList(v2Idx) ^ v1Idx ^ v3Idx
    xorLinkedList(v3Idx) = xorLinkedList(v3Idx) ^ v4Idx ^ v2Idx
    xorLinkedList(v4Idx) = xorLinkedList(v4Idx) ^ v3Idx ^ v1Idx
    xorLinkedListNeighbour(v1Idx) = v4Idx
    xorLinkedListNeighbour(v2Idx) = v3Idx
    xorLinkedListNeighbour(v3Idx) = v2Idx
    xorLinkedListNeighbour(v4Idx) = v1Idx
    
    allFlipCount += 1
    cost += costDelta
    checkSolutionXorTour
    annealTemperature
  }
  
  def flipTourSegment(start: Int, end: Int, costDelta: Double) {
    var i1 = start
    var i2 = end
    var i = end - start
    if (i < 0) i += nodeCount
    i = (i + 1) / 2

    while (i > 0) {
      val tmp1 = tour(i1)
      val tmp2 = tour(i2)
      tour(i1) = tmp2
      tourIdx(tmp2) = i1
      tour(i2) = tmp1
      tourIdx(tmp1) = i2

      i1 = next(i1)
      i2 = prev(i2)
      i -= 1
    }
    allFlipCount += 1
    cost += costDelta
    checkSolution
    annealTemperature
  }

  def checkSolutionXorTour {
    if (cost <= targetCost){
      solution = Array.range(0, nodeCount)
      var i = 0
      var actual = 0
      var previous = xorLinkedListNeighbour(actual)
      var next = previous ^ xorLinkedList(actual)
      while(i < nodeCount){
    	solution(i) = actual
        val tmp = next
        next = actual ^ xorLinkedList(next)
        actual = next
        i+=1
      }
    }
  }
  
  def checkSolution {
    if (cost <= targetCost) {
      solution = tour.clone
      solutionCost = cost
      solutionAtFlipCount = allFlipCount
      solutionAtIterationCount = allIterationCount
      printSolution
    }
  }

  def checkCost = {
      val expectedCost = distCalc.euclideanDistance(tour)
      if (expectedCost - cost > 0.0000000001){
        println(s"error in cost calculation expected[$expectedCost] actual[$cost]")
        throw new RuntimeException()
      }
  }
  
  def expCoinFlip(costDelta: Double): Boolean = {
    val exponent = (-costDelta) / temperature
    val p = Math.exp(exponent)
    val u = Random.nextDouble()
    u < p
  }

  val (nn, nnIdxs) = (Array.empty[TreeSet[(Double, Int)]], Array.empty[BitSet]) //computeLimitedNN

  def computeLimitedNN = {
    val nn = Array.ofDim[TreeSet[(Double, Int)]](nodeCount)
    val nnIdxs = Array.ofDim[BitSet](nodeCount)
    var maxEdgeLength = Double.MinValue
    var n1 = 0
    var n2 = 0
    while (n1 < nodeCount) {
      n2 = 0
      val nodeN1 = nodes(n1)
      val n1Neighbours = new TreeSet[(Double, Int)]
      val n1NeighbourIdxs = new BitSet

      var nnMaxN1 = Double.MinValue
      while (n2 < Math.min(storedNeighboursLimit, nodeCount)) {
        if (n1 != n2) {
          val nodeN2 = nodes(n2)
          val dist = distCalc.coordinateDistance(nodeN1, nodeN2)
          if (dist > maxEdgeLength) maxEdgeLength = dist
          n1Neighbours.add((dist, n2))
          n1NeighbourIdxs.add(n2)
          if (nnMaxN1 < dist) nnMaxN1 = dist
        }
        n2 += 1
      }

      while (n2 < nodeCount) {
        if (n1 != n2) {
          val nodeN2 = nodes(n2)
          val dist = distCalc.coordinateDistance(nodeN1, nodeN2)
          if (nnMaxN1 > dist) {
            if (dist > maxEdgeLength) maxEdgeLength = dist
            val toRemove = n1Neighbours.max
            n1Neighbours.remove(toRemove)
            n1NeighbourIdxs.remove(toRemove._2)
            n1Neighbours.add((dist, n2))
            n1NeighbourIdxs.add(n2)
            nnMaxN1 = n1Neighbours.max._1
          }
        }
        n2 += 1
      }
      nn(n1) = n1Neighbours
      nnIdxs(n1) = n1NeighbourIdxs

      n1 += 1
    }

    (nn, nnIdxs)
  }

}

