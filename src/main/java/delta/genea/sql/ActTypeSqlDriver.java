package delta.genea.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import delta.common.framework.objects.sql.ObjectSqlDriver;
import delta.common.utils.jdbc.CleanupManager;
import delta.common.utils.jdbc.JDBCTools;
import delta.genea.data.ActType;

/**
 * SQL driver for act types.
 * @author DAM
 */
public class ActTypeSqlDriver extends ObjectSqlDriver<ActType>
{
  private static final Logger LOGGER=Logger.getLogger(ActTypeSqlDriver.class);

  private PreparedStatement _psGetByPrimaryKey;
  private PreparedStatement _psGetAll;
  private PreparedStatement _psInsert;
  private PreparedStatement _psUpdate;
  private PreparedStatement _psCount;

  /**
   * Constructor.
   */
  public ActTypeSqlDriver()
  {
    // Nothing to do!
  }

  @Override
  protected void buildPreparedStatements(Connection newConnection)
  {
    try
    {
      String fields="cle,type";
      // Select
      String sql="SELECT "+fields+" FROM type_acte WHERE cle = ?";
      _psGetByPrimaryKey=newConnection.prepareStatement(sql);
      // Select all
      sql="SELECT "+fields+" FROM type_acte";
      _psGetAll=newConnection.prepareStatement(sql);
      // Insert
      sql="INSERT INTO type_acte ("+fields+") VALUES (?,?)";
      if (usesHSQLDB())
      {
        _psInsert=newConnection.prepareStatement(sql);
      }
      else
      {
        _psInsert=newConnection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
      }
      // Update
      sql="UPDATE type_acte SET type=? WHERE cle=?";
      _psUpdate=newConnection.prepareStatement(sql);
      // Select count
      sql="SELECT COUNT(*) FROM type_acte WHERE cle = ?";
      _psCount=newConnection.prepareStatement(sql);
    }
    catch (SQLException sqlException)
    {
      LOGGER.error("Exception while building prepared statements for class ActType",sqlException);
    }
  }

  @Override
  public ActType getByPrimaryKey(Long primaryKey)
  {
    if (primaryKey==null)
    {
      return null;
    }
    Connection connection=getConnection();
    synchronized (connection)
    {
      ActType ret=null;
      ResultSet rs=null;
      try
      {
        _psGetByPrimaryKey.setLong(1,primaryKey.longValue());
        rs=_psGetByPrimaryKey.executeQuery();
        if (rs.next())
        {
          ret=new ActType(primaryKey);
          fillActType(ret,rs);
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


  @Override
  public List<ActType> getAll()
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      ArrayList<ActType> ret=new ArrayList<ActType>();
      ActType actType=null;
      ResultSet rs=null;
      try
      {
        rs=_psGetAll.executeQuery();
        while (rs.next())
        {
          long personKey=rs.getLong(1);
          Long primaryKey=Long.valueOf(personKey);
          actType=new ActType(primaryKey);
          fillActType(actType,rs);
          ret.add(actType);
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

  private void fillActType(ActType text, ResultSet rs) throws SQLException
  {
    int n=2;
    text.setType(rs.getString(n));
  }

  /**
   * Indicates if the text identified by <code>primaryKey</code>
   * exists or not.
   * @param primaryKey Identifier for the targeted text.
   * @return <code>true</code> if it does, <code>false</code> otherwise.
   */
  public boolean exists(long primaryKey)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      boolean ret=false;
      ResultSet rs=null;
      try
      {
        _psCount.setLong(1,primaryKey);
        rs=_psCount.executeQuery();
        if (rs.next())
        {
          long count=rs.getLong(1);
          if (count>0)
          {
            ret=true;
          }
        }
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psCount);
      }
      finally
      {
        CleanupManager.cleanup(rs);
      }
      return ret;
    }
  }

  @Override
  public void create(ActType text)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      try
      {
        int n=1;
        Long key=text.getPrimaryKey();
        if (key==null)
        {
          _psInsert.setNull(n,Types.INTEGER);
        }
        else
        {
          _psInsert.setLong(n,key.longValue());
        }
        n++;
        _psInsert.setString(n,text.getType());
        _psInsert.executeUpdate();
        if (usesHSQLDB())
        {
          if (key==null)
          {
            Long primaryKey=JDBCTools.getPrimaryKey(connection,1);
            text.setPrimaryKey(primaryKey);
          }
        }
        else
        {
          ResultSet rs=_psInsert.getGeneratedKeys();
          if (rs.next())
          {
            long primaryKey=rs.getLong(1);
            text.setPrimaryKey(Long.valueOf(primaryKey));
          }
        }
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psInsert);
      }
    }
  }

  @Override
  public void update(ActType text)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      try
      {
        int n=1;
        _psUpdate.setString(n,text.getType());
        n++;
        Long key=text.getPrimaryKey();
        _psUpdate.setLong(n,key.longValue());
        _psUpdate.executeUpdate();
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psInsert);
      }
    }
  }
}
