package delta.genea.webhoover.utils;

import java.util.ArrayList;
import java.util.List;

import delta.genea.webhoover.HtmlTools;

/**
 * Utility methods related to text parsing.
 * @author DAM
 */
public class ParsingUtils
{
  private static final String START_TAG="<";
  private static final String END_TAG=">";
  private static final String SLASH="/";

  /**
   * Split the given string to get the contents of HTML tags.
   * @param tag Tags to use.
   * @param contents Input.
   * @return A list of tag contents.
   */
  public static List<String> splitAsTags(String tag, String contents)
  {
    String startTag=START_TAG+tag;
    String endTag=START_TAG+SLASH+tag+END_TAG;
    List<String> tags=new ArrayList<String>();
    int index;
    String contentsLeft=contents;
    String item;
    while (true)
    {
      index=contentsLeft.indexOf(startTag);
      if (index==-1) break;
      contentsLeft=contentsLeft.substring(index+startTag.length());
      index=contentsLeft.indexOf(END_TAG);
      if (index==-1) break;
      contentsLeft=contentsLeft.substring(index+END_TAG.length());
      index=contentsLeft.indexOf(endTag);
      if (index==-1) break;
      item=contentsLeft.substring(0,index);
      contentsLeft=contentsLeft.substring(index+endTag.length());
      item=HtmlTools.htmlToString(item);
      tags.add(item);
    }
    return tags;
  }
}
