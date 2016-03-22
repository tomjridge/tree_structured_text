package tst

object Block_parser {

  sealed abstract class Block
  case class Node(cs: List[Block]) extends Block
  case class Leaf(s: String) extends Block
  def is_Leaf(x: Block) = {
    x match {
      case (x: Leaf) => true
      case _         => false
    }
  }

  // parse something that loks like: ( ... ( ... ) ... ( ... ) ... )
  def parse_balanced(s: String): (Node, String) = {
    if (s.startsWith("(")) {
      val (cs, s_ab) = parse_string_block_list(s.substring(1));
      if (s_ab.startsWith(")")) (Node(cs), s_ab.substring(1)) else { throw new Exception("s does not end with ket: " + s) }
    } else throw new Exception("s does not start with bra: " + s)
  }

  // assume we have consumed the opening '('; parse until and including the closing ')'
  def parse_string_block_list(s: String): (List[Block], String) = {
    if (s.startsWith("(")) {
      val (n, s_ab) = parse_balanced(s)
      val (ns, s_bc) = parse_string_block_list(s_ab)
      (n :: ns, s_bc)
    } else if (s.startsWith(")")) {
      (List(), s)
    } else {
      val p = (c: Char) => { !(c == '(' || c == ')') }
      val lbl = s.takeWhile(p)
      val s_ab = s.dropWhile(p)
      val (ns, s_bc) = parse_string_block_list(s_ab)
      (Leaf(lbl) :: ns, s_bc)
    }
  }

  object pp {
    def block_to_string(b0: Block): String = {
      b0 match {
        case (x: Leaf) => x.s
        case (x: Node) => { "(" + blocks_to_string(x.cs) + ")" }
      }
    }

    def blocks_to_string(bs: List[Block]): String = {
      bs.map(block_to_string _).mkString("")
    }
  }

}