package delta.genea.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import delta.common.framework.objects.sql.DatabaseType;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.misc.GeneaCfg;
import delta.genea.utils.GeneaLoggers;

/**
 * SQL driver for a 'genea' database.
 * @author DAM
 */
public class GeneaSqlDriver
{
  private static final Logger _logger=GeneaLoggers.getGeneaSqlLogger();
  private DatabaseType _databaseType;
  private Connection _dbConnection;
  private PersonSqlDriver _personDriver;
  private UnionSqlDriver _unionDriver;
  private PlaceSqlDriver _placeDriver;
  private ActSqlDriver _actDriver;
  private PictureSqlDriver _pictureDriver;
  private TextSqlDriver _textDriver;

  /**
   * Constructor.
   * @param dataSource Associated objects source.
   */
  public GeneaSqlDriver(GeneaDataSource dataSource)
  {
    try
    {
      buildConnection(dataSource);
      buildDrivers(dataSource);
    }
    catch (Exception e)
    {
      _logger.error("",e);
    }
  }

  private void buildConnection(GeneaDataSource dataSource) throws Exception
  {
    GeneaCfg cfg=GeneaCfg.getInstance();
    String dbType=cfg.getDBType();
    _databaseType=DatabaseType.getDBTypeByName(dbType);
    String driver=cfg.getJdbcDriver();
    Class.forName(driver);
    String url=cfg.getJdbcUrl();
    String dbName=dataSource.getDbName();
    url=url.replace("$DB_NAME",dbName);
    String user=cfg.getJdbcUser();
    String password=cfg.getJdbcPassword();
    _dbConnection=DriverManager.getConnection(url,user,password);
  }

  private void buildDrivers(GeneaDataSource dataSource)
  {
    _personDriver=new PersonSqlDriver(dataSource);
    _personDriver.setConnection(_dbConnection,_databaseType);
    _placeDriver=new PlaceSqlDriver(dataSource);
    _placeDriver.setConnection(_dbConnection,_databaseType);
    _unionDriver=new UnionSqlDriver(dataSource);
    _unionDriver.setConnection(_dbConnection,_databaseType);
    _actDriver=new ActSqlDriver(dataSource);
    _actDriver.setConnection(_dbConnection,_databaseType);
    _pictureDriver=new PictureSqlDriver(dataSource);
    _pictureDriver.setConnection(_dbConnection,_databaseType);
    _textDriver=new TextSqlDriver(dataSource);
    _textDriver.setConnection(_dbConnection,_databaseType);
  }

  /**
   * Get the SQL driver for persons.
   * @return the SQL driver for persons.
   */
  public PersonSqlDriver getPersonDriver()
  {
    return _personDriver;
  }

  /**
   * Get the SQL driver for unions.
   * @return the SQL driver for unions.
   */
  public UnionSqlDriver getUnionDriver()
  {
    return _unionDriver;
  }

  /**
   * Get the SQL driver for places.
   * @return the SQL driver for places.
   */
  public PlaceSqlDriver getPlaceDriver()
  {
    return _placeDriver;
  }

  /**
   * Get the SQL driver for acts.
   * @return the SQL driver for acts.
   */
  public ActSqlDriver getActDriver()
  {
    return _actDriver;
  }

  /**
   * Get the SQL driver for pictures.
   * @return the SQL driver for pictures.
   */
  public PictureSqlDriver getPictureDriver()
  {
    return _pictureDriver;
  }

  /**
   * Get the SQL driver for texts.
   * @return the SQL driver for texts.
   */
  public TextSqlDriver getTextDriver()
  {
    return _textDriver;
  }

  /**
   * Terminate this driver.
   */
  public void close()
  {
    try
    {
      _dbConnection.close();
    }
    catch (SQLException e)
    {
      _logger.error("",e);
    }
  }
}
