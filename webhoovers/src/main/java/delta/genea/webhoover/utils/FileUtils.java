package delta.genea.webhoover.utils;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods related to files.
 * @author DAM
 */
public class FileUtils
{
  private static final Logger LOGGER=LoggerFactory.getLogger(FileUtils.class);

  /**
   * Delete a file.
   * @param file File to delete.
   * @return <code>true</code> if file does not exist or was deleted, <code>false</code> otherwise.
   */
  public static boolean deleteFile(File file)
  {
    boolean ret=true;
    if (file.exists())
    {
      boolean ok=file.delete();
      if (!ok)
      {
        LOGGER.warn("Could not delete file: {}",file);
      }
      ret=ok;
    }
    return ret;
  }
}
