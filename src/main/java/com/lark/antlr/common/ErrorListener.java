package com.lark.antlr.common;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class ErrorListener extends BaseErrorListener {

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine, String msg, RecognitionException e) {
        // Construct the error message
        String errorMessage = String.format("Syntax error at line %d:%d - %s", line, charPositionInLine, msg);

        // Optionally, you can log or print the error message

        // Throw a custom exception with the error message
        throw new SyntaxException(errorMessage);
    }
}