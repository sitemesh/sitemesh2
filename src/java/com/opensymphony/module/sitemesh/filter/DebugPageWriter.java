/*
 * Title:        DebugPageWriter
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.filter;

import java.io.PrintWriter;

public class DebugPageWriter extends PrintWriter {
    public DebugPageWriter(PrintWriter out) {
        super(out);
    }

    public void flush() {
        System.out.println("DebugPageWriter.flush");
        super.flush();
    }

    public void close() {
        System.out.println("DebugPageWriter.close");
        super.close();
    }

    public boolean checkError() {
        System.out.println("DebugPageWriter.checkError");
        return super.checkError();
    }

    protected void setError() {
        System.out.println("DebugPageWriter.setError");
        super.setError();
    }

    public void write(int c) {
        System.out.println("DebugPageWriter.write (int) :" + c);
        super.write(c);
    }

    public void write(char buf[], int off, int len) {
        System.out.println("DebugPageWriter.write (buf, off, len) :" + new String(buf) + " " + off + " " + len);
        super.write(buf, off, len);
    }

    public void write(char buf[]) {
        System.out.println("DebugPageWriter.write (buf) :" + new String(buf));
        super.write(buf);
    }

    public void write(String s, int off, int len) {
        System.out.println("DebugPageWriter.write (s, off, len) :" + s + " " + off + " " + len);
        super.write(s, off, len);
    }

    public void write(String s) {
        System.out.println("DebugPageWriter.write (s) :" + s);
        super.write(s);
    }

    public void print(boolean b) {
        System.out.println("DebugPageWriter.print (boolean) : " + b);
        super.print(b);
    }

    public void print(char c) {
        System.out.println("DebugPageWriter.print (char) :" + c);
        super.print(c);
    }

    public void print(int i) {
        System.out.println("DebugPageWriter.print (int) :" + i);
        super.print(i);
    }

    public void print(long l) {
        System.out.println("DebugPageWriter.print (long) :" + l);
        super.print(l);
    }

    public void print(float f) {
        System.out.println("DebugPageWriter.print (float) :" + f);
        super.print(f);
    }

    public void print(double d) {
        System.out.println("DebugPageWriter.print (double) :" + d);
        super.print(d);
    }

    public void print(char s[]) {
        System.out.println("DebugPageWriter.print (char[]) :" + new String(s));
        super.print(s);
    }

    public void print(String s) {
        System.out.println("DebugPageWriter.print (string) :" + s);
        super.print(s);
    }

    public void print(Object obj) {
        System.out.println("DebugPageWriter.print (obj) :[" + obj.getClass().getName() + "] " + obj.toString());
        super.print(obj);
    }

    public void println() {
        System.out.println("DebugPageWriter.println");
        super.println();
    }

    public void println(boolean x) {
        System.out.println("DebugPageWriter.println (boolean) :" + x);
        super.println(x);
    }

    public void println(char x) {
        System.out.println("DebugPageWriter.println (char) :" + x);
        super.println(x);
    }

    public void println(int x) {
        System.out.println("DebugPageWriter.println (int) :" + x);
        super.println(x);
    }

    public void println(long x) {
        System.out.println("DebugPageWriter.println (long) :" + x);
        super.println(x);
    }

    public void println(float x) {
        System.out.println("DebugPageWriter.println (float) :" + x);
        super.println(x);
    }

    public void println(double x) {
        System.out.println("DebugPageWriter.println (double) :" + x);
        super.println(x);
    }

    public void println(char x[]) {
        System.out.println("DebugPageWriter.println (char[]) :" + x);
        super.println(x);
    }

    public void println(String x) {
        System.out.println("DebugPageWriter.println (string) :" + x);
        super.println(x);
    }

    public void println(Object x) {
        System.out.println("DebugPageWriter.println (obj) :[" + x.getClass().getName() + "] " + x.toString());
        super.println(x);
    }

}
