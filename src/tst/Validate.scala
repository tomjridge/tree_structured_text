package tst

// schema-like validation
object Validate {
  
  import Tst_parser._
  
  // want to match a top-level a-node, followed by b and c nodes
  
  def validate_a(x:Tst_node) = {
    val p = "a[ ]*".r
    p.findPrefixOf(x.lbl) match {
      case Some(_) => validate_bcs(x.cs)
      case _ => false
    }
  }
  
  def validate_bcs(xs:List[Tst_node]) = {
    xs.forall { x => validate_b(x) || validate_c(x) }
  }
  
  def validate_b(x:Tst_node) = {
    val p = "b[ ]*".r
    p.findPrefixOf(x.lbl) match {
      case Some(_) => true
      case _ => false
    }
  }
  
  def validate_c(x:Tst_node) = {
    val p = "c[ ]*".r
    p.findPrefixOf(x.lbl) match {
      case Some(_) => true
      case _ => false
    }
  }
  
  
  // test
  
  def main(args:Array[String]) {
    val x = Tst_parser.parse_tst("""(a (b c)   (c c)  )""")
    println(s"Validates: ${validate_a(x)}")
  }
  
}