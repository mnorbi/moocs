package common.collection.mutable


class BitSetExt extends scala.collection.mutable.BitSet {
  private val WordLength = 64
  
  def firstEmpty[B]: Int = {
    for (i <- 0 until nwords) {
      val w = word(i)
      for (j <- i * WordLength until (i + 1) * WordLength) {
        if ((w & (1L << j)) == 0L) {
          return j
        }
      }
    }
    max+1
  }
}