package knapsack

import common.Memoize1

class Node(val context: Context, val level: Int, val value: Int, val weight: Int, val path: List[Int]) extends Ordered[Node]{
  val bound = context match {
    case null => 1
    case _ => Node.computeBound(this)
  }
  
  override def equals(other: Any): Boolean = {
    other match {
      case that: Node =>
        canEqual(that) &&
          that.level == level &&
          that.value == value &&
          that.weight == weight
      case _ => false
    }
  }

  def canEqual(other: Any): Boolean =
    other.isInstanceOf[Node]

  override def hashCode: Int = {
    17 * (17 * (17 * 37 + value) + level) + weight
  }

  override def toString: String = {
    format("level[%s] value[%s] weight[%s] ", level, value, weight)
  }

  def compare(that:Node):Int = {
    bound.compare(that.bound)
  }

}

object Node {
  val memoizedComputeBound = Memoize1(computeBound)
  def apply(context: Context, level: Int, value: Int, weight: Int, list: List[Int]) = new Node(context, level, value, weight, list)
  def apply(context: Context, level: Int, value: Int, weight: Int) = new Node(context, level, value, weight, List.range(0, level))
  def computeBound(node: Node): Float = {
    if (node.weight >= node.context.capacity) {
      0l
    } else {
      val level = node.level + 1;
      var idx = level
      var bound = node.value.asInstanceOf[Float];
      var weight = node.weight;
      while (idx < node.context.items && weight + node.context.weights(idx) <= node.context.capacity) {
        weight += node.context.weights(idx)
        bound += node.context.values(idx)
        idx += 1
      }
      if (idx < node.context.items) {
        bound += (node.context.capacity - weight).asInstanceOf[Float] * node.context.values(idx).asInstanceOf[Float] / node.context.weights(idx).asInstanceOf[Float]
      }
      bound
    }
  }
  
  
}