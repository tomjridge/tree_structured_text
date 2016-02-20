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
      
        Some sample scala code is in the file [tst_parser.scala](tst_parser.scala).)
      
      ) (# parsing)
    
    
    (section (References)
      (Some references here.))


    (section (Tools)
      (# describe some tools that can format the text to html, latex, etc.)
      (emacs
        (A simple mode for editing .tst files is:

          (code (emacs-lisp)

(defun foo-indent-function ()
  (save-excursion
    (beginning-of-line)
    (indent-line-to (* 2 (car (syntax-ppss)))))
  (when (< (current-column) (car (syntax-ppss)))
    (back-to-indentation) ; move to end of indentation if to the left
    ))
            
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

          ;; comment to ensure code is not parsed as children         
          
            )))

      )

    )
  )
