package simpleinterpreter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static simpleinterpreter.TokenType.*;

class Parser {

    private static class ParseError extends RuntimeException {

    }

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Expr> parse() {
        List<Expr> expressions = new ArrayList<>();
        while (!isAtEnd()) {
            expressions.add(program());
        }

        return expressions;
    }

    private Expr program() {
        try {
            return expression();
        }
        catch (ParseError | IOException error) {
            synchronize();
            return null;
        }
    }

    private Expr expression() throws IOException {
        Expr expr = factor();

        while (match(PLUS, MINUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() throws IOException {
        Expr expr = unary();

        while (match(STAR, SLASH)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() throws IOException {


        if (match(MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return number();
    }

    private Expr number() throws IOException {
        if (match(NUMBER)) {
            return new Expr.Number(previous().literal);
        }
        throw error(peek(), "Expect number.");
    }


    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()){
            current++;
        }
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) throws IOException {
        SimpleInterpreter.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            advance();
        }
    }

}