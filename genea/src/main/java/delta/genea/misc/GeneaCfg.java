package delta.genea.misc;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.utils.misc.TypedProperties;

/**
 * Provides access to configuration information for the GENEA application.
 * @author DAM
 */
public class GeneaCfg
{
  private static final Logger LOGGER=LoggerFactory.getLogger(GeneaCfg.class);

  private File _actsRootPath;
  private File _picturesRootPath;
  private String _defaultDatasource;
  private int _port;

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
    TypedProperties props=new TypedProperties();
    props.loadFromFile(new File("genea.cfg"));
    String actsRootPath=props.getStringProperty("acts_root_dir","acts");
    _actsRootPath=new File(actsRootPath);
    LOGGER.info("_actsRootPath={}",_actsRootPath);
    String picturesRootPath=props.getStringProperty("pictures_root_dir","pictures");
    _picturesRootPath=new File(picturesRootPath);
    LOGGER.info("_picturesRootPath={}",_picturesRootPath);
    _defaultDatasource=props.getStringProperty("datasource.default.name","genea");
    LOGGER.info("_defaultDatasource={}",_defaultDatasource);
    _port=props.getIntProperty("server.port",8081);
    LOGGER.info("_port={}",Integer.valueOf(_port));
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

  /**
   * get the default datasource.
   * @return A datasource spec.
   */
  public String getDefaultDatasource()
  {
    return _defaultDatasource;
  }

  /**
   * Get the web server port.
   * @return A port.
   */
  public int getServerPort()
  {
    return _port;
  }
}
