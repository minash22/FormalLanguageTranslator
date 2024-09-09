import java.io.*;

import E4.Lexer;
import E4.NumberTok;
import E4.Tag;
import E4.Token;

public class Evaluator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Evaluator(Lexer l, BufferedReader br) {
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
        int expr_val;
        expr_val = expr();
        match(Tag.EOF);
        System.out.println(expr_val);
    }

    private int expr() {
        int term_val, exprp_val;
        term_val = term();
        exprp_val = exprp(term_val);
        return exprp_val;
    }

    private int exprp(int exprp_i) {
        int term_val, exprp_val;
        switch (look.tag) {
            case '+':
                match('+');
                term_val = term();
                exprp_val = exprp(exprp_i + term_val);
                break;
            case '-':
                match('-');
                term_val = term();
                exprp_val = exprp(exprp_i - term_val);
                break;
            default:
                exprp_val = exprp_i;
                break;

        }
        return exprp_val;
    }

    private int term() {
        int termp_i, term_val;
        termp_i = fact();
        term_val = termp(termp_i);
        return term_val;
    }

    private int termp(int termp_i) {
        int termp_val, fact_val;
        switch (look.tag) {
            case '*':
                match('*');
                fact_val = fact();
                termp_val = termp(termp_i * fact_val);
                break;
            case '/':
                match('/');
                fact_val = fact();
                termp_val = termp(termp_i / fact_val);
                break;

            default:
                termp_val = termp_i;
                break;
        }
        return termp_val;
    }

    private int fact() {
        int fact_val = 0;
        // System.out.println(look.getClass());
        switch (look.tag) {

            case '(':
                match('(');
                fact_val = expr();
                match(')');
                break;
            case Tag.NUM:
                if (look instanceof NumberTok) {
                    fact_val = ((NumberTok) look).value;
                } else {
                    error("Unexpected token in factor: " + look);
                }
                match(Tag.NUM);

                break;
            default:
                error("Unexpected token in factor: " + look);
        }
        return fact_val;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "./sample.lft";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Evaluator evaluator = new Evaluator(lex, br);
            evaluator.start();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}