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

        R visitBreakStmt(Break stmt);

        R visitContinueStmt(Continue stmt);
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
        Block(List<Statement> stmts, int loopDepth) {
            this.stmts = stmts;
            this.loopDepth = loopDepth;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }

        final List<Statement> stmts;
        public int loopDepth;
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

    static class Break extends Statement {
        Break() {
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBreakStmt(this);
        }
    }

    static class Continue extends Statement {
        Continue() {
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitContinueStmt(this);
        }
    }

    abstract <R> R accept(Visitor<R> visitor);
}
