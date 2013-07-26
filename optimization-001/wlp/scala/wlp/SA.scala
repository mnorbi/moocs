package wlp

import scala.util.Random

class SA(context: Context) {
  var targetCost = 17765203.0
  var temperature = targetCost
  var alpha = 0.99995

//    var targetCost = 2688.0
//    var temperature = 10.0
//    var alpha = 0.99995

//    var targetCost = 2610.0
//    var temperature = 10.0
//    var alpha = 0.99995

//  var targetCost = 6121.0
//  var temperature = 20.0
//  var alpha = 0.99995

//  var targetCost = 13000.0
//  var temperature = 40.0
//  var alpha = 0.99995
  

  def overloadPenalty(overload: Double) = {
    -overload*100
  }

  def search {
    var i = 0
    while (true) {
      val moved = move
      if (moved) {
        annealTemperature
        if (context.isFeasable && context.cost <= targetCost) {
          context.printSolution
          targetCost = context.cost
        }
        if (i % 100 == 0) {
          println(context.cost + " " + context.overload + " " + temperature + " " + context.openedWarehouses.size)
          if (context.isFeasable) {
            println("=========================FEASABLE========================")
          }
        }
        i += 1
      }
    }
  }

  def move = {
    var ret = false
    val warehouse = Random.nextInt(context.warehouseCount)
    
    if (context.openedWarehouses.contains(warehouse) && context.openedWarehouses.size > 1){
      ret = closeWarehouse(warehouse)
    } else if (context.closedWarehouses.contains(warehouse)){
      ret = openWarehouse(warehouse)
    }
    ret
  }
  
  def closeWarehouse(warehouse: Int) = {
    var ret = false
    var costDelta = context.closeWarehouseCostDelta(warehouse)
    if (costDelta < 0 || expCoinFlip(costDelta)){
        context.closeWarehouse(warehouse, costDelta)
        ret = true
    }
    ret
  }

  def openWarehouse(warehouse: Int) = {
    var ret = false
    var costDelta = context.openWarehouseCostDelta(warehouse)
    if (costDelta < 0 || expCoinFlip(costDelta)){
      context.openWarehouse(warehouse, costDelta)
      ret = true
    }
    ret
  }
  
  def annealTemperature {
    temperature *= alpha
  }

  def expCoinFlip(costDelta: Double): Boolean = {
    val exponent = (-costDelta) / temperature
    val p = Math.exp(exponent)
    val u = Random.nextDouble()
    u < p
  }
}