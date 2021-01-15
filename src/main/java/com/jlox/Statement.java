package main.java.com.jlox;

import java.util.List;

abstract class Statement {
    interface Visitor<R> {
        R visitExpressionStmt(Expression stmt);

        R visitPrintStmt(Print stmt);

        R visitVarStmt(Var stmt);

        R visitBlockStmt(Block stmt);

        R visitIfStmt(If stmt);

        R visitWhileStmt(While stmt);
    }

    static class Expression extends Statement {
        Expression(Expr expr) {
            this.expression = expr;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }

        final Expr expression;
    }

    static class Print extends Statement {
        Print(Expr expr) {
            this.expression = expr;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }

        final Expr expression;
    }

    static class Var extends Statement {
        Var(Token name, Expr expr) {
            this.name = name;
            this.initializer = expr;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }

        final Token name;
        final Expr initializer;
    }

    static class Block extends Statement {
        Block(List<Statement> stmts) {
            this.stmts = stmts;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }

        final List<Statement> stmts;
    }

    static class If extends Statement {
        If(Expr condition, Statement thenBranch, Statement elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }

        final Expr condition;
        final Statement thenBranch;
        final Statement elseBranch;

    }

    static class While extends Statement {
        While(Expr condition, Statement body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }

        final Expr condition;
        final Statement body;
    }

    abstract <R> R accept(Visitor<R> visitor);
}
