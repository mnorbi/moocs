object worksheet {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(62); 
  println("Welcome to the Scala worksheet");$skip(48); val res$0 = 
  augmentString("alibaba").toLowerCase().sorted;System.out.println("""res0: String = """ + $show(res$0));$skip(102); val res$1 = 
  augmentString("llliiiiibba").toLowerCase().groupBy(x => x).mapValues(x => x.length()).toList.sorted;System.out.println("""res1: List[(Char, Int)] = """ + $show(res$1));$skip(46); val res$2 = 
  ("a"::List("b")).foldLeft("")((x,y) => x+y);System.out.println("""res2: java.lang.String = """ + $show(res$2))}
  //List("gabcdef", "abcdefg", "cdabgfe").groupBy( x => forcomp.Anagrams.wordOccurrences(x))
  //("gabcdef"::List("abcdefg")).groupBy(x => wordOccurrences(x))
}