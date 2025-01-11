package delta.genea.webhoover.free;

import java.io.File;

/**
 * Constants for the free webhoover.
 * @author DAM
 */
public class Constants
{
  /**
   * Root site URL.
   */
  // Could use: http://geneasion.free.fr/Numerisation/BMSCamphin/
  // Could use: http://ph.sion.free.fr/tabsion/
  public static final String SITE="http://geneavenir.free.fr/prisonniers%20de%20guerres/";

  /**
   * Output directory.
   */
  public static final File OUT_DIR=new File("/home/dm/tmp/prisonniers");
  /**
   * Name for the directory of temporary files.
   */
  public static final String TMP_NAME="prisonniers";
}
