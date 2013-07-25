package knapsack

import scala.collection.immutable.StringLike

trait BranchAndBound extends SolverTrait{
  val context: Context
  val name = this.getClass().getSimpleName()
  def solve: (Int, Array[Int]) = {
    if (context.logTiming){
    	println("%s".format(name))
    }
    val now = System.nanoTime
    
    var maxValue = 0
    var path = List[Int]()

    val v = Node(null, context.weights.findIndexOf(w => w <= context.capacity)-1, 0, 0)

    addNode(v)
    while (hasNextNode) {
      val parent = getNextNode;
      val level = parent.level + 1
      if (parent.bound > maxValue) {
        val child1 = Node(context, level, parent.value + context.values(level), parent.weight + context.weights(level), 1 :: parent.path)

        if (child1.weight <= context.capacity
          && child1.value > maxValue) {
          maxValue = child1.value
          path = child1.path
        }
        val child2 = Node(context, level, parent.value, parent.weight, 0 :: parent.path) //skipped item

        if (child1.bound > maxValue && child2.bound > maxValue){
          addNode(child1, child2)
        }else if (child1.bound > maxValue) {
          addNode(child1)
        } else if (child2.bound > maxValue) {
          addNode(child2)
        }
      }
    }

    val ret = (maxValue, remap(path))
    if (context.logTiming){
    	val seconds = (System.nanoTime - now).asInstanceOf[Float] / 1000000000
    	println("%.4f seconds".format(seconds))
    }
    printSolution(ret)
    ret
  }

  def printSolution(solution: (Int, Array[Int])){
	  println(solution._1+" 1")
	  solution._2.foreach(x => print(x + " "))
	  println()
  }
  
  def remap(path: List[Int]): Array[Int] = {
    var mappedPath = new Array[Int](context.items)
    
    var x = path.size
    while (x > 0) {
      mappedPath(context.mapping(path.size-x)) = path(x-1)
      x -= 1
    }
    mappedPath
  }

  def hasNextNode: Boolean

  def getNextNode: Node

  def addNode(node: Node)

  def addNode(node1: Node, node2: Node)

}