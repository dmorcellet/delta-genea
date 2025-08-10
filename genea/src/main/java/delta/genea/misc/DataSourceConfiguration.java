package delta.genea.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data source configuration.
 * @author DAM
 */
public class DataSourceConfiguration
{
  private static final Logger LOGGER=LoggerFactory.getLogger(DataSourceConfiguration.class);

  private String _datasourceName;

  /**
   * Reference to the sole instance of this class.
   */
  private static DataSourceConfiguration _instance;

  /**
   * Get the sole instance of this class.
   * @return The sole instance of this class.
   */
  public static DataSourceConfiguration getInstance()
  {
    if (_instance==null)
    {
      _instance=new DataSourceConfiguration();
    }
    return _instance;
  }

  /**
   * Private constructor.
   */
  private DataSourceConfiguration()
  {
    _datasourceName=GeneaCfg.getInstance().getDefaultDatasource();
    LOGGER.info("_datasourceName={}",_datasourceName);
  }

  /**
   * Get the default data source name.
   * @return the default data source name.
   */
  public String getDefaultDatasourceName()
  {
    return _datasourceName;
  }
}
