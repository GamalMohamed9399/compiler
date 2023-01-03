package simpleinterpreter;

import java.io.IOException;
import java.util.List;

class Interpreter implements Expr.Visitor<Object> {

    void interpret(List<Expr> expressions)  {
        try {
            for (Expr expression : expressions) {
                System.out.println("Your Output is: \n" + evaluate(expression));
            }
        } catch (RuntimeError error) {
            SimpleInterpreter.runtimeError(error);
        }
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        checkNumberOperands(expr.operator, left, right);
        switch (expr.operator.type){

            case PLUS: return (double)left + (double)right; // Take this line as a guide.
            case MINUS: return (double)left - (double)right;
            case STAR: return (double)left * (double)right;
            case SLASH: return (double)left / (double)right;


        }

        return null;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        
        Object right = evaluate(expr.right);

        return -(double)right;
    }

    @Override
    public Object visitNumberExpr(Expr.Number expr) {
        return expr.value;
    }

    private void checkNumberOperands(Token operator,
                                     Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;

        throw new RuntimeError(operator, "Operands must be numbers.");
    }
}