package delta.genea.webhoover.ad62;

import java.io.File;

/**
 * Constants for webhoover 62.
 * @author DAM
 */
public class Constants
{
  /**
   * Identifier of package to get.
   */
  static final String ID="620075538";
  static final String ROOT_SITE="http://www.archinoe.net";

  /**
   * Root URL of the site.
   */
  private static final String SITE_ROOT="http://www.archinoe.net/cg62/registre.php#";

  /**
   * Get the URL of the main page.
   * @return an URL.
   */
  public static String getMainURL()
  {
    return SITE_ROOT;
  }

  /**
   * Get the filename to use for a page.
   * @param pageNumber Page number (starting at one).
   * @return A filename.
   */
  public static String getImageName(int pageNumber)
  {
    String fileName=String.valueOf(pageNumber);
    if (pageNumber<10) fileName="0"+fileName;
    if (pageNumber<100) fileName="0"+fileName;
    if (pageNumber<1000) fileName="0"+fileName;
    fileName="page_"+fileName+".jpg";
    return fileName;
  }

  /**
   * Get the file to use for a page.
   * @param rootDir Root directory for this file.
   * @param pageNumber Page number (starting at one).
   * @return A file.
   */
  public static File getImageFile(File rootDir, int pageNumber)
  {
    String fileName=getImageName(pageNumber);
    File out=new File(rootDir,fileName);
    return out;
  }
}
