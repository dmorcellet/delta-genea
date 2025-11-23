package delta.genea;

import java.io.File;

/**
 * Resolver for act files.
 * @author DAM
 */
public class ActFileResolver
{
  /**
   * Find out the file for an act.
   * @param rootDir Root directory for the file.
   * @param baseFilename Base filename.
   * @param index Part index.
   * @return A relative file name.
   */
  public static String resolveFilename(File rootDir, String baseFilename, int index)
  {
    String filename=baseFilename;
    if ((filename.endsWith(".jpg")) || (filename.endsWith(".png")))
    {
      filename=filename.substring(0,filename.length()-4);
    }
    if (index>1)
    {
      filename=filename+"-"+index;
    }
    File jpgFile=new File(rootDir,filename+".jpg");
    if (jpgFile.exists())
    {
      return filename+".jpg";
    }
    File pngFile=new File(rootDir,filename+".png");
    if (pngFile.exists())
    {
      return filename+".png";
    }
    return filename+".jpg";
  }
}
