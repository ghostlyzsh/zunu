package me.ghostlyzsh.zunu.zunu;

public class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;
    final int start;

    Token(TokenType type, String lexeme, Object literal, int line, int start) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.start = start;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
