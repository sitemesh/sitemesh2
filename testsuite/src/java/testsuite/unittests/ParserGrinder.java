package testsuite.unittests;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

import com.opensymphony.module.sitemesh.parser.FastPageParser;
import com.opensymphony.module.sitemesh.parser.HTMLPageParser;
import com.opensymphony.module.sitemesh.PageParser;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: Nov 22 2003
 * Time: 12:09:26 AM
 */
public class ParserGrinder
{
  public static void main(String[] args) throws IOException, InterruptedException
  {
    if(args.length==0)
    {
      System.err.println("Usage: java " + ParserGrinder.class.getName() + " <html file>");
      System.exit(1);
    }
    File file = new File(args[0]);
    if(!file.exists() || file.isDirectory())
    {
      System.err.println("File " + args[0] + " does not exist or is a directory");
      System.exit(1);
    }
    FileInputStream fis = new FileInputStream(file);
    byte[] contents = new byte[(int)file.length()];
    fis.read(contents);
    String page = new String(contents);
    final char[] chars = page.toCharArray();
    //warm up parser

    for(int i=0;i<10;i++)
    {
      PageParser parser = new HTMLPageParser();
      //FastPageParser parser = new FastPageParser();
      parser.parse(chars);
    }

    //now go crazy
    final int threadCount = 5;
    Thread[] threads = new Thread[threadCount];
    final List[] lists = new ArrayList[threadCount];
    final int passes = 50;
    for(int i=0;i<threads.length;i++)
    {
      final int index = i;
      lists[index] = new ArrayList(passes);
      threads[index] = new Thread(new Runnable()
      {
        public void run()
        {
          for(int j=0;j<passes;j++)
          {
            PageParser parser = new HTMLPageParser();
            //PageParser parser = new FastPageParser();
            try
            {
              lists[index].add(parser.parse(chars));
            }
            catch(IOException e)
            {
              e.printStackTrace();
            }
          }
        }
      });
    }
    //we do this here instead of above just to not count the thread creation overhead
    long now = System.currentTimeMillis();
    long startMemory = Runtime.getRuntime().freeMemory();
    System.out.println("startMemory=" + startMemory);
    for(int i=0;i<threads.length;i++)
    {
      threads[i].start();
    }

    for(int i = 0; i < threads.length; i++)
    {
      threads[i].join();
    }
    long timeTaken = System.currentTimeMillis() - now;
    //System.gc();
    long endMemory = Runtime.getRuntime().freeMemory();
    System.out.println("time taken " + timeTaken + " for " + (threads.length * passes) + " parses. Memory used=" + (endMemory-  startMemory));
  }
}
