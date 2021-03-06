package main.java.com.jlox;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.LineReader.Option;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.DefaultParser.Bracket;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.widget.AutopairWidgets;

public class Lox {
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRunTimeError = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    public static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        if (hadError)
            System.exit(65);
        if (hadRunTimeError)
            System.exit(70);
    }

    public static void runPrompt() throws IOException {
        String prompt = "> ";
        Terminal terminal = TerminalBuilder.terminal();
        DefaultParser parser = new DefaultParser();
        parser.setEofOnUnclosedBracket(Bracket.CURLY);

        LineReader reader = LineReaderBuilder.builder().terminal(terminal).parser(parser)
                .variable(LineReader.SECONDARY_PROMPT_PATTERN, "...").variable(LineReader.INDENTATION, 2)
                .option(Option.INSERT_BRACKET, true).build();

        AutopairWidgets autopair = new AutopairWidgets(reader);
        autopair.enable();

        for (;;) {
            String line = null;
            try {
                line = reader.readLine(prompt);
            } catch (UserInterruptException e) {
                break;
            } catch (EndOfFileException e) {
                break;
            }

            run(line);
            hadError = false;
        }
    }

    public static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Statement> statements = parser.parser();
        // Expr expression = parser.parse();

        if (hadError)
            return;

        interpreter.interperet(statements);
        // System.out.println(new AstPrinter().print(expression));
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    static void RunTimeError(RunTimeError error) {
        System.err.println("[line " + error.token.line + "] " + error.getMessage());
        hadRunTimeError = true;
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ":" + message);
        hadError = true;
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }
}
