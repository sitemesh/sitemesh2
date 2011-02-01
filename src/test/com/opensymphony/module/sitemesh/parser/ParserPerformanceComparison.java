package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;

import java.io.*;

public class ParserPerformanceComparison {

    public static void main(String... args) throws Exception
    {
        // Read the file
        CharArrayWriter writer = new CharArrayWriter();
        BufferedReader reader = new BufferedReader(new FileReader("/home/jazzy/Desktop/test.html"));
        char[] buffer = new char[4096];
        int length;
        while ((length = reader.read(buffer)) > 0)
        {
            writer.write(buffer, 0, length);
        }
        reader.close();
        char[] page = writer.toCharArray();
        // Create the parsers
        PageParser normal = new HTMLPageParser();
        PageParser fast = new FastPageParser();
        PageParser superfast = new SuperFastSimplePageParser();

        int times = 1000;

        System.out.println("Amount of data: " + page.length);
        
        System.gc();
        runPerformanceTest("Normal #1", page, normal, times);
        System.gc();
        runPerformanceTest("Fast #1", page, fast, times);
        System.gc();
        runPerformanceTest("Super Fast #1", page, superfast, times);
        System.gc();
        runPerformanceTest("Normal #2", page, normal, times);
        System.gc();
        runPerformanceTest("Fast #2", page, fast, times);
        System.gc();
        runPerformanceTest("Super Fast #2", page, superfast, times);
        System.gc();
        runPerformanceTest("Normal #3", page, normal, times);
        System.gc();
        runPerformanceTest("Fast #2", page, fast, times);
        System.gc();
        runPerformanceTest("Super Fast #2", page, superfast, times);
    }

    public static void runPerformanceTest(String name, char[] data, PageParser parser, int times) throws Exception
    {
        Writer writer = new NullWriter();
        long start = System.currentTimeMillis();
        for (int i = 0; i < times; i++)
        {
            Page page = parser.parse(data);
            page.writeBody(writer);
        }
        long finish = System.currentTimeMillis();
        long time = finish - start;
        System.out.println(name + " total: " + time + "ms");
        System.out.println(name + " average: " + (double) time / (double) times + "ms");
    }

    private static class NullWriter extends Writer
    {
        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            // do nothnig
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }
    }
}
