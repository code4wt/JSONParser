package com.titizz.jsonparser.tokenizer;

/**
 * Created by code4wt on 17/5/10.
 */
public enum TokenType {
    BEGIN_OBJECT(1),
    END_OBJECT(2),
    BEGIN_ARRAY(4),
    END_ARRAY(8),
    NULL(16),
    NUMBER(32),
    STRING(64),
    BOOLEAN(128),
    SEP_COLON(256),
    SEP_COMMA(512),
    END_DOCUMENT(1024);

    TokenType(int code) {
        this.code = code;
    }

    private int code;

    public int getTokenCode() {
        return code;
    }
}
