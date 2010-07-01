package delta.genea.webhoover.gennpdc;

import java.io.File;

/**
 * @author DAM
 */
public class Constants
{
  /**
   * Root site URL.
   */
  public static final String SITE="http://www.gennpdc.net";
  public static final String URL_BIRTH_ACTS_FOR_PLACE=SITE+"/releves/tab_naiss.php?args=";
  public static final String ROOT_SITE_N="http://www.gennpdc.net/releves/acte_naiss.php?xid=";
  public static final String ROOT_SITE_M="http://www.gennpdc.net/releves/acte_mari.php?xid=";
  public static final File OUT_DIR=new File("/home/dm/tmp/gennpdc");
  public static final File ACTS_FILE=new File("/home/dm/bb.txt");
}
