package delta.genea.webhoover.ad62;

import java.io.File;

/**
 * Constants for webhoover 62.
 * @author DAM
 */
public class Constants
{
  private static final String ROOT_SITE="https://archivesenligne.pasdecalais.fr";

  /**
   * Get the URL for the main page of a set.
   * @param id Set identifier.
   * @return An URL.
   */
  public static String buildMainSetPageURL(int id)
  {
    return ROOT_SITE+"/v2/ad62/visualiseur/etat_civil.html?id="+id;
  }

  /**
   * Get the URL for the 'notice' page of a set.
   * @param id Set identifier.
   * @return An URL.
   */
  public static String buildNoticePageURL(int id)
  {
    return ROOT_SITE+"/v2/ad62/notice_ajax?id="+id+"&ajax=true";
  }

  /**
   * Get the URL for an image generation request.
   * @param width Image width.
   * @param height Image height.
   * @param image Image (ex: /mnt/lustre/ad62/etat_civil_registres_1/132/frad062_5mir_132_01/frad062_5mir_132_01_0547.jpg"/images/genereImage.html?r=0&n=0&b=0&c=0&o=IMG&id=visu_image_1&image=/mnt/lustre/ad62/etat_civil_registres_1/132/frad062_5mir_132_01/frad062_5mir_132_01_0001.jpg)
   * @return An URL.
   */
  public static String buildGenereImageURL(int width, int height, String image)
  {
    return ROOT_SITE+"/v2/images/genereImage.html?l="+width+"&h="+height+"&r=0&n=0&b=0&c=0&o=IMG&id=image_single&image="+image;
  }

  /**
   * Get the URL for a cached image.
   * @param cachePath Path of the cached image.
   * @return An URL.
   */
  public static String buildCachedImageURL(String cachePath)
  {
    return ROOT_SITE+cachePath;
  }

  /**
   * Get the filename to use for a page.
   * @param pageNumber Page number (starting at one).
   * @return A filename.
   */
  private static String getImageName(int pageNumber)
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
