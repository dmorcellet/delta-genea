package delta.genea.web;

import java.io.File;

import delta.common.framework.web.WebApplicationContext;
import delta.genea.misc.GeneaCfg;
import delta.genea.misc.GeneaConstants;

/**
 * Application context for the 'genea' web application.
 * @author DAM
 */
public class GeneaApplicationContext extends WebApplicationContext
{
  /**
   * Constructor.
   */
  public GeneaApplicationContext()
  {
    // Nothing to do here !
  }

  /**
   * Get the path for an image.
   * @param targetedDir Key for image directory (ACTS or PICTURES).
   * @param image Relative image path.
   * @return An absolute file path.
   */
  public File getImagePath(String targetedDir, String image)
  {
    File root=null;
    if (GeneaConstants.ACTS_DIR.equals(targetedDir))
    {
      root=GeneaCfg.getInstance().getActsRootPath();
    }
    else if (GeneaConstants.PICTURES_DIR.equals(targetedDir))
    {
      root=GeneaCfg.getInstance().getPicturesRootPath();
    }
    if (root!=null)
    {
      return new File(root,image);
    }
    return null;
  }
}
