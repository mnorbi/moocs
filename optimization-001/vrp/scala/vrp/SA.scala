package vrp

import scala.util.Random

class SA(context: Context) {
  var targetCost = 1310.0
  var initialTemperature = 30*targetCost
  var temperature = initialTemperature
  var lowestTemperature = 0.0000015*initialTemperature
  var reheatTemperature = .5*initialTemperature
  var alpha = 0.9999999
  var reheatCount = 0
  def overloadPenalty(overload: Double)= {
    val ret = -overload*0.8
    ret
  }

  def search {
    var i = 0
    while (true) {
      val moved = move
      if (moved) {
    	annealTemperature        
        if (context.isFeasable && context.cost <= targetCost) {
          context.printSolution
          context.checkCost
          targetCost = context.cost
        }
        if (i % 100000 == 0) {
          println(context.cost + " " + context.overload + " " + temperature)
          if (context.isFeasable){
            println("=========================FEASABLE========================")
          }
        }
        i += 1
      }
    }
  }

  def move = {
    var ret = false
    val routePointA = context.routePoints(Random.nextInt(context.routePointCount))
    val routePointB = context.locationRoutePoints(Random.nextInt(context.locationCount - 1) + 1)
    if (routePointA != routePointB && routePointB.prev != routePointA) {
      var costDelta = routePointB.costDeltaAfterInsert(routePointA)
      var overloadDelta = routePointB.overloadDeltaAfterInsert(routePointA)
      val overloadAdjustedCostDelta = costDelta + overloadPenalty(overloadDelta)
      if (overloadAdjustedCostDelta < 0 || expCoinFlip(overloadAdjustedCostDelta)){
        routePointB.insertAfter(routePointA)
        context.cost += costDelta
        context.overload += overloadDelta
        ret = true
      }      
    }
    ret
  }

  def annealTemperature {
    temperature *= alpha
    if (temperature < lowestTemperature){
      temperature = reheatTemperature
      reheatCount += 1
    }
  }

  def expCoinFlip(costDelta: Double): Boolean = {
    val exponent = (-costDelta) / temperature
    val p = Math.exp(exponent)
    val u = Random.nextDouble()
    u < p
  }
}