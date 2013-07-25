package common

import java.io.File

trait ListFiles{
  protected def listFiles(folder: String): Array[String] = {
    new File(folder).listFiles.map(f => folder+"/"+f.getName()).sortWith((x: String, y: String) => x.length() < y.length() || y.compare(x) < 0)
  }
}