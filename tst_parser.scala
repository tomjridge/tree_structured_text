package tst

object tst_parser {

  sealed abstract class Block
  case class Node(cs:List[Block]) extends Block
  case class Leaf(s:String) extends Block
  def is_Leaf(x:Block) = {
    x match {
      case (x:Leaf) => true
      case _ => false
    }
  }
  
  
  def parse_balanced(s:String) : (Node,String) = {
    if (s.startsWith("(")) {
      val (cs,s_ab) = parse_string_block_list(s.substring(1));
      if(s_ab.startsWith(")")) (Node(cs),s_ab.substring(1)) else { throw new Exception("s does not end with ket: "+s) }
    }
    else throw new Exception("s does not start with bra: "+s)
  }
  
  // assume we have consumed the opening '('; parse until and including the closing ')'
  def parse_string_block_list(s:String) : (List[Block],String) = {
    if (s.startsWith("(")) {
      val (n,s_ab) = parse_balanced(s)
      val (ns,s_bc) = parse_string_block_list(s_ab)
      (n::ns,s_bc)
    } else if (s.startsWith(")")) {
      (List(),s)
    } else {
      val p = (c:Char) => { ! (c=='(' || c==')' ) }
      val lbl = s.takeWhile(p)
      val s_ab = s.dropWhile(p)
      val (ns,s_bc) = parse_string_block_list(s_ab)
      (Leaf(lbl)::ns,s_bc)
    }
  }
  
  case class Tst_node(lbl:String,cs:List[Tst_node])
  
  def block_to_string(b0:Block) : String = {
    b0 match {
      case (x:Leaf) => x.s
      case (x:Node) => { "(" + blocks_to_string(x.cs) + ")" }
    }
  }
  
  def blocks_to_string(bs:List[Block]) : String = {
    bs.map(block_to_string _).mkString("")
  }
  
  def block_to_tst(b0:Block) : Tst_node = {
    b0 match {
      case (b0:Node) => {
        // look at children; merge into label until only node children remain
        val cs_ab = b0.cs.reverse
        val p : Block => Boolean = {
          case (x:Node) => true
          case (x:Leaf) => {
            // ws leafs are ok
            import scala.util.matching.Regex
            val ws = "[ \t\n]*".r
            if(ws.findFirstIn(x.s) == Some(x.s)) true else false
          }
        }
        val cs_bc = cs_ab.takeWhile(p).filter( (x) => !is_Leaf(x)).reverse.map(block_to_tst _)
        val cs_cd = cs_ab.dropWhile(p).reverse
        val lbl = blocks_to_string(cs_cd)
        Tst_node(lbl,cs_bc)
        
      }
      case (b0:Leaf) => { Tst_node(b0.s,List()) }
    }
  }
  
  def pprint(n0:Tst_node,indent:Integer) : String = {
  	 (" " * indent) + "(" + n0.lbl + (if (n0.cs.isEmpty) "" else "\n") + 
  	   (n0.cs.map(pprint(_,indent+2))).mkString("\n") + ")"
  	
  }
  
  def pprint(n0:Tst_node) : String = pprint(n0,0)
  
  def main(args:Array[String]) {
    parse_balanced("""(a (b c)   (d c)  )""")
  }
  
  
}
