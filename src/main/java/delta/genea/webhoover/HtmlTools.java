package delta.genea.webhoover;

/**
 * @author DAM
 */
public class HtmlTools
{
  private static final String[][] REPLACEMENTS=
  {
    { "&nbsp;",""},
    { "&eacute;", "é"},
    { "&egrave;", "è" },
    { "&ecirc;", "ê" },
    { "&agrave;", "à" },
    { "&ucirc;", "û" }
  };

  public static String htmlToString(String htmlString)
  {
    String tmp=htmlString;
    for(int i=0;i<REPLACEMENTS.length;i++)
    {
      tmp=tmp.replace(REPLACEMENTS[i][0],REPLACEMENTS[i][1]);
    }
    tmp=tmp.replace("<strong>","");
    tmp=tmp.replace("</strong>","");
    return tmp;
  }
}
