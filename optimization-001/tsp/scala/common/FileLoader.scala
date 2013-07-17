package common

trait FileLoader{
  protected def loadContent(fileName:String): String = {
    try {
      val source = scala.io.Source.fromFile(fileName, "UTF-8");
      val lines = source.getLines mkString "\n"
      source.close()
      lines
    } catch {
      case t:Throwable => 
        println("Could not load file[" + fileName + "]")
        throw new RuntimeException(t)
    }
  }
}