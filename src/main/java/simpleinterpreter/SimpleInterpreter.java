package simpleinterpreter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// this is file is copied from https://github.com/OsamaMaani/Interpreter-Visualizer/blob/master/InterpreterVisualizer/src/main/java/simpleinterpreter/SimpleInterpreter.java
// with some editions to fit the project
public class SimpleInterpreter {

    private boolean run = false;
    private final String source;

    public static void main(String[] args){
        System.out.println("Please enter your expression: ");
        java.util.Scanner in = new java.util.Scanner(System.in);
        String source = in.nextLine();
        SimpleInterpreter interpreter = new SimpleInterpreter(source);
    }

    public SimpleInterpreter(String source) {
        this.source = source;
        run();
    }



    public void run() {
        if (run) return;
        run = true;
        Scanner scanner = new Scanner(source, this);
        scanner.scanTokens();
        List<Token> tokens = scanner.getTokens();
        Parser parser = new Parser(tokens);
        List<Expr> expressions = new ArrayList<>();
        expressions = parser.parse();

        Interpreter interpreter = new Interpreter();
        interpreter.interpret(expressions);
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    static void report(int line, String where, String message) {
        String output = "[line " + line + "] Error" + where + ": " + message;
        System.err.println(output);
    }

    static void runtimeError(RuntimeError error) {
        String output = error.getMessage() + "\n[line " + error.token.line + "]";
        System.err.println(output);
    }
}