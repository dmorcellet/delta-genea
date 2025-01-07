package delta.genea.webhoover.ad01;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Check completion of act directories.
 * @author DAM
 */
public class MainCheckActsDir
{
  private static final Logger LOGGER=LoggerFactory.getLogger(MainCheckActsDir.class);

  /**
   * Get the filename for an image.
   * @param nb Image index.
   * @return A filename.
   */
  public static String getImageFileName(int nb)
  {
    String nbStr=String.valueOf(nb);
    if (nb<10) nbStr="0"+nbStr;
    if (nb<100) nbStr="0"+nbStr;
    return nbStr+".jpg";
  }

  /**
   * Get the filename for a big image.
   * @param nb Image index.
   * @return A filename.
   */
  public static String getBigImageFileName(int nb)
  {
    String nbStr=String.valueOf(nb);
    if (nb<10) nbStr="0"+nbStr;
    if (nb<100) nbStr="0"+nbStr;
    return "big"+nbStr+".png";
  }

  /**
   * Handle a directory.
   * @param dir Directory to use.
   */
  public static void handleDirectory(File dir)
  {
    String[] childs=dir.list();
    int nbchilds=childs.length;
    if (nbchilds%2!=0)
    {
      LOGGER.warn("Odd number of files in {}",dir);
    }
    List<String> names=Arrays.asList(childs);
    Collections.sort(names);
    int nb=nbchilds/2;
    for(int i=0;i<nb;i++)
    {
      String expected=getImageFileName(i);
      if (!names.get(i).equals(expected))
      {
        LOGGER.warn("Expected: {}",expected);
      }
      String expected2=getBigImageFileName(i);
      if (!names.get(i+nb).equals(expected2))
      {
        LOGGER.warn("Expected: {}",expected2);
      }
    }
  }

  /**
   * Perform checks.
   * @param dir Directory to use.
   */
  public static void doIt(File dir)
  {
    File[] childs=dir.listFiles();
    File child;
    for(int i=0;i<childs.length;i++)
    {
      child=childs[i];
      if (!child.isDirectory())
      {
        LOGGER.error("Not a directory: {}",child);
      }
      else
      {
        LOGGER.info("Handling: {}",child);
        handleDirectory(child);
      }
    }
  }

  /**
   * Main method for this tool.
   * @param args Not used.
   */
  public static void main(String[] args)
  {
    doIt(Constants.ROOT_DIR);
  }
}
