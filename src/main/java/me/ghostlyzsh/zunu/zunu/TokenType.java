package me.ghostlyzsh.zunu.zunu;

public enum TokenType {
    // single-character tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SLASH, STAR, SEMICOLON,

    // one or two character tokens
    BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,
    PLUS_EQUAL, MINUS_EQUAL, STAR_EQUAL, SLASH_EQUAL,

    // literals
    IDENTIFIER, STRING, INT, FLOAT,

    // keywords
    AND, ELSE, FALSE, FN, FOR, IF, OR, SEND,
    RETURN, TRUE, LET, WHILE, BREAK, NULL,
    CONTINUE,

    EOF
}
