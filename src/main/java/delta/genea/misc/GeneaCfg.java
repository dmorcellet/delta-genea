package delta.genea.misc;

import java.io.File;

import org.apache.log4j.Logger;

import delta.common.utils.configuration.Configuration;
import delta.common.utils.configuration.Configurations;
import delta.genea.utils.GeneaLoggers;

/**
 * Provides access to configuration information for the GENEA application.
 * @author DAM
 */
public class GeneaCfg
{
  private static final Logger _logger=GeneaLoggers.getGeneaLogger();

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
  /**
   * Database type.
   */
  private static final String DB_TYPE="DB_TYPE";

  private File _actsRootPath;
  private File _picturesRootPath;
  private String _dbType;
  private String _jdbcDriver;
  private String _dbName;
  private String _jdbcUrl;
  private String _jdbcUser;
  private String _jdbcPassword;

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
    _dbType=cfg.getStringValue(SECTION_NAME,DB_TYPE,"MYSQL");
    String section="GENEA."+_dbType;
    _jdbcDriver=cfg.getStringValue(section,"SQL.DRIVER","com.mysql.jdbc.Driver");
    _dbName=cfg.getStringValue(section,"SQL.DB_NAME","genea");
    _jdbcUrl=cfg.getStringValue(section,"SQL.URL","jdbc:mysql://localhost:3306/genea");
    _jdbcUser=cfg.getStringValue(section,"SQL.USER","dada");
    _jdbcPassword=cfg.getStringValue(section,"SQL.PASSWORD","glor4fin3del");
    if (_logger.isInfoEnabled())
    {
      _logger.info("_actsRootPath="+_actsRootPath);
      _logger.info("_picturesRootPath="+_picturesRootPath);
      _logger.info("_dbType="+_dbType);
      _logger.info("_jdbcDriver="+_jdbcDriver);
      _logger.info("_jdbcUrl="+_jdbcUrl);
      _logger.info("_jdbcUser="+_jdbcUser);
      _logger.info("_jdbcPassword="+_jdbcPassword);
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

  /**
   * Get the database type.
   * @return the database type.
   */
  public String getDBType()
  {
    return _dbType;
  }

  /**
   * Get the JDBC driver class.
   * @return the JDBC driver class.
   */
  public String getJdbcDriver()
  {
    return _jdbcDriver;
  }

  /**
   * Get the default database name.
   * @return the default database name.
   */
  public String getDbName()
  {
    return _dbName;
  }

  /**
   * Get the JDBC database URL.
   * @return the JDBC database URL.
   */
  public String getJdbcUrl()
  {
    return _jdbcUrl;
  }

  /**
   * Get the JDBC user name.
   * @return the JDBC user name.
   */
  public String getJdbcUser()
  {
    return _jdbcUser;
  }

  /**
   * Get the password of JDBC user.
   * @return the password of JDBC user.
   */
  public String getJdbcPassword()
  {
    return _jdbcPassword;
  }
}
