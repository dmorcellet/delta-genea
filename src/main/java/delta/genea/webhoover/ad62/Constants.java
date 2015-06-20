package delta.genea.webhoover.ad62;

import java.io.File;

/**
 * Constants for webhoover 62.
 * @author DAM
 */
public class Constants
{
  /**
   * Name of place to use.
   */
  public static final String PLACE_NAME="PROVIN";
  //public static final File ROOT_DIR=new File("/home/dm/data/genealogie/archives en ligne/ad62/Recensements/Billy Berclau/1831");
  //public static final File ROOT_DIR=new File("/home/dm/data/genealogie/archives en ligne/ad62/Repertoire Militaire/Bethune 1907 1001-1500");
  //public static final File ROOT_DIR=new File("/home/dm/data/genealogie/archives en ligne/ad62/Repertoire Militaire/Bethune 1905 1001-1500");
  /**
   * Root directory for file storage.
   */
  public static final File ROOT_DIR=new File("/home/dm/data/genealogie/archives en ligne/ad62/Repertoire Militaire/Bethune 1908 500-1000");
  /**
   * Identifier of package to get.
   */
  public static final String ID="620075538";
  /**
   * Number of pages in the package.
   */
  public static final int NB_PAGES=764;
  static final String ROOT_SITE="http://www.archinoe.net";

  /**
   * Root URL of the site.
   */
  public static final String SITE_ROOT="http://www.archinoe.net/cg62/registre.php#";

  /**
   * URL fragment for decade tables.
   */
  public static final String RECHERCHE_TD="?id=recherche_tables_decennales";

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
