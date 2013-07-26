package wlp

import scala.Array.canBuildFrom
import scala.util.Sorting
import scala.collection.mutable.BitSet

class Context(input: String) {
  val (warehouseCount, customerCount, warehouseCapacity, warehouseSetupCost, customerDemand, customerTravelCost) = {
    var rowPointer = 0
    val rows = input.split("\n")
    var rowItems = rows(0).split("\\s+")
    val warehouseCount = rowItems(0).toInt
    val customerCount = rowItems(1).toInt
    val warehouseCapacity = Array.ofDim[Int](warehouseCount)
    val warehouseSetupCost = Array.ofDim[Double](warehouseCount)
    val customerDemand = Array.ofDim[Int](customerCount)
    val customerTravelCost = Array.ofDim[Double](customerCount, warehouseCount)
    rowPointer += 1
    var i = 0
    while (i < warehouseCount) {
      rowItems = rows(rowPointer).split("\\s+")
      warehouseCapacity(i) = rowItems(0).toInt
      warehouseSetupCost(i) = rowItems(1).toDouble
      i += 1
      rowPointer += 1
    }

    i = 0
    while (i < customerCount) {
      customerDemand(i) = rows(rowPointer).toInt
      rowPointer += 1
      var j = 0
      for (t <- rows(rowPointer).split("\\s+").map(x => x.toDouble)) yield {
        customerTravelCost(i)(j) = t
        j += 1
      }
      i += 1
      rowPointer += 1
    }
    (warehouseCount, customerCount, warehouseCapacity, warehouseSetupCost, customerDemand, customerTravelCost)
  }

  val warehousesByTravelCost = calculateWarehousesByTravelCost
  val warehousesIdxByTravelCost = calculateWarehouseIdxByTravelCost
  val sortedWarehousesByCapacity = calculateSortedWarehousesByCapacity
  val openedWarehouses = new BitSet()
  val closedWarehouses = BitSet((0 until warehouseCount): _*)

  val warehouseCustomerAssignment = Array.fill[BitSet](warehouseCount)(new BitSet())
  val customerWarehouseAssignment = Array.fill[Int](customerCount)(-1)
  val customerPerWarehouse = Array.ofDim[Int](warehouseCount)
  val warehouseRemainingCapacity = warehouseCapacity.clone
  //  val builtWarehouses  = calculateBuiltWarehouses

  var overload = 0.0
  var cost = initAssignmentWith(calculateTopDemandCustomers, (customer: Int) => sortedWarehousesByCapacity)
  printSolution

  def calculateTopDemandCustomers = {
    val ret = Array.range(0, customerCount)
    Sorting.stableSort(ret, customerDemand(_: Int) > customerDemand(_: Int))
    ret
  }

  def calculateSortedWarehousesByCapacity = {
    val ret = Array.range(0, warehouseCount)
    Sorting.stableSort(ret, warehouseCapacity(_: Int) > warehouseCapacity(_: Int))
    ret
  }

  def calculateSortedWarehousesByLowestSetupCost = {
    val ret = Array.range(0, warehouseCount)
    Sorting.stableSort(ret, warehouseSetupCost(_: Int) < warehouseSetupCost(_: Int))
    ret
  }

  def calculateWarehousesByTravelCost = {
    var ret = Array.ofDim[Array[Int]](customerCount)
    var i = 0
    while (i < customerCount) {
      ret(i) = Array.range(0, warehouseCount)
      Sorting.stableSort(ret(i), customerTravelCost(i)(_: Int) < customerTravelCost(i)(_: Int))
      i += 1
    }
    ret
  }

  def calculateWarehouseIdxByTravelCost = {
    var ret = Array.ofDim[Array[Int]](customerCount)
    var customer = 0
    while (customer < customerCount) {
      var warehouse = 0
      ret(customer) = Array.fill[Int](warehouseCount)(0)
      while (warehouse < warehouseCount) {
        ret(customer)(warehousesByTravelCost(customer)(warehouse)) = warehouse
        warehouse += 1
      }
      customer += 1
    }
    ret
  }


  def closestWarehouse(customer: Int, warehouses: BitSet) = {
    var min = warehouseCount
    val iter = warehouses.iterator
    while (min != 0 && iter.hasNext) {
      min = Math.min(min, warehousesIdxByTravelCost(customer)(iter.next))
    }
    warehousesByTravelCost(customer)(min)
  }

  def closeWarehouseCostDelta(warehouse: Int) = {
    var costDelta = 0.0
    costDelta -= warehouseSetupCost(warehouse)
    val warehouses = openedWarehouses - warehouse
    for (customer <- warehouseCustomerAssignment(warehouse)) {
      val newWarehouse = closestWarehouse(customer, warehouses)
      costDelta += reassignCustomerCostDelta(customer, newWarehouse)
    }
    costDelta
  }

  def closeWarehouse(warehouse: Int, costDelta: Double) = {
    openedWarehouses.remove(warehouse)
    closedWarehouses.add(warehouse)
    for (customer <- warehouseCustomerAssignment(warehouse)) {
      val newWarehouse = closestWarehouse(customer, openedWarehouses)
      reassignCustomer(customer, newWarehouse, 0, overloadDelta(customer, newWarehouse))
    }
    cost += costDelta
  }

  def openWarehouse(newWarehouse: Int, costDelta: Double) = {
    openedWarehouses.add(newWarehouse)
    closedWarehouses.remove(newWarehouse)
    var customer = 0
    while(customer < customerCount){
      val reassignCost = reassignCustomerCostDelta(customer, newWarehouse)
      if (reassignCost < 0){
    	reassignCustomer(customer, newWarehouse, 0, overloadDelta(customer, newWarehouse))
      }
      customer+=1
    }
    cost += costDelta
  }
  
  def openWarehouseCostDelta(newWarehouse: Int) = {
    var customer = 0
    var costDelta = warehouseSetupCost(newWarehouse)
    while(customer < customerCount){
      val reassignCost = reassignCustomerCostDelta(customer, newWarehouse)
      if (reassignCost < 0){
    	  costDelta += reassignCost
      }
      customer+=1
    }
    costDelta
  }
  
  def initAssignment = {
    var i = 0
    while (i < customerCount) {
      var j = 0
      val customer = i
      val demand = customerDemand(customer)
      while (j < warehouseCount && !isDemandFeasable(j, demand)) {
        j += 1
      }
      assignCustomer(customer, j)
      i += 1
    }
    calculateCost
  }

  def initAssignmentWith(sortedCustomers: Array[Int], warehousesForCustomer: Int => Array[Int]) = {
    var i = 0
    while (i < customerCount) {
      var j = 0
      val customer = sortedCustomers(i)
      val demand = customerDemand(customer)
      val warehouses = warehousesForCustomer(customer)
      while (j < warehouses.length && !isDemandFeasable(warehouses(j), demand)) {
        j += 1
      }
      if (!isDemandFeasable(warehouses(j), demand)) {
        throw new RuntimeException()
      }
      assignCustomer(customer, warehouses(j))
      i += 1
    }

    calculateCost
  }

  def printSolution {
    print(calculateCost)
    print(" ")
    println("0")
    var i = 0
    while (i < customerCount) {
      print(customerWarehouseAssignment(i))
      print(" ")
      i += 1
    }
    println
  }

  def isFeasable = {
    if (overload > 0) throw new RuntimeException()
    overload == 0
  }

  @inline
  def isDemandFeasable(warehouse: Int, demand: Int) = {
    warehouseRemainingCapacity(warehouse) >= demand
  }

  def assignCustomer(customer: Int, warehouse: Int) {
    if (customerWarehouseAssignment(customer) != -1) {
      throw new RuntimeException()
    }
    addDemand(warehouse, customerDemand(customer))
    customerWarehouseAssignment(customer) = warehouse
    warehouseCustomerAssignment(warehouse).add(customer)
    openedWarehouses.add(warehouse)
    closedWarehouses.remove(warehouse)
  }

  @inline
  def reassignCustomer(customer: Int, newWarehouse: Int, costDelta: Double, overloadDelta: Double) {
    val demand = customerDemand(customer)

    val oldWarehouse = customerWarehouseAssignment(customer)
    removeDemand(oldWarehouse, demand)
    warehouseCustomerAssignment(oldWarehouse).remove(customer)

    addDemand(newWarehouse, demand)
    customerWarehouseAssignment(customer) = newWarehouse
    warehouseCustomerAssignment(newWarehouse).add(customer)
    cost += costDelta
    overload += overloadDelta
  }

  def reassignCustomerCostDelta(customer: Int, newWarehouse: Int, setupCostCalculationIncluded: Boolean = false) = {
    var ret: Double = 0.0
    val oldWarehouse = customerWarehouseAssignment(customer)
    if (setupCostCalculationIncluded) {
      if (customerPerWarehouse(oldWarehouse) == 1) {
        ret -= warehouseSetupCost(oldWarehouse)
      }
      if (customerPerWarehouse(newWarehouse) == 0) {
        ret += warehouseSetupCost(newWarehouse)
      }
    }
    ret -= customerTravelCost(customer)(oldWarehouse)
    ret += customerTravelCost(customer)(newWarehouse)
    ret
  }

  @inline
  final def addDemand(warehouse: Int, demand: Int) {
    warehouseRemainingCapacity(warehouse) += -demand
    customerPerWarehouse(warehouse) += 1
  }

  @inline
  final def removeDemand(warehouse: Int, demand: Int) {
    warehouseRemainingCapacity(warehouse) += demand
    customerPerWarehouse(warehouse) += -1
  }

  def checkCost {
    var i = 0
    var expectedCost = calculateCost
    if (expectedCost - cost > 0.000001) {
      println(s"expected:$expectedCost got:$cost")
      throw new RuntimeException()
    }
  }  
  def calculateCost = {
    val cost = setupCost + travelCost
    cost
  }

  def setupCost: Double = {
    var cost = 0.0
    var i = 0
    while (i < warehouseCount) {
      if (customerPerWarehouse(i) > 0) {
        cost += warehouseSetupCost(i)
      }
      i += 1
    }
    cost
  }

  def travelCost: Double = {
    var cost = 0.0
    var i = 0
    while (i < customerCount) {
      cost += customerTravelCost(i)(customerWarehouseAssignment(i))
      i += 1
    }
    cost
  }

  def overloadDelta(customer: Int, newWarehouse: Int) = {
    var ret = 0.0
    val currentWarehouse = customerWarehouseAssignment(customer)
    if (currentWarehouse != newWarehouse) {
      val demand = customerDemand(customer)
      if (warehouseRemainingCapacity(currentWarehouse) < 0) {
        val delta = warehouseRemainingCapacity(currentWarehouse) + demand
        if (delta <= 0) {
          ret += demand
        } else {
          ret -= warehouseRemainingCapacity(currentWarehouse)
        }
      }
      val delta = warehouseRemainingCapacity(newWarehouse) - demand
      if (delta < 0) {
        if (warehouseRemainingCapacity(newWarehouse) <= 0) {
          ret -= demand
        } else {
          ret += delta
        }
      }
    }
    ret
  }
  
  def printOpenedWarehouses {
    for( wh <- openedWarehouses){
      print(s"$wh ")
    }
    println
  }

}