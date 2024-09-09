package E5;
// package E5;

import java.io.*;
// import java.util.*;

public class Lexer {
    public static int line = 1;
    private char peek = ' ';

    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
            if (peek == '\n')
                line++;
            readch(br);
        }

        // Handle comments
        if (peek == '/') {
            readch(br);
            if (peek == '/') {
                // Single-line comment, skip until newline or EOF
                while (peek != '\n' && peek != (char) -1) {
                    readch(br);
                }
                // Recursive call to handle the rest of the input after the comment
                return lexical_scan(br);
            } else if (peek == '*') {
                // Multi-line comment, skip until '*/' or EOF
                readch(br);
                while (true) {
                    if (peek == '*') {
                        readch(br);
                        if (peek == '/') {
                            readch(br);
                            // Recursive call to handle the rest of the input after the comment
                            return lexical_scan(br);
                        }
                    } else if (peek == (char) -1) {
                        System.err.println("Unterminated multi-line comment");
                        return null;
                    } else {
                        readch(br);
                    }
                }
            } else {
                // Division operator
                return Token.div;
            }
        }

        switch (peek) {
            // ... Handle cases of ( ) [ ] { } + - * / ; , ... //

            case '!':
                peek = ' ';
                return Token.not;
            case '(':
                peek = ' ';
                return Token.lpt;
            case ')':
                peek = ' ';
                return Token.rpt;
            case '[':
                peek = ' ';
                return Token.lpq;
            case ']':
                peek = ' ';
                return Token.rpq;
            case '{':
                peek = ' ';
                return Token.lpg;
            case '}':
                peek = ' ';
                return Token.rpg;
            case '+':
                peek = ' ';
                return Token.plus;
            case '-':
                peek = ' ';
                return Token.minus;
            case '*':
                peek = ' ';
                return Token.mult;
            case '/':
                peek = ' ';
                return Token.div;
            case ';':
                peek = ' ';
                return Token.semicolon;
            case ',':
                peek = ' ';
                return Token.comma;
            // ... Handle cases of || < > <= >= == <> ... //
            case ':':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.init;
                } else {
                    System.err.println("Erroneous character"
                            + " after : : " + peek);
                    return null;
                }
            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : " + peek);
                    return null;
                }
            case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character"
                            + " after | : " + peek);
                    return null;
                }

            case '<':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.le;
                } else if (peek == '>') {
                    peek = ' ';
                    return Word.ne;
                } else {
                    peek = ' ';
                    return Word.lt;
                }

            case '>':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.ge;
                } else {
                    peek = ' ';
                    return Word.gt;
                }
            case '=':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.eq;
                } else {
                    System.err.println("Erroneous character"
                            + " after = : " + peek);
                    return null;
                }

            case (char) -1:
                return new Token(Tag.EOF);

            // case '_':
            // System.err.println("Identifiers cannot start with an underscore: " + peek);
            // return null;
            default:
                if (Character.isLetter(peek) || peek == '_') {
                    // Handle identifiers and keywords
                    StringBuilder lexemeBuilder = new StringBuilder();
                    do {
                        lexemeBuilder.append(peek);
                        readch(br);
                    } while (Character.isLetterOrDigit(peek) || peek == '_');

                    String lexeme = lexemeBuilder.toString().toLowerCase(); // Convert to lowercase for case-insensitive
                                                                            // comparison
                    if (lexeme.matches("^(?!_+$)[a-zA-Z_][a-zA-Z0-9_]*$")) {
                        switch (lexeme) {
                            case "assign":
                                return Word.assign;
                            case "to":
                                return Word.to;
                            case "if":
                                return Word.iftok;
                            case "else":
                                return Word.elsetok;
                            case "do":
                                return Word.dotok;
                            case "for":
                                return Word.fortok;
                            case "begin":
                                return Word.begin;
                            case "end":
                                return Word.end;
                            case "print":
                                return Word.print;
                            case "read":
                                return Word.read;
                            // case "init":
                            // return Word.init;
                            // case "or":
                            // return Word.or;
                            // case "and":
                            // return Word.and;
                            default:
                                return new Word(Tag.ID, lexeme);
                        }
                    } else {
                        System.err.println("Invalid identifier: " + lexeme);
                        return null;
                    }
                } else if (Character.isDigit(peek)) {
                    StringBuilder numBuilder = new StringBuilder();
                    do {
                        numBuilder.append(peek);
                        readch(br);
                    } while (Character.isDigit(peek));

                    int numericValue = Integer.parseInt(numBuilder.toString());
                    return new NumberTok(Tag.NUM, numericValue);
                } else {
                    System.err.println("Erroneous character: "
                            + peek);
                    return null;
                }
        }
        // return null;
    }

    // public static void main(String[] args) {
    // Lexer lex = new Lexer();
    // String path = "./sample.txt"; // the path to the file to read
    // try {
    // BufferedReader br = new BufferedReader(new FileReader(path));
    // Token tok;
    // do {
    // tok = lex.lexical_scan(br);
    // System.out.println("Scan: " + tok);
    // } while (tok.tag != Tag.EOF);
    // br.close();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
}
