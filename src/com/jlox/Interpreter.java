package com.jlox;

import com.jlox.Statement.Visitor;
import java.util.List;

class Interpreter implements Expr.Visitor<Object>, Statement.Visitor<Void> {

    void interperet(List<Statement> stmts) {
        try {
            for (Statement stmt : stmts) {
                execute(stmt);
            }
        } catch (RunTimeError error) {
            Lox.RunTimeError(error);
        }
    }

    private void execute(Statement stmt) {
        stmt.accept(this);
    }

    /*
    void interperet(Expr expr) {
        try {
            Object value = evaluate(expr);
            System.out.println(stringify(value));
        } catch (RunTimeError error) {
            Lox.RunTimeError(error);
        }
    }
    */

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public Void visitExpressionStmt(Statement.Expression stmt) {
        evaluate(stmt.expression);

        return null;
    }

    @Override
    public Void visitPrintStmt(Statement.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));

        return null;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG:
                return !verisimilitude(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double) right;
        }

        return null;
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double)
            return;
        throw new RunTimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double)
            return;
        throw new RunTimeError(operator, "Operands must be a number.");
    }

    private boolean verisimilitude(Object object) {
        if (object == null)
            return false;
        if (object instanceof Boolean)
            return (boolean) object;

        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null)
            return true;
        if (a == null)
            return false;

        return a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null)
            return "nil";
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }

            return text;
        }

        return object.toString();
    }

    @Override
    public Object visitTernaryExpr(Expr.Ternary expr) {
        Object left = evaluate(expr.left);

        if (left instanceof Boolean) {
            if (verisimilitude(left)) {
                return evaluate(expr.mid);
            }

            return evaluate(expr.right);
        }

        throw new RunTimeError(expr.operator, " The left expression in ternary operator must return a boolean value.");
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left - (double) right;
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left <= (double) right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }

                throw new RunTimeError(expr.operator, "Operands must both be numbers or strings.");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double) left / (double) right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double) left * (double) right;
        }

        return null;
    }
}
