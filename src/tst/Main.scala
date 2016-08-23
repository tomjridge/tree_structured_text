package tst

object Main {
  
  def main(args:Array[String]) {
    // first arg is the file to parse and pretty-print
    val fn = args(0)
    val s = scala.io.Source.fromFile(fn).mkString
    val x = Tst_parser.parse_tst(s)
    Tst_parser.pp.pprint(x)
  }
  
}