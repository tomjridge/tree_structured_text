package tst

object Tst_parser {

  import Block_parser._

  case class Tst_node(lbl: String, cs: List[Tst_node])

  // blocks to tst
  def block_to_tst(b0: Block): Tst_node = {
    b0 match {
      case (b0: Node) => {
        // look at children; merge into label until only node children remain
        val cs_ab = b0.cs.reverse
        val p: Block => Boolean = {
          case (x: Node) => true
          case (x: Leaf) => {
            // ws leafs are ok
            import scala.util.matching.Regex
            val ws = "[ \t\n]*".r
            if (ws.findFirstIn(x.s) == Some(x.s)) true else false
          }
        }
        val cs_bc = cs_ab.takeWhile(p).filter((x) => !is_Leaf(x)).reverse.map(block_to_tst _)
        val cs_cd = cs_ab.dropWhile(p).reverse
        val lbl = Block_parser.pp.blocks_to_string(cs_cd)
        Tst_node(lbl, cs_bc)

      }
      case (b0: Leaf) => { Tst_node(b0.s, List()) }
    }
  }

  
  object pp {

    def pprint(n0: Tst_node, indent: Integer): String = {
      (" " * indent) + "(" + n0.lbl + (if (n0.cs.isEmpty) "" else "\n") +
        (n0.cs.map(pprint(_, indent + 2))).mkString("\n") + ")"

    }

    def pprint(n0: Tst_node): String = pprint(n0, 0)

  }

  object pp_md {

    import scala.util.matching.Regex

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


    def pprint(n0:Tst_node): String = {
      // search for all nodes with label md, and print children
      if (regexp("md[ ]*".r).validate(n0)) {
        // print children
        n0.cs.map { x => x.lbl }.mkString("\n")
        // don't need to apply recursively
      } else {
        // apply recursively
        n0.cs.map(pprint _).mkString("\n")
      }
    }

  }

  
  def parse_tst(s:String) : Tst_node = {
    block_to_tst(Block_parser.parse_balanced(s)._1)
  }
  
  def main(args: Array[String]) {
    parse_balanced("""(a (b c)   (d c)  )""")
  }

}
