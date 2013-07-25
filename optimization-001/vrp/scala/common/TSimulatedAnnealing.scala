package common

import scala.util.Random

trait TSimulatedAnnealing[T] extends TSearch[T] {
  val maxIter: Long
  
  def stateInitializer: TSearch[T]
  
  def initialState: T = {
    stateInitializer.search._2
  }

  def cost(state: T): Double
  
  def costHeuristic(state : T): Double

  def nextState(state: T): T

  def annealTemperature
  
  def temperature: Double
  
  def expCoinFlip(currCost: Double, nextCost: Double): Boolean = {
    val p = Math.exp((-(nextCost - currCost))/temperature)
    val u = Random.nextDouble()
    u < p
  }
  
  def search = {
	var minState = initialState
	var minCost = costHeuristic(minState)
    var currState = minState
    var currCost = minCost
    var idx = -1
    while (idx < maxIter) {
      idx += 1
      val nextStateVal = nextState(currState)
      val nextCost = costHeuristic(nextStateVal)
      if (nextCost < currCost) {
        currState = nextStateVal
        currCost = nextCost
        if (currCost < minCost) {
        	minCost = currCost
        	minState = currState
        }
      } else if (expCoinFlip(currCost, nextCost)){
        currState = nextStateVal
        currCost = nextCost
      }
      annealTemperature
    }
	(cost(minState), minState)
  }

}