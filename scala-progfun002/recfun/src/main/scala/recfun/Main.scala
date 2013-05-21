package recfun
import common._

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }
  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = {
    if(c == 0 || r == 0 || c == r) 1
    else pascal(c-1, r-1) + pascal(c,r-1)
  }

  /**
   * Exercise 2
   */
  def balance(chars: List[Char]): Boolean = {
    def signum(c: Char) = {
      if (c == '(') 1
      else if (c == ')') -1
      else 0
    }
    
    def loop(acc:Int, chars2 : List[Char]) : Boolean = {
      if (acc < 0) false
      else if (chars2.isEmpty) acc == 0
      else loop(acc+signum(chars2.head), chars2.tail)
    }
        
    loop(0,chars)
  }

  /**
   * Exercise 3
   */
  def countChange(money: Int, coins: List[Int]): Int = {
	  if (money < 0) 0
	  else if (money == 0) 1
	  else {
	    if (coins.isEmpty) 0
	    else countChange(money, coins.tail)+countChange(money-coins.head, coins)
	  }
  }
}
