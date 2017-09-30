package schemely.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import schemely.lexer.Tokens;


public class SchemeQuoted extends SchemePsiElementBase
{
  public SchemeQuoted(ASTNode node)
  {
    super(node, "Quoted");
  }

  @Override
  public String toString()
  {
    return "SchemeQuoted";
  }

  @Override
  public int getQuotingLevel()
  {
    ASTNode child = getNode().findChildByType(Tokens.PREFIXES);
    if (child != null)
    {
      IElementType type = child.getElementType();
      if ((type == Tokens.QUOTE_MARK) || (type == Tokens.BACK_QUOTE) || type == Tokens.SYNTAX || type == Tokens.BACK_SYNTAX)
      {
        return 1;
      }
      else if (type == Tokens.COMMA || type == Tokens.COMMA_AT || type == Tokens.UNSYNTAX || type == Tokens.UNSYNTAX_AT)
      {
        return -1;
      }
    }

    // Should never happen
    return 0;
  }
}
