package delta.genea.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.framework.objects.sql.ObjectSqlDriver;
import delta.common.utils.jdbc.CleanupManager;
import delta.common.utils.jdbc.JDBCTools;
import delta.genea.data.ActText;

/**
 * SQL driver for texts.
 * @author DAM
 */
public class TextSqlDriver extends ObjectSqlDriver<ActText>
{
  private static final Logger LOGGER=LoggerFactory.getLogger(TextSqlDriver.class);

  private PreparedStatement _psGetByPrimaryKey;
  private PreparedStatement _psGetAll;
  private PreparedStatement _psInsert;
  private PreparedStatement _psUpdate;
  private PreparedStatement _psCount;

  /**
   * Constructor.
   */
  public TextSqlDriver()
  {
    // Nothing to do!
  }

  @Override
  protected void buildPreparedStatements(Connection newConnection)
  {
    try
    {
      String fields="cle,texte";
      // Select
      String sql="SELECT "+fields+" FROM texte_acte WHERE cle = ?";
      _psGetByPrimaryKey=newConnection.prepareStatement(sql);
      // Select all
      sql="SELECT "+fields+" FROM texte_acte ORDER BY cle";
      _psGetAll=newConnection.prepareStatement(sql);
      // Insert
      sql="INSERT INTO texte_acte ("+fields+") VALUES (?,?)";
      if (usesHSQLDB())
      {
        _psInsert=newConnection.prepareStatement(sql);
      }
      else
      {
        _psInsert=newConnection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
      }
      // Update
      sql="UPDATE texte_acte SET texte=? WHERE cle=?";
      _psUpdate=newConnection.prepareStatement(sql);
      // Select count
      sql="SELECT COUNT(*) FROM texte_acte WHERE cle = ?";
      _psCount=newConnection.prepareStatement(sql);
    }
    catch (SQLException sqlException)
    {
      LOGGER.error("Exception while building prepared statements for class ActText",sqlException);
    }
  }

  @Override
  public ActText getByPrimaryKey(Long primaryKey)
  {
    if (primaryKey==null)
    {
      return null;
    }
    Connection connection=getConnection();
    synchronized (connection)
    {
      ActText ret=null;
      ResultSet rs=null;
      try
      {
        _psGetByPrimaryKey.setLong(1,primaryKey.longValue());
        rs=_psGetByPrimaryKey.executeQuery();
        if (rs.next())
        {
          ret=new ActText(primaryKey);
          fillActText(ret,rs);
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
  public List<ActText> getAll()
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      List<ActText> list=new ArrayList<ActText>();
      ActText actText=null;
      ResultSet rs=null;
      try
      {
        rs=_psGetAll.executeQuery();
        while (rs.next())
        {
          actText=new ActText(Long.valueOf(rs.getLong(1)));
          fillActText(actText,rs);
          list.add(actText);
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
      return list;
    }
  }

  private void fillActText(ActText text, ResultSet rs) throws SQLException
  {
    int n=2;
    text.setText(rs.getString(n));
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
  public void create(ActText text)
  {
    if (text==null)
    {
      throw new IllegalArgumentException("text==null");
    }
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
        _psInsert.setString(n,text.getText());
        _psInsert.executeUpdate();
        if (key==null)
        {
          if (usesHSQLDB())
          {
            Long primaryKey=JDBCTools.getPrimaryKey(connection,1);
            text.setPrimaryKey(primaryKey);
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
      }
      catch (SQLException sqlException)
      {
        LOGGER.error("",sqlException);
        CleanupManager.cleanup(_psInsert);
      }
    }
  }

  @Override
  public void update(ActText text)
  {
    if (text==null)
    {
      throw new IllegalArgumentException("text==null");
    }
    Long key=text.getPrimaryKey();
    if (key==null)
    {
      throw new IllegalArgumentException("key==null");
    }
    Connection connection=getConnection();
    synchronized (connection)
    {
      try
      {
        int n=1;
        _psUpdate.setString(n,text.getText());
        n++;
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
