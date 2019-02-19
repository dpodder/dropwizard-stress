package com.example.tools.stressclient.resources;

public class ResourceMismatchException extends Exception
{
    private static final long serialVersionUID = 1L;

    public ResourceMismatchException(String reason, String expected, String actual)
    {
        super(reason + "\n  Expected: <" + expected + ">\n  Actual  : <" + actual + ">");
    }
}
