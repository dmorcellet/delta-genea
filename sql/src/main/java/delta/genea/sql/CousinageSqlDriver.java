package delta.genea.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.data.ObjectsSource;
import delta.common.framework.objects.sql.ObjectSqlDriver;
import delta.common.utils.jdbc.CleanupManager;
import delta.genea.data.Cousinage;
import delta.genea.data.Person;

/**
 * SQL driver for cousinages.
 * @author DAM
 */
public class CousinageSqlDriver extends ObjectSqlDriver<Cousinage>
{
  private static final Logger LOGGER=LoggerFactory.getLogger(CousinageSqlDriver.class);

  private PreparedStatement _psGetByPrimaryKey;
  private PreparedStatement _psGetAll;
  private PreparedStatement _psGetFromPerson;

  private ObjectsSource _mainDataSource;

  /**
   * Constructor.
   * @param mainDataSource Main data source.
   */
  public CousinageSqlDriver(ObjectsSource mainDataSource)
  {
    _mainDataSource=mainDataSource;
  }

  @Override
  protected void buildPreparedStatements(Connection newConnection)
  {
    try
    {
      String fields="cle_cousinage,cle_cousin1,cle_cousin2";
      // Select
      String sql="SELECT "+fields+" FROM cousins WHERE cle_cousinage = ?";
      _psGetByPrimaryKey=newConnection.prepareStatement(sql);
      // Select all
      sql="SELECT "+fields+" FROM cousins";
      _psGetAll=newConnection.prepareStatement(sql);
      // Get from person
      sql="SELECT cle_cousinage FROM cousins WHERE cle_cousin1 = ? OR cle_cousin2 = ?";
      _psGetFromPerson=newConnection.prepareStatement(sql);
    }
    catch (SQLException sqlException)
    {
      LOGGER.error("Exception while building prepared statements for class Cousinage",sqlException);
    }
  }

  @Override
  public Cousinage getByPrimaryKey(Long primaryKey)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      Cousinage ret=null;
      ResultSet rs=null;
      try
      {
        _psGetByPrimaryKey.setLong(1,primaryKey.longValue());
        rs=_psGetByPrimaryKey.executeQuery();
        if (rs.next())
        {
          ret=new Cousinage(primaryKey);
          fillCousinage(ret,rs);
        }
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psGetByPrimaryKey);
      }
      finally
      {
        CleanupManager.cleanup(rs);
      }
      return ret;
    }
  }

  private void fillCousinage(Cousinage cousinage, ResultSet rs) throws SQLException
  {
    int n=2;
    DataProxy<Person> cousin1Proxy=null;
    long cousin1Key=rs.getLong(n);
    if (!rs.wasNull())
    {
      cousin1Proxy=_mainDataSource.buildProxy(Person.class,Long.valueOf(cousin1Key));
    }
    cousinage.setCousin1(cousin1Proxy);
    n++;
    DataProxy<Person> cousin2Proxy=null;
    long cousin2Key=rs.getLong(n);
    if (!rs.wasNull())
    {
      cousin2Proxy=_mainDataSource.buildProxy(Person.class,Long.valueOf(cousin2Key));
    }
    cousinage.setCousin2(cousin2Proxy);
  }

  @Override
  public List<Cousinage> getAll()
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      ArrayList<Cousinage> ret=new ArrayList<Cousinage>();
      Cousinage cousinage=null;
      ResultSet rs=null;
      try
      {
        rs=_psGetAll.executeQuery();
        while (rs.next())
        {
          Long primaryKey=null;
          long key=rs.getLong(1);
          if (!rs.wasNull())
          {
            primaryKey=Long.valueOf(key);
          }
          cousinage=new Cousinage(primaryKey);
          fillCousinage(cousinage,rs);
          ret.add(cousinage);
        }
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psGetAll);
      }
      finally
      {
        CleanupManager.cleanup(rs);
      }
      return ret;
    }
  }

  /**
   * Get the identifiers of cousinages where the person identified
   * by <code>primaryKey</code> do appear.
   * @param primaryKey Identifier of the targeted person.
   * @return A list of cousinage identifiers.
   */

  public List<Long> getFromPerson(Long primaryKey)
  {
    ArrayList<Long> ret=new ArrayList<Long>();
    Long cousinage=null;
    ResultSet rs=null;
    try
    {
      _psGetFromPerson.setLong(1,primaryKey.longValue());
      _psGetFromPerson.setLong(2,primaryKey.longValue());
      rs=_psGetFromPerson.executeQuery();
      while (rs.next())
      {
        cousinage=Long.valueOf(rs.getLong(1));
        ret.add(cousinage);
      }
    }
    catch (SQLException sqlException)
    {
      LOGGER.error("",sqlException);
      CleanupManager.cleanup(_psGetFromPerson);
    }
    finally
    {
      CleanupManager.cleanup(rs);
    }
    return ret;
  }

  @Override
  public List<Long> getRelatedObjectIDs(String relationName, Long primaryKey)
  {
    List<Long> ret=new ArrayList<Long>();
    if (relationName.equals(Cousinage.COUSINAGES_RELATION))
    {
      ret=getFromPerson(primaryKey);
    }
    return ret;
  }
}
