
import java.io.*;

import E5.CodeGenerator;
import E5.Lexer;
import E5.NumberTok;
import E5.OpCode;
import E5.SymbolTable;
import E5.Tag;
import E5.Token;
import E5.Word;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count = 0;
    boolean read = false;
    boolean print = false;
    boolean assign = false;
    boolean add = false;
    boolean sub = false;
    boolean mul = false;
    boolean div = false;
    int add_op = 0;
    int sub_op = 0;
    int mul_op = 0;
    int div_op = 0;
    int ldc_val;

    public Translator(Lexer l, BufferedReader br) {
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
            error("syntax error. Expected: " + t + ", Found: " + look);
        }
    }

    public void prog() {

        int lnext_prog = code.newLabel();
        statlist(lnext_prog);
        // code.emitLabel(lnext_prog);
        match(Tag.EOF);
        code.emit(OpCode.GOto, lnext_prog);
        code.emitLabel(lnext_prog);

        try {
            code.toJasmin();
        } catch (java.io.IOException e) {
            System.out.println("IO error\n");
        }

    }

    private void statlist(int lnext_prog) {
        stat(lnext_prog);
        statlistp(lnext_prog);
    }

    private void statlistp(int lnext_prog) {
        switch (look.tag) {
            case ';':
                match(';');
                // lnext_prog = code.newLabel();
                // code.emit(OpCode.GOto, lnext_prog);
                // code.emitLabel(lnext_prog);
                stat(lnext_prog);
                statlistp(lnext_prog);
                break;

        }
    }

    public void stat(int lnext_prog) {
        switch (look.tag) {


            case Tag.ASSIGN:
                assign = true;
                match(Tag.ASSIGN);
                assignlist(lnext_prog);
                lnext_prog = code.newLabel();
                code.emit(OpCode.GOto, lnext_prog);
                code.emitLabel(lnext_prog);
                assign = false;
                break;

            case Tag.PRINT:
                match(Tag.PRINT);
                print = true;
                match('(');
                exprlist(lnext_prog);
                match(')');
                // code.emit(OpCode.invokestatic, 1);
                lnext_prog = code.newLabel();
                code.emit(OpCode.GOto, lnext_prog);
                code.emitLabel(lnext_prog);
                print = false;
                break;

            case Tag.READ:
                match(Tag.READ);
                read = true;
                // code.emit(OpCode.invokestatic, 0);
                match('(');
                idlist(lnext_prog);
                match(')');
                lnext_prog = code.newLabel();
                code.emit(OpCode.GOto, lnext_prog);
                code.emitLabel(lnext_prog);
                read = false;
                break;

            case Tag.FOR:
                match(Tag.FOR);
                match('(');

                int loopStart = code.newLabel();
                int loopBody = code.newLabel();
                int loopEnd = code.newLabel();

                if (look.tag == Tag.ID) {
                    String loopVar = ((Word) look).lexeme;
                    int idAddr = st.lookupAddress(loopVar);
                    if (idAddr == -1) {
                        idAddr = count;
                        st.insert(loopVar, count++);
                    }

                    match(Tag.ID);
                    match(Tag.INIT);
                    expr(lnext_prog);
                    code.emit(OpCode.istore, idAddr);
                    match(';');
                    code.emitLabel(loopStart);
                    bexpr(loopBody);
                    match(')');
                    match(Tag.DO);
                    code.emit(OpCode.GOto, loopEnd);
                    code.emitLabel(loopBody);
                    stat(lnext_prog);
                    code.emit(OpCode.GOto, loopStart);
                    code.emitLabel(loopEnd);

                } else {

                    code.emitLabel(loopStart);
                    bexpr(loopBody);
                    match(')');
                    match(Tag.DO);
                    code.emit(OpCode.GOto, loopEnd);
                    code.emitLabel(loopBody);
                    stat(lnext_prog);
                    code.emit(OpCode.GOto, loopStart);
                    code.emitLabel(loopEnd);
                }
                break;

            case Tag.IF:
                int ifBody = code.newLabel();
                int ifElse = code.newLabel();
                int ifEnd = code.newLabel();

                match(Tag.IF);
                match('(');
                bexpr(ifBody);
                match(')');
                code.emit(OpCode.GOto, ifElse);
                code.emitLabel(ifBody);
                stat(lnext_prog);
                if (look.tag == Tag.ELSE) {
                    code.emit(OpCode.GOto, ifEnd);
                    code.emitLabel(ifElse);
                    match(Tag.ELSE);
                    stat(lnext_prog);

                } else {
                    code.emitLabel(ifElse);
                    code.emit(OpCode.GOto, ifEnd);
                }

                match(Tag.END);
                // code.emit(OpCode.GOto, ifEnd);
                code.emitLabel(ifEnd);
                break;

            case '{':
                match('{');
                statlist(lnext_prog);
                match('}');

                break;

            default:
                error("Invalid Syntax in stat");
                break;

            // ... completare ...
        }

    }

    private void assignlist(int lnext_prog) {

        switch (look.tag) {
            case '[':
                match('[');
                expr(lnext_prog);
                match(Tag.TO);
                idlist(lnext_prog);
                match(']');
                assignlistp(lnext_prog);
                break;

            default:
                error("Syntax error");
                break;
        }

    }

    private void assignlistp(int lnext_prog) {
        switch (look.tag) {
            case '[':
                match('[');
                expr(lnext_prog);
                match(Tag.TO);
                idlist(lnext_prog);
                match(']');
                assignlistp(lnext_prog);
                break;

        }
    }

    private void idlist(int lnext_prog) {
        switch (look.tag) {
            case Tag.ID:
                // match(Tag.ID);
                int id_addr = st.lookupAddress(((Word) look).lexeme);
                if (id_addr == -1) {
                    id_addr = count;
                    st.insert(((Word) look).lexeme, count++);
                }
                if (read) {
                    code.emit(OpCode.invokestatic, 0);
                }
                code.emit(OpCode.istore, id_addr);
                match(Tag.ID);
                idlistp(lnext_prog);

                break;
            default:
                error("Syntax error in idlist");
                break;

        }
    }

    private void idlistp(int lnext_prog) {
        switch (look.tag) {
            case ',':
                match(',');
                // if (read) {
                // code.emit(OpCode.invokestatic, 0);
                // }
                int id_addr = st.lookupAddress(((Word) look).lexeme);
                if (id_addr == -1) {
                    id_addr = count;
                    st.insert(((Word) look).lexeme, count++);
                }
                if (assign) {
                    code.emit(OpCode.ldc, ldc_val);

                }
                if (read) {
                    code.emit(OpCode.invokestatic, 0);
                }
                code.emit(OpCode.istore, id_addr);
                match(Tag.ID);
                idlistp(lnext_prog);

                break;
        }

    }

    private void bexpr(int lnext_prog) {
        switch (look.tag) {
            case Tag.RELOP:

                String op = ((Word) look).lexeme;

                match(Tag.RELOP);
                expr(lnext_prog);
                expr(lnext_prog);

                switch (op) {
                    case "==":
                        code.emit(OpCode.if_icmpeq, lnext_prog);

                        break;
                    case "<>":
                        code.emit(OpCode.if_icmpne, lnext_prog);
                        break;
                    case "<":
                        code.emit(OpCode.if_icmplt, lnext_prog);
                        break;
                    case ">":
                        code.emit(OpCode.if_icmpgt, lnext_prog);
                        break;
                    case "<=": // <=
                        code.emit(OpCode.if_icmple, lnext_prog);
                        break;
                    case ">=": // >=
                        code.emit(OpCode.if_icmpge, lnext_prog);
                        break;
                    default:
                        error("Invalid relational operator in bexpr");

                }
                break;

            default:
                error("Syntax error at bexpr");
                break;
        }
    }

    private void expr(int lnext_prog) {

        switch (look.tag) {

            case '+':
                add = true;
                match('+');
                match('(');
                exprlist(lnext_prog);
                match(')');
                add = false;
                break;
            case '-':
                sub = true;
                match('-');
                expr(lnext_prog);
                expr(lnext_prog);
                code.emit(OpCode.isub);
                sub = false;

                break;
            case '*':
                mul = true;

                match('*');
                match('(');
                exprlist(lnext_prog);
                match(')');

                mul = false;

                break;
            case '/':
                div = true;
                match('/');
                expr(lnext_prog);
                expr(lnext_prog);
                code.emit(OpCode.idiv);
                div = false;
                break;

            case Tag.NUM:

                code.emit(OpCode.ldc, ((NumberTok) look).value);
                ldc_val = ((NumberTok) look).value;
                match(Tag.NUM);

                break;

            case Tag.ID:

                int id_addr = st.lookupAddress(((Word) look).lexeme);
                if (id_addr == -1) {
                    error("Undeclared variable: " + (((Word) look).lexeme));
                }
                code.emit(OpCode.iload, id_addr);

                match(Tag.ID);
                break;
            default:
                error("Syntax error in expr");

        }
        if (print && !add && !mul &&!sub && !div) {
            code.emit(OpCode.invokestatic, 1); // Emit print instruction after evaluating each expression
        }

    }

    private void exprlist(int lnext_prog) {

        expr(lnext_prog);

        exprlistp(lnext_prog);

    }

    private void exprlistp(int lnext_prog) {
        switch (look.tag) {
            case ',':

                match(',');

                expr(lnext_prog);
                if (add && !mul) {
                    code.emit(OpCode.iadd);

                }
                if (mul) {
                    code.emit(OpCode.imul);

                }

                exprlistp(lnext_prog);

                break;

        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "/Users/minasharifi/Desktop/MinaSharifiLFT1039105/E5/s.lft"; // the path to the file to be read
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator translator = new Translator(lex, br);
            translator.prog();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}