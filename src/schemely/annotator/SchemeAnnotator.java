package schemely.annotator;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.HashSet;
import org.jetbrains.annotations.NotNull;
import schemely.highlighter.SchemeSyntaxHighlighter;
import schemely.lexer.Tokens;
import schemely.psi.impl.SchemeLiteral;
import schemely.psi.impl.SchemeVector;
import schemely.psi.impl.list.SchemeList;
import schemely.psi.impl.symbols.SchemeIdentifier;
import schemely.psi.resolve.ResolveUtil;
import schemely.psi.util.SchemePsiUtil;

import java.util.Arrays;
import java.util.Set;


public class SchemeAnnotator implements Annotator {
    public static final
    Set<String>
            IMPLICIT_NAMES =
            new HashSet<String>(Arrays.asList("syntax",
                    "unsyntax",
                    "quasisyntax",
                    "unsyntax-splicing",
                    "quote",
                    "quasiquote",
                    "unquote",
                    "unquote-splicing",
                    "lambda",
                    "trace",
                    "case-lambda",
                    "define-record",
                    "define",
                    "define-syntax",
                    "eval",
                    "apply",
                    "printf",
                    "display",
                    "syntax-case",
                    "syntax-rules",
                    "call/cc",
                    "system",
                    "format",
                    "foreign-procedure",
                    "foreign-callable",
                    "load-shared-object",
                    "load",
                    "eof-object?",
                    "foreign-entry?",
                    "foreign-entry",
                    "list",
                    "not",
                    "and",
                    "or",
                    "exit",
                    "vector",
                    "make-vector",
                    "vector-ref",
                    "vector-set!",
                    "list-ref",
                    "make-list",
                    "remove!",
                    "remv!",
                    "remq!",
                    "remove",
                    "remv",
                    "remq",
                    "remp",
                    "memq",
                    "memv",
                    "memp",
                    "member",
                    "assq",
                    "assv",
                    "assoc",
                    "assp",
                    "list-sort",
                    "list-tail",
                    "list-head",
                    "zero?",
                    "map",
                    "for-each",
                    "for-all",
                    "filter",
                    "partition",
                    "exists",
                    "reverse",
                    "fold-left",
                    "fold-right",
                    "vector-map",
                    "vector-for-each",
                    "string-for-each",
                    "library",
                    "module",
                    "compile",
                    "compile-file",
                    "compile-library",
                    "compile-program",
                    "cons",
                    "car",
                    "cdr",
                    "set-car!",
                    "set-cdr!",
                    "length",
                    "caar",
                    "cadr",
                    "cdddr",
                    "cdar",
                    "if",
                    "else",
                    "let",
                    "let*",
                    "letrec",
                    "let-values",
                    "values",
                    "syntax-error",
                    "set!",
                    "begin",
                    "cond",
                    "and",
                    "or",
                    "case",
                    "do",
                    "eq?",
                    "eqv?",
                    "equal?",
                    "boolean?",
                    "symbol?",
                    "procedure?",
                    "bytevector?",
                    "real?",
                    "integer?",
                    "char?",
                    "string?",
                    "vector?",
                    "list?",
                    "hashtable?",
                    "machine-type",
                    "import",
                    "include",
                    "export",
                    "null?",
                    "delay",
                    "let-syntax",
                    "letrec-syntax"));

    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (ResolveUtil.getQuotingLevel(element) > 0) {
            if (element instanceof SchemeIdentifier) {
                Annotation annotation = holder.createInfoAnnotation(element, null);
                annotation.setTextAttributes(SchemeSyntaxHighlighter.QUOTED_TEXT);
            } else if (element instanceof SchemeLiteral) {
                Annotation annotation = holder.createInfoAnnotation(element, null);
                if (SchemePsiUtil.isStringLiteral(element)) {
                    annotation.setTextAttributes(SchemeSyntaxHighlighter.QUOTED_STRING);
                } else if (SchemePsiUtil.isNumberLiteral(element)) {
                    annotation.setTextAttributes(SchemeSyntaxHighlighter.QUOTED_NUMBER);
                } else {
                    annotation.setTextAttributes(SchemeSyntaxHighlighter.QUOTED_TEXT);
                }
            } else if (element instanceof SchemeList || element instanceof SchemeVector) {
                ASTNode node = element.getNode();
                Annotation annotation = holder.createInfoAnnotation(node, null);
                annotation.setTextAttributes(SchemeSyntaxHighlighter.QUOTED_TEXT);
        /*for (ASTNode child : node.getChildren(Tokens.BRACES))
        {

        }*/
            }
        } else if (element instanceof SchemeList) {
            annotateList((SchemeList) element, holder);
        }
//    else if (element instanceof SchemeIdentifier)
//    {
//      SchemeIdentifier identifier = (SchemeIdentifier) element;
//      if (identifier.resolve() == null)
//      {
//        holder.createWarningAnnotation(identifier, "Can't resolve");
//      }
//    }
    }

    private void annotateList(SchemeList list, AnnotationHolder holder) {
        SchemeIdentifier first = list.getFirstIdentifier();

//    PsiElement second = list.getSecondNonLeafElement();
//    StringBuffer buffer = new StringBuffer();
//    buffer.append(first == null ? "null" : '"' + first.getReferenceName() + '"');
//    buffer.append(" ");
//    buffer.append(second == null ? "null" : '"' + second.getText() + '"');
//    System.out.println(buffer.toString());

        if ((first != null)) {
            Annotation annotation = holder.createInfoAnnotation(first, null);
            if (IMPLICIT_NAMES.contains(first.getReferenceName())) {
                annotation.setTextAttributes(SchemeSyntaxHighlighter.KEYWORD);
            } else {
//                annotation.setTextAttributes(SchemeSyntaxHighlighter.);
            }
        }
    }
}
