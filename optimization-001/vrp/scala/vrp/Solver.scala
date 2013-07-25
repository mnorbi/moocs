package vrp

import common.FileLoader
import common.TimedRun
import common.SimpleFileNameProvider

object Solver extends App with FileLoader with SimpleFileNameProvider with TimedRun {
  override def main(args: Array[String]) {
    val aFileName = fileName(args).get
    val context = new Context(loadContent(aFileName))
    search(context)
  }

  def search(context: Context) {
    val sa = new SA(context)
    sa.search
  }
}