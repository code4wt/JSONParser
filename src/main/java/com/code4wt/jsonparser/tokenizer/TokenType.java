package com.code4wt.jsonparser.tokenizer;

/**
 * Created by code4wt on 17/5/10.
 */
public enum TokenType {
    BEGIN_OBJECT, END_OBJECT,
    BEGIN_ARRAY, END_ARRAY,
    NULL, NUMBER, STRING, BOOLEAN,
    SEP_COLON, SEP_COMMA,
    END_DOCUMENT
}
