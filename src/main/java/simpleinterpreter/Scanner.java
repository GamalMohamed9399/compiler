package simpleinterpreter;

import java.util.ArrayList;
import java.util.List;

import static simpleinterpreter.TokenType.*;

class Scanner {

    private final String source;

    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;


    SimpleInterpreter simpleInterpreter;

    Scanner(String source, SimpleInterpreter simpleInterpreter) {
        this.source = source;
        this.simpleInterpreter = simpleInterpreter;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    void scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line, current, current));
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case '*':
                addToken(STAR);
                break;
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
                break;

            case ' ':
            case '\r':
            case '\t':
                break;

            case '\n':
                line++;
                break;

            default:
                if (isDigit(c)) {
                    number();
                } else {
                    SimpleInterpreter.error(line, "Unexpected character.");
                }
                break;
        }
    }

    private void number() {
        while (isDigit(peek())) advance();
        if (peek() == '.' && isDigit(peekNext())) {

            advance();

            while (isDigit(peek())) advance();
        }
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }


    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }


    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }


    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }


    private boolean isAtEnd() {
        return current >= source.length();
    }


    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line, start, current - 1));
    }
}