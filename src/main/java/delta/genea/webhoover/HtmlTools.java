package delta.genea.webhoover;

/**
 * Some HTML decoding tools.
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

  /**
   * Transform an HTML fragment to a plain string.
   * @param htmlString HTML string to use.
   * @return A plain string.
   */
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
