package com.code4wt.jsonparser.tokenizer;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by code4wt on 17/5/11.
 */
public class CharReader {

    private static final int BUFFER_SIZE = 1024;

    private Reader reader;

    private char[] buffer;

    private int pos;

    private int size;

    public CharReader(Reader reader) {
        this.reader = reader;
        buffer = new char[BUFFER_SIZE];
    }

    public char next() throws IOException {
        if (!hasMore()) {
            return (char) -1;
        }

        return buffer[pos++];
    }

    public boolean hasMore() throws IOException {
        if (pos < size) {
            return true;
        }

        fillBuffer();
        return pos < size;
    }

    void fillBuffer() throws IOException {
        int n = reader.read(buffer);
        if (n == -1) {
            return;
        }

        pos = 0;
        size = n;
    }
}
