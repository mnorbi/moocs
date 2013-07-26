package common

trait TSearch[T] {
  /**
   * search state space, returns (cost measure, solution) tupple
   */
  def search: (Double, T)
}