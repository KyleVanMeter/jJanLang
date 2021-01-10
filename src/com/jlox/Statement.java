package com.jlox;

import java.util.List;

abstract class Statement {
    interface Visitor<R> {
        R visitExpressionStmt(Expression stmt);

        R visitPrintStmt(Print stmt);

        R visitVarStmt(Var stmt);

        R visitBlockStmt(Block stmt);
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

    abstract <R> R accept(Visitor<R> visitor);
}
