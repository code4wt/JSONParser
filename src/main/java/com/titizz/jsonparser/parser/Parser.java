package com.titizz.jsonparser.parser;

import com.titizz.jsonparser.exception.JsonParseException;
import com.titizz.jsonparser.model.JsonArray;
import com.titizz.jsonparser.model.JsonObject;
import com.titizz.jsonparser.tokenizer.Token;
import com.titizz.jsonparser.tokenizer.TokenList;
import com.titizz.jsonparser.tokenizer.TokenType;

/**
 * Created by code4wt on 17/5/19.
 */
public class Parser {

    private static final int BEGIN_OBJECT_TOKEN = 1;
    private static final int END_OBJECT_TOKEN = 2;
    private static final int BEGIN_ARRAY_TOKEN = 4;
    private static final int END_ARRAY_TOKEN = 8;
    private static final int NULL_TOKEN = 16;
    private static final int NUMBER_TOKEN = 32;
    private static final int STRING_TOKEN = 64;
    private static final int BOOLEAN_TOKEN = 128;
    private static final int SEP_COLON_TOKEN = 256;
    private static final int SEP_COMMA_TOKEN = 512;
    private static final int END_DOCUMENT_TOKEN = 1024;

    private TokenList tokens;

    public Object parse(TokenList tokens) {
        this.tokens = tokens;
        return parse();
    }

    private Object parse() {
        Token token = tokens.next();
        if (token == null) {
            return new JsonObject();
        } else if (token.getTokenType() == TokenType.BEGIN_OBJECT) {
            return parseJsonObject();
        } else if (token.getTokenType() == TokenType.BEGIN_ARRAY) {
            return parseJsonArray();
        } else {
            throw new JsonParseException("Parse error, invalid Token.");
        }
    }

    private JsonObject parseJsonObject() {
        JsonObject jsonObject = new JsonObject();
        int expectToken = STRING_TOKEN | BEGIN_OBJECT_TOKEN | END_OBJECT_TOKEN;
        String key = null;
        Object value = null;
        while (tokens.hasMore()) {
            Token token = tokens.next();
            TokenType tokenType = token.getTokenType();
            String tokenValue = token.getValue();
            switch (tokenType) {
            case BEGIN_OBJECT:
                if ((tokenType.getTokenCode() & expectToken) == 0) {
                    throw new JsonParseException("Parse error, invalid Token.");
                }
                jsonObject.put(key, parseJsonObject());    // 递归解析json object
                expectToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN | END_DOCUMENT_TOKEN;
                break;
            case END_OBJECT:
                if ((tokenType.getTokenCode() & expectToken) == 0) {
                    throw new JsonParseException("Parse error, invalid Token.");
                }
                return jsonObject;
            case BEGIN_ARRAY:
                if ((tokenType.getTokenCode() & expectToken) == 0) {
                    throw new JsonParseException("Parse error, invalid Token.");
                }
                jsonObject.put(key, parseJsonArray());
                expectToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                break;
            case NULL:
                if ((tokenType.getTokenCode() & expectToken) == 0) {
                    throw new JsonParseException("Parse error, invalid Token.");
                }
                jsonObject.put(key, null);
                expectToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN | END_ARRAY_TOKEN;
                break;
            case NUMBER:
                if ((tokenType.getTokenCode() & expectToken) == 0) {
                    throw new JsonParseException("Parse error, invalid Token.");
                }
                if (tokenValue.contains(".") || tokenValue.contains("e") || tokenValue.contains("E")) {
                    jsonObject.put(key, Double.valueOf(tokenValue));
                } else {
                    Long num = Long.valueOf(tokenValue);
                    if (num > Integer.MAX_VALUE) {
                        jsonObject.put(key, num);
                    } else {
                        jsonObject.put(key, num.intValue());
                    }
                }
                expectToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN | END_ARRAY_TOKEN;
                break;
            case BOOLEAN:
                if ((tokenType.getTokenCode() & expectToken) == 0) {
                    throw new JsonParseException("Parse error, invalid Token.");
                }
                jsonObject.put(key, Boolean.valueOf(token.getValue()));
                expectToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN | END_ARRAY_TOKEN;
                break;
            case STRING:
                if ((tokenType.getTokenCode() & expectToken) == 0) {
                    throw new JsonParseException("Parse error, invalid Token.");
                }
                Token preToken = tokens.peekPrevious();
                if (preToken.getTokenType() == TokenType.SEP_COLON) {
                    value = token.getValue();
                    jsonObject.put(key, value);
                    expectToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN | END_ARRAY_TOKEN;
                } else {
                    key = token.getValue();
                    expectToken = SEP_COLON_TOKEN | END_OBJECT_TOKEN | END_ARRAY_TOKEN;
                }
                break;
            case SEP_COLON:
                if ((tokenType.getTokenCode() & expectToken) == 0) {
                    throw new JsonParseException("Parse error, invalid Token.");
                }
                expectToken = NULL_TOKEN | NUMBER_TOKEN | BOOLEAN_TOKEN | STRING_TOKEN
                        | BEGIN_OBJECT_TOKEN | BEGIN_ARRAY_TOKEN;
                break;
            case SEP_COMMA:
                if ((tokenType.getTokenCode() & expectToken) == 0) {
                    throw new JsonParseException("Parse error, invalid Token.");
                }
                expectToken = STRING_TOKEN;
                break;
            case END_DOCUMENT:
                return jsonObject;
            }
        }

        throw new JsonParseException("Parse error, invalid Token.");
    }

    private JsonArray parseJsonArray() {
        int expectToken = BEGIN_ARRAY_TOKEN | END_ARRAY_TOKEN | BEGIN_OBJECT_TOKEN | NULL_TOKEN
                | NUMBER_TOKEN | BOOLEAN_TOKEN | STRING_TOKEN;
        JsonArray jsonArray = new JsonArray();
        while (tokens.hasMore()) {
            Token token = tokens.next();
            TokenType tokenType = token.getTokenType();
            String tokenValue = token.getValue();
            switch (tokenType) {
                case BEGIN_OBJECT:
                    if ((tokenType.getTokenCode() & expectToken) == 0) {
                        throw new JsonParseException("Parse error, invalid Token.");
                    }
                    jsonArray.add(parseJsonObject());
                    expectToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN | END_ARRAY_TOKEN;
                    break;
                case BEGIN_ARRAY:
                    if ((tokenType.getTokenCode() & expectToken) == 0) {
                        throw new JsonParseException("Parse error, invalid Token.");
                    }
                    jsonArray.add(parseJsonArray());
                    expectToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN | END_ARRAY_TOKEN | END_DOCUMENT_TOKEN;
                    break;
                case END_ARRAY:
                    if ((tokenType.getTokenCode() & expectToken) == 0) {
                        throw new JsonParseException("Parse error, invalid Token.");
                    }
                    return jsonArray;
                case NULL:
                    if ((tokenType.getTokenCode() & expectToken) == 0) {
                        throw new JsonParseException("Parse error, invalid Token.");
                    }
                    jsonArray.add(null);
                    expectToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN | END_ARRAY_TOKEN;
                    break;
                case NUMBER:
                    if ((tokenType.getTokenCode() & expectToken) == 0) {
                        throw new JsonParseException("Parse error, invalid Token.");
                    }
                    if (tokenValue.contains(".") || tokenValue.contains("e") || tokenValue.contains("E")) {
                        jsonArray.add(Double.valueOf(tokenValue));
                    } else {
                        jsonArray.add(Integer.valueOf(tokenValue));
                    }
                    expectToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN | END_ARRAY_TOKEN;
                    break;
                case BOOLEAN:
                    if ((tokenType.getTokenCode() & expectToken) == 0) {
                        throw new JsonParseException("Parse error, invalid Token.");
                    }
                    jsonArray.add(Boolean.valueOf(tokenValue));
                    expectToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN | END_ARRAY_TOKEN;
                    break;
                case STRING:
                    if ((tokenType.getTokenCode() & expectToken) == 0) {
                        throw new JsonParseException("Parse error, invalid Token.");
                    }
                    jsonArray.add(tokenValue);
                    expectToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN | END_ARRAY_TOKEN;
                    break;
                case SEP_COMMA:
                    if ((tokenType.getTokenCode() & expectToken) == 0) {
                        throw new JsonParseException("Parse error, invalid Token.");
                    }
                    expectToken = STRING_TOKEN | NULL_TOKEN | NUMBER_TOKEN | BOOLEAN_TOKEN
                            | BEGIN_ARRAY_TOKEN | BEGIN_OBJECT_TOKEN;
                    break;
                case END_DOCUMENT:
                    return jsonArray;
                default:
                    throw new JsonParseException("Unexpected Token.");
            }
        }

        throw new JsonParseException("Parse error, invalid Token.");
    }
}