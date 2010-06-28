package delta.genea.webhoover;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import delta.common.utils.files.TextFileReader;

/**
 * @author DAM
 */
public class TextTools
{
  public static List<String> findAllBetween(String line, String before, String after)
  {
    List<String> ret=new ArrayList<String>();
    int baseIndex=0;
    while(true)
    {
      int index=line.indexOf(before,baseIndex);
      if (index!=-1)
      {
        int index2=line.indexOf(after,index+before.length());
        if (index2!=-1)
        {
          String item=line.substring(index+before.length(),index2);
          ret.add(item);
          baseIndex=index2+after.length();
        }
        else
        {
          break;
        }
      }
      else
      {
        break;
      }
    }
    return ret;
  }

  public static String findLine(List<String> lines, String pattern)
  {
    for(String line : lines)
    {
      if (line.indexOf(pattern)!=-1)
      {
        return line;
      }
    }
    return null;
  }

  public static String findBetween(String line, String before, String after)
  {
    String ret=null;
    int index=line.indexOf(before);
    if (index!=-1)
    {
      int index2=line.indexOf(after,index+before.length());
      if (index2!=-1)
      {
        ret=line.substring(index+before.length(),index2);
      }
    }
    return ret;
  }

  public static List<String> splitAsLines(File page)
  {
    List<String> ret=new ArrayList<String>();
    TextFileReader reader=new TextFileReader(page);
    if (reader.start())
    {
      String line;
      while(true)
      {
        line=reader.getNextLine();
        if (line==null) break;
        ret.add(line);
      }
      reader.terminate();
    }
    return ret;
  }
}
