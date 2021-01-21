package main.java.com.jlox;

import java.util.List;

interface LoxCallable {
    int arity;
    Object call(Interpreter interpreter, List<Object> args);
}
