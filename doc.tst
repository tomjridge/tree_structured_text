(
  (meta 
    (title Tree-structured text, RFC)
    (author Tom Ridge)
    (date 2016-02-18))

  (body
    (section (Introduction)
      
      (# structure: informal intro; formal grammar)
      
      (This document describes the tree-structured text format. This is a plain-text format that is
        human-readable and also capable of being manipulated easily by machines.
        
        Tree-structured text (TST) is similar to XML, HTML and S-expressions. It also has
        similarities with JSON and YAML. The primary aim of TST is to represent a tree
        structure. The structure is encoded using "normal" brackets. A tree is initiated using the
        open bracket '(', and closed using the closing bracket ')'.
        
        The structure of the text between the brackets conforms to the following grammar:)
      
      (verbatim
        T -> '(' lbl (list(T,ws)) ')' )

      (where a 'lbl' consists of text. The text can contain brackets. Thus, the tree looks
        something like: )

      (verbatim
        (XXX (YYY) ZZZ (AAA) (BBB) (CCC)))
      
      (Here, the node label is 'XXX (YYY) ZZZ', and the child nodes are 'AAA' etc. We allow
        whitespace between the subtree nodes 'AAA' etc., but any text 'ZZZ' after a possible child
        node 'YYY' indicates that the child nodes have not commenced, and that the text '(YYY) ZZZ'
        is part of the node label.

        Usually this does not cause problems, because brackets in normal text are typically followed
        by a full stop (like this, for example). So there is no ambiguity. Moreover, text normally
        contains balanced brackets, although text which contains a single bracket will obviously not
        work so well - if possible, include a comment containing the matching bracket or something
        similar.

        As a minor optimization, the top-level node in a document does not have to start and end
        with a bracket. )

      )(# end Introduction)

    
    (section (Parsing)
      
      (Clearly we need to keep track of opening and closing brackets. Parsing is slightly
        complicated by the fact that the occurrence of (bracketed text) inside the label may
        correspond to a child, or not if followed by more non-whitespace text. Thus, children can only
        really be recognized after consuming all the input corresponding to the parent (up to the
          parent's closing bracket).

        To parse a node, we parse an opening bracket, and then continue until we find another (opening
          or closing) bracket. If closing, we are done. Otherwise, we start parsing a potential node. If
        that node is followed by anything other than whitespace (possibly followed by another node)
        then the potential node was actually text, and the text should be added to the label of the
        code.

        Pseudo-code: At each point, we need to track the stack of nodes that have already been
        parsed. Thus, the state is a tree, with a top-level node, and descendants. At a given point,
        we may be parsing the label, or parsing a potential child. 

        )



      
      (Some sample scala code is as follows:)
      (code (scala)
        

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
        
        ) (# code)
      
      

      
      ) (# parsing)
    
    
    (section (References)
      (Some references here.))


    (section (Tools)
      (# describe some tools that can format the text to html, latex, etc.)
      (emacs
        (A simple mode for editing .tst files is:

(require 'generic-x) ;; we need this

(define-generic-mode 
    'tst-mode                         ;; name of the mode to create
  '() 
  '()                     ;; some keywords
  '()     ;; is a built-in 
  '("\\.tst$")                      ;; files for which to activate this mode 
  (list
   (lambda ()
     (set (make-local-variable 'indent-line-function) #'foo-indent-function)
     (outline-minor-mode)
     (set (make-local-variable 'outline-regexp) "[ \t]*[(]")
     (set-fill-column 100)
   ))
  "A mode for tst files"            ;; doc string for this mode
  )

          ;;          
          
          ))

      )

    )
  )
  