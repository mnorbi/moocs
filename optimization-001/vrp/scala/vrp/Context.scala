package vrp

import scala.Array.canBuildFrom
import scala.collection.mutable.DoubleLinkedList
import scala.util.Sorting

class Context(input: String) {
  val (locationCount, vechicleCount, vechicleRemainingCapacity, demands, locations) = {
    var rowCursor = 0
    var rows = input.split("\n")
    var rowItems = rows(rowCursor).split("\\s+")
    val locationCount = rowItems(0).toInt
    val vechicleCount = rowItems(1).toInt
    val vechicleRemainingCapacity = Array.fill(vechicleCount)(rowItems(2).toInt)
    val demands = new Array[Int](locationCount)
    val locations = new Array[Array[Double]](locationCount)

    rowCursor += 1
    var i = 0
    while (i < locationCount) {
      rowItems = rows(i + rowCursor).split("\\s+")
      demands(i) = rowItems(0).toInt
      locations(i) = Array(rowItems(1).toDouble, rowItems(2).toDouble)
      i += 1
    }
    rowCursor += i

    (locationCount, vechicleCount, vechicleRemainingCapacity, demands, locations)
  }

  var cost = 0.0
  var overload = 0.0
  val distances = initDistances
  val locationRoutePoints = Array.tabulate[RoutePoint](locationCount)(location => new RoutePoint(location, -1)) //each location has to be assigned once to some vechicle
  val vechicleRoute = Array.tabulate[RoutePoint](vechicleCount)(vechicle => new RoutePoint(0, vechicle)) //end locations of each vechicle
  val routePoints = vechicleRoute ++ locationRoutePoints.slice(1, locationCount)
  val routePointCount = vechicleCount + locationCount - 1
  initRoutes

  def initDistances = {
    val distances = Array.ofDim[Double](locationCount, locationCount)
    var i = 0
    while (i < locations.length) {
      var j = i + 1
      while (j < locationCount) {
        distances(i)(j) = Math.pow(Math.pow(locations(i)(0) - locations(j)(0), 2) + Math.pow(locations(i)(1) - locations(j)(1), 2), 0.5)
        distances(j)(i) = distances(i)(j)
        j += 1
      }
      i += 1
    }
    distances
  }

  def initRoutes = {
    val sortedLocations = Array.range(1, locationCount)
    Sorting.stableSort(sortedLocations, (i: Int, j: Int) => demands(i) > demands(j))
    var i = 1
    while (i < locationCount) {
      val location = sortedLocations(i - 1)
      var j = 0
      while (j < vechicleCount && vechicleRemainingCapacity(j) < demands(location)) {
        j += 1
      }
      if (j == vechicleCount)
        throw new RuntimeException()
      appendLocation(location, j)
      i += 1
    }

    cost = calculateCost
  }

  def appendLocation(location: Int, vechicle: Int) {
    val routePoint = locationRoutePoints(location).insertAfter(vechicleRoute(vechicle).prev)
  }

  def printSolution {
    println(s"$cost 0")
    for (route <- vechicleRoute) yield {
      route.printRoute
    }
  }

  def calculateCost = {
    var i = 0
    var ret = 0.0
    while (i < vechicleCount) {
      ret += vechicleRoute(i).routeCost
      i += 1
    }
    ret
  }

  def checkCost {
    var i = 0
    var expectedCost = calculateCost
    if (expectedCost - cost > 0.0000001) {
      println(s"expected:$expectedCost got:$cost")
      throw new RuntimeException()
    }
  }

  def isFeasable = {
    if (overload > 0) throw new RuntimeException()
    overload == 0
  }

  class RoutePoint(var location: Int, var vechicle: Int) {
    var prev = this
    var next = this

    def removalCost = {
      val ret = Context.this.distances(prev.location)(next.location) +
        -Context.this.distances(prev.location)(location) +
        -Context.this.distances(location)(next.location)
      ret
    }

    def remove {
      prev.next = next
      next.prev = prev
      prev = this
      next = this
      unloadVechicle
    }

    def unloadVechicle {
      if (vechicle != -1) {
        val demand = Context.this.demands(location)
        Context.this.vechicleRemainingCapacity(vechicle) += demand
        vechicle = -1
      }
    }

    def loadVechicle(thatVechicle: Int) {
      val demand = Context.this.demands(location)
      vechicle = thatVechicle
      Context.this.vechicleRemainingCapacity(vechicle) -= demand
    }

    def overloadDeltaAfterInsert(that: RoutePoint) = {
      var ret = 0.0
      if (vechicle != that.vechicle) {
    	val demand = demands(location)
        if (vechicleRemainingCapacity(vechicle) < 0){
          val delta = vechicleRemainingCapacity(vechicle)+demand
          if (delta <= 0){
            ret += demand
          } else {
            ret -= vechicleRemainingCapacity(vechicle)
          }
        }
    	val delta = vechicleRemainingCapacity(that.vechicle) - demand
    	if (delta < 0){
    	  if (vechicleRemainingCapacity(that.vechicle) <= 0){
    	    ret -= demand
    	  } else {
    	    ret += delta
    	  }
    	}
      }
      ret
    }

    def costDeltaAfterInsert(that: RoutePoint) = {
      val ret =
        removalCost +
          -Context.this.distances(that.location)(that.next.location) +
          Context.this.distances(that.location)(location) +
          Context.this.distances(location)(that.next.location)
      ret
    }

    def insertAfter(that: RoutePoint) = {
      remove
      this.prev = that
      this.next = that.next
      that.next = this
      this.next.prev = this
      loadVechicle(that.vechicle)
      this
    }

    def printRoute {
      var route = this.next
      print("0 ")
      while (route.location != 0) {
        print(route.location)
        print(" ")
        route = route.next
      }
      print("0")
      println
    }

    def routeCost = {
      var route = this.next
      var ret = distances(0)(route.location)
      while (route.location != 0) {
        ret += distances(route.location)(route.next.location)
        route = route.next
      }
      ret
    }

    override def toString = {
      s"location:$location vechicle:$vechicle"
    }

  }
}
