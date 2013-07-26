package common

import java.util.concurrent.TimeoutException

trait TimedRun {
  def logTiming[F](f: => F): F = {
    val startTime = System.nanoTime
    val ret = f
    val endTime = System.nanoTime
    val seconds = (endTime - startTime).asInstanceOf[Float] / 1000000000
    println("%.4f seconds".format(seconds))
    ret
  }
  
  @throws(classOf[java.util.concurrent.TimeoutException])
  def timedRun[F](timeout: Long)(f: => F): F = {

    import java.util.concurrent.{ Callable, FutureTask, TimeUnit }

    val task = new FutureTask(new Callable[F]() {
      def call() = {
        f
      }
    })

    val t = new Thread(task);

    try {
      t.start()
      task.get(timeout, TimeUnit.SECONDS)
    } catch {
      case te: TimeoutException =>
        printf("<timeout %s>\n", timeout)
        t.interrupt()
        throw te
    }
  }
}