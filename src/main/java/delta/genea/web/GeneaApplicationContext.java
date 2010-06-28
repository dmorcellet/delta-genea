package delta.genea.web;

import java.io.File;

import delta.common.framework.web.WebApplicationContext;
import delta.genea.misc.GeneaCfg;
import delta.genea.misc.GeneaConstants;

/**
 * @author DAM
 */
public class GeneaApplicationContext extends WebApplicationContext
{
  public GeneaApplicationContext()
  {
    // Nothing to do here !
  }

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
