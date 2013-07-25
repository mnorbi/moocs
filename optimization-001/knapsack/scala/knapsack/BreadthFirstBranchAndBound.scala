package knapsack

import scala.collection.mutable.Stack
import common.Memoize1
import scala.collection.mutable.Queue

class BreadthFirstBranchAndBound(val context: Context) extends BranchAndBound {
  val queue = new Queue[Node]()

  def hasNextNode: Boolean = {
    !queue.isEmpty
  }
  
  def getNextNode: Node = {
    queue.dequeue
  }
 
  def addNode(node: Node) {
    queue.enqueue(node)
  }
  
  def addNode(node1: Node, node2: Node) {
    queue.enqueue(node1);
    queue.enqueue(node2);
  }

}

