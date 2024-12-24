package delta.genea.misc;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.utils.configuration.Configuration;
import delta.common.utils.configuration.Configurations;

/**
 * Provides access to configuration information for the GENEA application.
 * @author DAM
 */
public class GeneaCfg
{
  private static final Logger LOGGER=LoggerFactory.getLogger(GeneaCfg.class);

  /**
   * Section name used to look for configuration data.
   */
  private static final String SECTION_NAME="GENEA";

  /**
   * Root path for act images storage.
   */
  private static final String ACTS_ROOT="ACTS_ROOT";
  /**
   * Root path for act pictures storage.
   */
  private static final String PICTURES_ROOT="PICTURES_ROOT";

  private File _actsRootPath;
  private File _picturesRootPath;

  /**
   * Reference to the sole instance of this class.
   */
  private static GeneaCfg _instance;

  /**
   * Get the sole instance of this class.
   * @return The sole instance of this class.
   */
  public static GeneaCfg getInstance()
  {
    if (_instance==null)
    {
      _instance=new GeneaCfg();
    }
    return _instance;
  }

  /**
   * Private constructor.
   */
  private GeneaCfg()
  {
    Configuration cfg=Configurations.getConfiguration();
    _actsRootPath=cfg.getFileValue(SECTION_NAME,ACTS_ROOT,null);
    _picturesRootPath=cfg.getFileValue(SECTION_NAME,PICTURES_ROOT,null);
    if (LOGGER.isInfoEnabled())
    {
      LOGGER.info("_actsRootPath="+_actsRootPath);
      LOGGER.info("_picturesRootPath="+_picturesRootPath);
    }
  }

  /**
   * Get the root path for act images storage.
   * @return the root path for act images storage.
   */
  public File getActsRootPath()
  {
    return _actsRootPath;
  }

  /**
   * Get the root path for pictures images storage.
   * @return the root path for pictures images storage.
   */
  public File getPicturesRootPath()
  {
    return _picturesRootPath;
  }
}
