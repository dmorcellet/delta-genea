package delta.genea.misc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
    InputStream is=getClass().getClassLoader().getResourceAsStream("delta/genea/misc/datasource.properties");
    Properties props=new Properties();
    try
    {
      props.load(is);
    }
    catch(IOException ioe)
    {
      LOGGER.error("",ioe);
    }
    loadFromProperties(props);
  }
  
  private void loadFromProperties(Properties props)
  {
    _datasourceName=props.getProperty("datasource.default.name","genea");
    if (LOGGER.isInfoEnabled())
    {
      LOGGER.info("_datasourceName="+_datasourceName);
    }
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
