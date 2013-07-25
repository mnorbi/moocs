package common

trait FileLoader extends App{
  protected def loadContent(fileName:String): String = {
    try {
      val source = scala.io.Source.fromFile(fileName, "UTF-8");
      val lines = source.getLines mkString "\n"
      source.close()
      lines
    } catch {
      case _ => 
        println("Could not load file[" + fileName + "]")
        throw new RuntimeException()
    }
  }
}