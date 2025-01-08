package delta.genea.webhoover.utils;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods related to images.
 * @author DAM
 */
public class ImageUtils
{
  private static final Logger LOGGER=LoggerFactory.getLogger(ImageUtils.class);

  /**
   * Build a montage image.
   * @param files Input files.
   * @param output Result file.
   * @return <code>true</code> if successful, <code>false</code> otherwise.
   */
  public static boolean makeImage(File[][] files, File output)
  {
    boolean ret;
    int nbH=files.length;
    if (nbH==0)
    {
      return false;
    }
    int nbV=files[0].length;
    if (nbV==0)
    {
      return false;
    }
    ImageMontageMaker maker=new ImageMontageMaker();
    try
    {
      // Image building may raise an exception if JAI is not installed correctly
      // One way to do it is to install it on the JRE
      maker.doIt(files,output);
      ret=true;
    }
    catch(Exception e)
    {
      FileUtils.deleteFile(output);
      LOGGER.error("Could not make image: "+output,e);
      ret=false;
    }
    for(int hIndex=0;hIndex<nbH;hIndex++)
    {
      for(int vIndex=0;vIndex<nbV;vIndex++)
      {
        boolean ok=files[hIndex][vIndex].delete();
        if (!ok)
        {
          LOGGER.warn("Cannot delete: {}",files[hIndex][vIndex]);
        }
      }
    }
    return ret;
  }
}
