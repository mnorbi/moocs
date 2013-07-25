package knapsack

import scala.collection.mutable.Stack

import common.Memoize1

class DepthFirstBranchAndBound(val context: Context) extends BranchAndBound {
  val stack = new Stack[Node]()

  def hasNextNode: Boolean = {
    !stack.isEmpty
  }
  
  def getNextNode: Node = {
    stack.pop
  }
 
  def addNode(node: Node) {
    stack.push(node)
  }
  
  def addNode(node1: Node, node2: Node) {
    stack.push(node2);
    stack.push(node1);
  }

}

