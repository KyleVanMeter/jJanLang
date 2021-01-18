package main.java.com.jlox;

import main.java.com.jlox.Expr.Assign;
import main.java.com.jlox.Expr.Logical;
import main.java.com.jlox.Statement.Break;
import main.java.com.jlox.Statement.Visitor;
import main.java.com.jlox.Statement.While;

import java.util.List;

class Interpreter implements Expr.Visitor<Object>, Statement.Visitor<Void> {
    private Environment environment = new Environment();
    private boolean bubble = false;

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

    void executeBlock(List<Statement> stmts, Environment environment, int depth) {
        Environment previous = this.environment;

        try {
            this.environment = environment;

            for (Statement statement : stmts) {
                if (statement instanceof Statement.Break) {
                    this.bubble = true;
                    break;
                }
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void visitBlockStmt(Statement.Block stmt) {
        executeBlock(stmt.stmts, new Environment(environment), stmt.loopDepth);

        return null;
    }

    /*
     * void interperet(Expr expr) { try { Object value = evaluate(expr);
     * System.out.println(stringify(value)); } catch (RunTimeError error) {
     * Lox.RunTimeError(error); } }
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
    public Void visitIfStmt(Statement.If stmt) {
        if (verisimilitude(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }

        return null;
    }

    @Override
    public Void visitVarStmt(Statement.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);
        return value;
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

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        Object value = environment.get(expr.name);

        if (value == null)
            throw new RunTimeError(expr.name, "Cannot access uninitialized variable '" + expr.name.lexeme + "'.");
        return value;
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

    @Override
    public Object visitLogicalExpr(Logical expr) {
        Object left = evaluate(expr.left);

        if (expr.operator.type == TokenType.OR) {
            if (verisimilitude(left))
                return left;
        } else {
            if (!verisimilitude(left))
                return left;
        }

        return evaluate(expr.right);
    }

    @Override
    public Void visitWhileStmt(Statement.While stmt) {
        while (verisimilitude(evaluate(stmt.condition))) {
            if (this.bubble) {
                this.bubble = false;
                break;
            }

            execute(stmt.body);
        }

        return null;
    }

    @Override
    public Void visitBreakStmt(Break stmt) {
        return null;
    }
}
