package tst

// schema-like validation
object Validate {

  import Tst_parser._
  import scala.util.matching.Regex

  // want to match a top-level a-node, followed by b and c nodes
  
  
  def regexp(p: Regex) = {
    object x {
      def validate(x: Tst_node) = {
        p.findPrefixOf(x.lbl) match {
          case Some(_) => true
          case _       => false
        }
      }
    }
    x
  }

  def validate_a(x: Tst_node) = {
    regexp("a[ ]*".r).validate(x) && validate_bcs(x.cs)
  }

  def validate_bcs(xs: List[Tst_node]) = {
    xs.forall { x => validate_b(x) || validate_c(x) }
  }

  def validate_b(x: Tst_node) = regexp("b[ ]*".r).validate(x)
  
  def validate_c(x: Tst_node) = regexp("c[ ]*".r).validate(x)

  // test

  def main(args: Array[String]) {
    val x = Tst_parser.parse_tst("""(a (b c)   (c c)  )""")
    println(s"Validates: ${validate_a(x)}")
    val x2 = Tst_parser.parse_tst("""(a (b c)   (d c)  )""")
    println(s"Validates: ${validate_a(x2)}")
  }

}