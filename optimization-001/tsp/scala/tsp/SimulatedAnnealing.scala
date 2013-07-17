package tsp

import common.TSearch
import common.TSimulatedAnnealing

class SimulatedAnnealing[TTour <: Array[Int], TPoint <: Array[Double]](val points: Array[TPoint], val maxIter: Long, var temperature: Double, val stateInitializer: TSearch[TTour], val costCalculator: DistanceCalculator) extends TSimulatedAnnealing[TTour] {
  var a = 0.99

  override def annealTemperature {
    temperature *= a
    a *= a
  }

  override def costHeuristic(state: TTour) = {
    costCalculator.coordinateDistance(state)
  }

  override def cost(state: TTour) = {
    costCalculator.euclideanDistance(state)
  }

  override def nextState(state: TTour): TTour = {
    //TODO
    state
  }
}