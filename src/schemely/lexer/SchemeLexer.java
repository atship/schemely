package schemely.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import dk.brics.automaton.RunAutomaton;

/**
 * @author Colin Fleming
 */
public class SchemeLexer extends LexerBase {
    private static final RunAutomaton NUMBER = SchemeNumber.number();

    private CharSequence buffer;
    private int start;
    private int end;
    protected int cursor;
    private int bufferEnd;
    protected IElementType type;

    @Override
    public void start(CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.start = startOffset;
        this.end = startOffset;
        this.bufferEnd = endOffset;
        decodeState(initialState);
        advance();
    }

    @Override
    public void advance() {
        if (start >= bufferEnd) {
            type = null;
            return;
        }
        if (end >= bufferEnd) {
            start = end;
            type = null;
            return;
        }
        start = end;
        cursor = start;

        char next = peek();
        if (Character.isWhitespace(next)) {
            readWhitespace();
        } else if (test('(', Tokens.LEFT_PAREN) ||
                test(')', Tokens.RIGHT_PAREN) ||
                test('[', Tokens.LEFT_SQUARE) ||
                test(']', Tokens.RIGHT_SQUARE) ||
                test("#t", Tokens.BOOLEAN_LITERAL) ||
                test("#f", Tokens.BOOLEAN_LITERAL) ||
                test("#'", Tokens.SYNTAX) ||
                test("#`", Tokens.BACK_SYNTAX) ||
                test("#,@", Tokens.UNSYNTAX_AT)||
                test("#,", Tokens.UNSYNTAX) ||
                test("'", Tokens.QUOTE_MARK) ||
                test("`", Tokens.BACK_QUOTE) ||
                test(",@", Tokens.COMMA_AT) ||
                test(",", Tokens.COMMA) ||
                test("...", Tokens.IDENTIFIER) ||
                test('{', Tokens.LEFT_CURLY) ||
                test('}', Tokens.RIGHT_CURLY) ||
                test("#(", Tokens.OPEN_VECTOR) ||
                test("#\\newline", Tokens.CHAR_LITERAL) ||
                test("#\\space", Tokens.CHAR_LITERAL) ||
                test("#\\tab", Tokens.CHAR_LITERAL) ||
                test("#\\vtab", Tokens.CHAR_LITERAL) ||
                test("#\\backspace", Tokens.CHAR_LITERAL) ||
                test("#\\return", Tokens.CHAR_LITERAL) ||
                test("#\\alarm", Tokens.CHAR_LITERAL) ||
                test("#\\delete", Tokens.CHAR_LITERAL) ||
                test("#\\page", Tokens.CHAR_LITERAL) ||
                test("#\\esc", Tokens.CHAR_LITERAL) ||
                test("#!eof", Tokens.SPECIAL)) {
            // done
        } /*else if (next == '\'') {
            readSingleChar(Tokens.QUOTE_MARK);
        } else if (next == '`') {
            readSingleChar(Tokens.BACK_QUOTE);
        } else if (next == ',') {
            cursor++;
            if (peek() == '@') {
                cursor++;
                type = Tokens.COMMA_AT;
            } else {
                type = Tokens.COMMA;
            }
            end = cursor;
        }*/ else if (next == ';') {
            readSingleLineComment();
        } else if (lookingAt("#|") && supportsLongComments()) {
            readMultiLineComment();
        }
        // TODO sexp comments
        else if (next == '"') {
            readString();
        } else if (isIdentifierInitial(next)) {
            readIdentifier();
        } else if (next == '-') {
            if (Character.isDigit(peek(1))) {
                readNumber();
            } else {
                readSingleChar(Tokens.IDENTIFIER);
            }
        } else if (next == '+') {
            if (Character.isDigit(peek(1))) {
                readNumber();
            } else {
                readSingleChar(Tokens.IDENTIFIER);
            }
        } else if (next == '.') {
            if (Character.isDigit(peek(1))) {
                readNumber();
            } else {
                readSingleChar(Tokens.DOT);
            }
        } else if (Character.isDigit(next)) {
            readNumber();
        } else if (next == '#') {
            // TODO whole lexer should be case insensitive
            if (in(peek(1), "iexobdIEXOBD")) {
                readNumber();
            } else {
                cursor++;
                next = peek();
                if (next == '\\') {
                    cursor++;
                    readSingleChar(Tokens.CHAR_LITERAL);
                } else if (next == '!') {
                    if (Character.isWhitespace(peek(1)) || (peek(1) == '/')) {
                        readSingleLineComment();
                    } else {
                        bad();
                    }
                } /*else if (next == '\'') {
                    cursor++;
                    readSingleChar(Tokens.SYNTAX);
                } else if (next == '`') {
                    cursor++;
                    readSingleChar(Tokens.BACK_SYNTAX);
                } else if (next == ',') {
                    if (peek(1) == '@') {
                        cursor++;
                        readSingleChar(Tokens.UNSYNTAX_AT);
                    } else {
                        cursor++;
                        readSingleChar(Tokens.UNSYNTAX);
                    }
                } */ else {
                    bad();
                }
            }
        } else {
            bad();
        }

        end = cursor;
        assert end >= start;
    }

    protected boolean implementationSpecific() {
        return false;
    }

    protected boolean test(char ch, IElementType type) {
        if (peek() == ch) {
            this.type = type;
            cursor++;
            return true;
        }
        return false;
    }

    protected boolean test(String str, IElementType type) {
        if (lookingAt(str)) {
            this.type = type;
            cursor += str.length();
            return true;
        }
        return false;
    }

    private void readNumber() {
        int length = NUMBER.run(buffer.toString(), cursor);
        if (length >= 0) {
            cursor += length;
            this.type = Tokens.NUMBER_LITERAL;
        } else {
            bad();
        }
    }

    protected void readIdentifier() {
        cursor++;
        while (isIdentifierSubsequent(peek())) {
            cursor++;
        }
        this.type = Tokens.IDENTIFIER;
    }

    protected boolean isIdentifierInitial(char ch) {
        return Character.isLetter(ch) || in(ch, "!$%&*/:<=>?~_^@");
    }

    protected boolean isIdentifierSubsequent(char ch) {
        return isIdentifierInitial(ch) || Character.isDigit(ch) || in(ch, ".+-");
    }

    private void readSingleChar(IElementType type) {
        this.type = type;
        cursor++;
    }

    private void readWhitespace() {
        type = Tokens.WHITESPACE;
        while (Character.isWhitespace(peek())) {
            cursor++;
        }
    }

    private void readSingleLineComment() {
        char next;
        cursor++;
        next = peek();
        while (more() && (next != '\r') && (next != '\n')) {
            cursor++;
            next = peek();
        }
        type = Tokens.COMMENT;
    }

    private void readMultiLineComment() {
        cursor += 2;
        int depth = 1;

        while (has(2) && (depth > 0)) {
            if (lookingAt("#|")) {
                depth++;
                cursor += 2;
            } else if (lookingAt("|#")) {
                depth--;
                cursor += 2;
            } else {
                cursor++;
            }
        }
        type = Tokens.BLOCK_COMMENT;
    }

    private void readString() {
        cursor++;
        while (more() && peek() != '"') {
            if ((peek() == '\\') && has(2)) {
                cursor += 2;
            } else {
                cursor++;
            }
        }
        if (peek() == '"') {
            cursor++;
        }
        type = Tokens.STRING_LITERAL;
    }

    protected boolean supportsLongComments() {
        return true;
    }

    protected boolean lookingAt(String str) {
        return has(str.length()) && buffer.subSequence(cursor, cursor + str.length()).toString().equalsIgnoreCase(str);
    }

    private boolean in(char ch, String options) {
        return options.indexOf(ch) >= 0;
    }

    protected char peek() {
        if (more()) {
            return buffer.charAt(cursor);
        }
        return 0;
    }

    protected char peek(int offset) {
        if (has(offset + 1)) {
            return buffer.charAt(cursor + offset);
        }
        return 0;
    }

    private boolean more() {
        return cursor < bufferEnd;
    }

    protected boolean has(int n) {
        return (cursor + n) <= bufferEnd;
    }

    protected void bad() {
        cursor++;
        this.type = Tokens.BAD_CHARACTER;
    }

    @Override
    public int getState() {
        return encodeState();
    }

    @Override
    public IElementType getTokenType() {
        return type;
    }

    @Override
    public int getTokenStart() {
        return start;
    }

    @Override
    public int getTokenEnd() {
        return end;
    }

    @Override
    public CharSequence getBufferSequence() {
        return buffer;
    }

    @Override
    public int getBufferEnd() {
        return bufferEnd;
    }

    private void decodeState(int state) {
    }

    private int encodeState() {
        return 0;
    }
}
