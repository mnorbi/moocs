package knapsack

import scala.collection.mutable.PriorityQueue
import common.Memoize1

class BestFirstBranchAndBound(val context: Context) extends BranchAndBound {
  val queue = new PriorityQueue[Node]()
  
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

