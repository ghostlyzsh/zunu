package me.ghostlyzsh.zunu.zunu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private final String source;
    private final String name;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords;

    // define the keywords
    static {
        keywords = new HashMap<>();
        keywords.put("and", TokenType.AND);
        keywords.put("else", TokenType.ELSE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("fn", TokenType.FN);
        keywords.put("if", TokenType.IF);
        keywords.put("or", TokenType.OR);
        keywords.put("return", TokenType.RETURN);
        keywords.put("true", TokenType.TRUE);
        keywords.put("let", TokenType.LET);
        keywords.put("while", TokenType.WHILE);
        keywords.put("break", TokenType.BREAK);
        keywords.put("continue", TokenType.CONTINUE);
        keywords.put("null", TokenType.NULL);
    }

    Scanner(String source, String name) {
        this.source = source;
        this.name = name;
    }

    List<Token> scanTokens() {
        while(!isAtEnd()) {
            // this is at the beginning of the next lexeme
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line, start));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            // all the single character tokens. it simply adds tokens when it finds characters
            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;
            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;
            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;
            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '.':
                addToken(TokenType.DOT);
                break;
            // single or two character tokens. this checks for the next token before adding a token
            case '-':
                addToken(match('=') ? TokenType.MINUS_EQUAL : TokenType.MINUS);
                break;
            case '+':
                addToken(match('=') ? TokenType.PLUS_EQUAL : TokenType.PLUS);
                break;
            // slash(/), slash equal(/=), and comments(//)
            case '/':
                if (match('/')) {
                    // go to the end of the line or file
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else if(match('*')) {
                    while((!match('*') || peek() != '/') && !isAtEnd()) {
                        if(peek() == '\n') line++;
                        advance();
                    }
                    advance();
                } else {
                    addToken(match('=') ? TokenType.SLASH_EQUAL : TokenType.SLASH);
                }
                break;
            case '*':
                addToken(match('=') ? TokenType.STAR_EQUAL : TokenType.STAR);
                break;
            case ';':
                addToken(TokenType.SEMICOLON);
                break;
            // boolean operators
            case '!':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case '|':
                if (match('|')) {
                    addToken(TokenType.OR);
                }
                break;
            case '&':
                if(match('&')) {
                    addToken(TokenType.AND);
                }
                break;
            // disregard whitespace, but on newline increment line number
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;

            case '"': string(); break;

            default:
                if(isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Zunu.error(line, start, "Unexpected character.", this.name);
                }
                break;
        }
    }

    private void identifier() {
        // go forward while the character is a-zA-Z0-9
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        // see if the type is a keyword, otherwise mark it as an identifier
        TokenType type = keywords.get(text);
        if(type == null) type = TokenType.IDENTIFIER;
        addToken(type);
    }

    private void number() {
        // advance while the character is 0-9
        while (isDigit(peek())) advance();

        // find the decimal point
        boolean isFloat = false;
        if(peek() == '.' && isDigit(peekNext())) {
            advance();

            isFloat = true;

            // continue while there's a digit
            while (isDigit(peek())) advance();
        }
        if(isFloat) {
            addToken(TokenType.FLOAT, Float.parseFloat(source.substring(start, current)));
        } else {
            addToken(TokenType.INT, Integer.parseInt(source.substring(start, current)));
        }
    }

    private boolean match(char expected) {
        if(isAtEnd()) return false;
        if(source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private void string() {
        // continue to advance while the character isn't at the end or a "
        while(peek() != '"' && !isAtEnd()) {
            if(peek() == '\n') line++;
            advance();
        }

        if(isAtEnd()) {
            Zunu.error(line, start, "Unterminated string.", this.name);
            return;
        }

        advance(); // Closing "

        // Trim surrounding quotes
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    private char peekNext() {
        // two character lookahead
        if(current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        // a-zA-Z_
        return  (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                 c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private char peek() {
        // look at the current character without advancing
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        // generate a token and add it to the token array
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line, start));
    }
}
