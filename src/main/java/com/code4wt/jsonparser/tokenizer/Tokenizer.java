package com.code4wt.jsonparser.tokenizer;

import com.code4wt.jsonparser.exception.JsonParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.code4wt.jsonparser.tokenizer.TokenType.*;

/**
 * Created by code4wt on 17/5/10.
 */
public class Tokenizer {

    private CharReader charReader;

    private List<Token> tokens = new ArrayList<Token>();

    public void tokenize() throws IOException {
        // 使用do-while处理空文件
        Token token;
        do {
            token = parse();
            tokens.add(token);
        } while (token.getTokenType() != END_DOCUMENT);
    }

    private Token parse() throws IOException {
        char ch;
        for(;;) {
            if (!charReader.hasMore()) {
                return new Token(END_DOCUMENT, null);
            }

            ch = charReader.next();
            if (!isWhiteSpace(ch)) {
                break;
            }
        }

        switch (ch) {
            case '{':
                return new Token(BEGIN_OBJECT, String.valueOf(ch));
            case '}':
                return new Token(END_OBJECT, String.valueOf(ch));
            case '[':
                return new Token(BEGIN_ARRAY, String.valueOf(ch));
            case ']':
                return new Token(END_ARRAY, String.valueOf(ch));
            case ',':
                return new Token(SEP_COMMA, String.valueOf(ch));
            case ':':
                return new Token(SEP_COLON, String.valueOf(ch));
            case 'n':
                return readNull();
            case 't':
            case 'f':
                return readBoolean();
            case '"':
                return readString();
            case '-':
                return readNumber();
        }

        if (ch >= '0' && ch <= '9') {
            return readNumber();
        }

        throw new JsonParseException("Illegal character");
    }

    private boolean isWhiteSpace(char ch) {
        return (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n');
    }

    private Token readString() {
        return null;
    }

    private Token readNumber() {
        return null;
    }

    private Token readBoolean() {
        return null;
    }

    private Token readNull() {
        return null;
    }
}
