import java.io.*;

import E3.Lexer;
import E3.Tag;
import E3.Token;

public class Parser3_1 {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser3_1(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF)
                move();
        } else {
            error("syntax error. Expected: " + (char) t + ", Found: " + look);
        }
    }

    public void start() {
        expr();
        match(Tag.EOF);
        System.out.println("Input OK");
    }

    private void expr() {
        term();
        exprp();
    }

    private void exprp() {
        switch (look.tag) {
            case '+':
                match('+');
                term();
                exprp();
                break;
            case '-':
                match('-');
                term();
                exprp();
                break;
            // No default case, as exprp can be epsilon
        }
    }

    private void term() {
        fact();
        termp();
    }

    private void termp() {
        switch (look.tag) {
            case '*':
                match('*');
                fact();
                termp();
                break;
            case '/':
                match('/');
                fact();
                termp();
                break;
            // No default case, as termp can be epsilon
        }
    }

    private void fact() {
        switch (look.tag) {
            case '(':
                match('(');
                expr();
                match(')');
                break;
            case Tag.NUM:
                match(Tag.NUM);
                break;
            default:
                error("syntax error");
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "/Users/minasharifi/Downloads/lftMinaP/E3/sample3_1.lft"; // the path to the file to be read
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser3_1 parser = new Parser3_1(lex, br);
            parser.start();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
