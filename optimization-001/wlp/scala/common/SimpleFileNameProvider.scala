package common

trait SimpleFileNameProvider{
  protected def fileName(args: Array[String]): Option[String] = {
    val fileName = for {
      fileArg <- args.find(p => p.startsWith("-file="))
      fileName <- Some(fileArg.substring(6))
    } yield fileName;
    fileName;
  }
}