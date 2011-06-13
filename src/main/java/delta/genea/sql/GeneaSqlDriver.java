package delta.genea.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import delta.common.framework.objects.sql.DatabaseType;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.misc.DatabaseConfiguration;
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
  private ActTypeSqlDriver _actTypeDriver;

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

  /**
   * Set the flag that drives foreign key checks.
   * @param doCheck <code>true</code> to perform checks, <code>false</code> otherwise.
   */
  public void setForeignKeyChecks(boolean doCheck)
  {
    Statement s=null;
    try
    {
      s=_dbConnection.createStatement();
      String sql="SET FOREIGN_KEY_CHECKS="+(doCheck?"1":"0");
      s.execute(sql);
    }
    catch(Exception e)
    {
      _logger.error("",e);
    }
    finally
    {
      if (s!=null)
      {
        try
        {
          s.close();
          s=null;
        }
        catch(Exception e)
        {
          _logger.error("",e);
        }
      }
    }
  }

  private void buildConnection(GeneaDataSource dataSource) throws Exception
  {
    DatabaseConfiguration cfg=DatabaseConfiguration.getInstance();
    String dbType=cfg.getDBType();
    _databaseType=DatabaseType.getDBTypeByName(dbType);
    String driver=cfg.getJdbcDriver();
    Class.forName(driver);
    String dbName=dataSource.getDbName();
    String url=cfg.getJdbcUrl(dbName);
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
    _actTypeDriver=new ActTypeSqlDriver(dataSource);
    _actTypeDriver.setConnection(_dbConnection,_databaseType);
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
   * Get the SQL driver for act types.
   * @return the SQL driver for act types.
   */
  public ActTypeSqlDriver getActTypesDriver()
  {
    return _actTypeDriver;
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
