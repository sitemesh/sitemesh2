package com.opensymphony.module.sitemesh.parser.html;

public class ParserException extends RuntimeException {

    public ParserException(String message, int line, int column) {
        super(message + " [Line " + line + ", Column " + column + "]");
    }
    
}
