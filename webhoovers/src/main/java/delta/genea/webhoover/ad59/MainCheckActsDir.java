package delta.genea.webhoover.ad59;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tool to check acts directories.
 * @author DAM
 */
public class MainCheckActsDir
{
  private static final Logger LOGGER=LoggerFactory.getLogger(MainCheckActsDir.class);

  /**
   * Handle a directory.
   * @param childDir Directory to handle.
   */
  public static void handleChildDir(File childDir)
  {
    String[] childs=childDir.list();
    int nbchilds=childs.length;
    List<String> names=Arrays.asList(childs);
    Collections.sort(names);
    for(int i=0;i<nbchilds;i++)
    {
      String expected=Constants.getImageName(i+1);
      if (!names.get(i).equals(expected))
      {
        LOGGER.warn("Directory: {}. Expected: {}",childDir,expected);
      }
    }
  }

  /**
   * Do the job.
   * @param dir Directory to handle.
   */
  public static void doIt(File dir)
  {
    File[] childs=dir.listFiles();
    for(int i=0;i<childs.length;i++)
    {
      File child=childs[i];
      if (!child.isDirectory())
      {
        LOGGER.error("Not a directory: {}",child);
      }
      else
      {
        LOGGER.info("Handling: {}",child);
        handleChildDir(child);
      }
    }
  }

  /**
   * Main method for this tool.
   * @param args Output directory.
   */
  public static void main(String[] args)
  {
    File toDir=new File(args[0]);
    File dir=new File(toDir,Constants.PLACE_NAME);
    doIt(dir);
  }
}
