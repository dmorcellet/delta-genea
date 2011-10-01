package delta.genea.webhoover;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of tool methods related to text management.
 * @author DAM
 */
public class TextTools
{
  /**
   * On a given line, find all string items surrounded by <code>before</code> and </code>after</code>.
   * @param line Line to parse.
   * @param before String to find before items.
   * @param after String to find after items.
   * @return A possibly empty list of string items.
   */
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

  /**
   * In a list of lines, find the first line that contains the given <code>pattern</code>.
   * @param lines Lines to search.
   * @param pattern Pattern to search.
   * @return A line or <code>null</code< if none found.
   */
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

  /**
   * On a given line, find the first string item surrounded by <code>before</code> and </code>after</code>.
   * @param line Line to parse.
   * @param before String to find before item.
   * @param after String to find after item.
   * @return A string item or <code>null</code> if none found.
   */
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

  /**
   * In a given line, get the string item after the <code>before</code> string key.
   * @param line Line to parse.
   * @param before A string key.
   * @return A string item or <code>null</code> if <code>line</code> does not contain <code>before</code>.
   */
  public static String findAfter(String line, String before)
  {
    String ret=null;
    int index=line.indexOf(before);
    if (index!=-1)
    {
      ret=line.substring(index+before.length());
    }
    return ret;
  }
}
