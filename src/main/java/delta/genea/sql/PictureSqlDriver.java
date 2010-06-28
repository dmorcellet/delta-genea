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

import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.sql.ObjectSqlDriver;
import delta.common.utils.jdbc.CleanupManager;
import delta.common.utils.jdbc.JDBCTools;
import delta.genea.data.Person;
import delta.genea.data.PersonInPicture;
import delta.genea.data.Picture;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.utils.GeneaLoggers;

/**
 * SQL driver for pictures.
 * @author DAM
 */
public class PictureSqlDriver extends ObjectSqlDriver<Picture>
{
  private static final Logger _logger=GeneaLoggers.getGeneaSqlLogger();

  private PreparedStatement _psGetByPrimaryKey;
  private PreparedStatement _psGetAll;
  private PreparedStatement _psInsert;
  private PreparedStatement _psUpdate;
  private PreparedStatement _psCount;
  private PreparedStatement _psGetOtherFromPerson;
  private PreparedStatement _psGetPersonsInPicture;
  private GeneaDataSource _mainDataSource;

  PictureSqlDriver(GeneaDataSource mainDataSource)
  {
    _mainDataSource=mainDataSource;
  }

  @Override
  protected void buildPreparedStatements(Connection newConnection)
  {
    try
    {
      String fields="cle_photo,titre,chemin,commentaire";
      // Select
      String sql="SELECT "+fields+" FROM photo WHERE cle_photo = ?";
      _psGetByPrimaryKey=newConnection.prepareStatement(sql);
      // Get all
      sql="SELECT "+fields+" FROM photo";
      _psGetAll=newConnection.prepareStatement(sql);
      // Insert
      sql="INSERT INTO photo ("+fields+") VALUES (?,?,?,?)";
      if (usesHSQLDB())
      {
        _psInsert=newConnection.prepareStatement(sql);
      }
      else
      {
        _psInsert=newConnection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
      }
      // Update
      sql="UPDATE photo SET title=?,chemin=?,commentaire=? WHERE cle_photo=?";
      _psUpdate=newConnection.prepareStatement(sql);
      // Select count
      sql="SELECT COUNT(*) FROM photo WHERE cle_photo = ?";
      _psCount=newConnection.prepareStatement(sql);
      sql="SELECT DISTINCT cle_photo FROM personne_photo WHERE cle_personne = ?";
      _psGetOtherFromPerson=newConnection.prepareStatement(sql);
      sql="SELECT cle_personne FROM personne_photo WHERE cle_photo = ?";
      _psGetPersonsInPicture=newConnection.prepareStatement(sql);
    }
    catch (SQLException sqlException)
    {
      _logger.error("Exception while building prepared statements for class Picture",sqlException);
    }
  }

  @Override
  public Picture getByPrimaryKey(long primaryKey)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      Picture ret=null;
      ResultSet rs=null;
      try
      {
        _psGetByPrimaryKey.setLong(1,primaryKey);
        rs=_psGetByPrimaryKey.executeQuery();
        if (rs.next())
        {
          ret=new Picture(primaryKey,_mainDataSource.getPictureDataSource());
          fillPicture(ret,rs);
          List<PersonInPicture> persons=loadPersonsInPicture(primaryKey);
          ret.setPersonsInPicture(persons);
        }
      }
      catch (SQLException sqlException)
      {
        _logger.error("",sqlException);
        CleanupManager.cleanup(_psGetByPrimaryKey);
      }
      finally
      {
        CleanupManager.cleanup(rs);
      }
      return ret;
    }
  }

  private void fillPicture(Picture picture, ResultSet rs) throws SQLException
  {
    int n=2;
    picture.setTitle(rs.getString(n));
    n++;
    picture.setPath(rs.getString(n));
    n++;
    picture.setComment(rs.getString(n));
    n++;
  }

  private List<PersonInPicture> loadPersonsInPicture(long primaryKey)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      List<PersonInPicture> ret=null;
      PersonInPicture tmp=null;
      ResultSet rs=null;
      try
      {
        _psGetPersonsInPicture.setLong(1,primaryKey);
        rs=_psGetPersonsInPicture.executeQuery();
        while (rs.next())
        {
          tmp=new PersonInPicture();
          int n=1;
          tmp.setPersonProxy(new DataProxy<Person>(rs.getLong(n),
              _mainDataSource.getPersonDataSource()));
          n++;
          if (ret==null)
          {
            ret=new ArrayList<PersonInPicture>();
          }
          ret.add(tmp);
        }
      }
      catch (SQLException sqlException)
      {
        _logger.error("",sqlException);
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
  public List<Picture> getAll()
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      ArrayList<Picture> ret=new ArrayList<Picture>();
      Picture picture=null;
      ResultSet rs=null;
      try
      {
        //System.out.println("GET ALL pictures");
        rs=_psGetAll.executeQuery();
        while (rs.next())
        {
          long primaryKey=rs.getLong(1);
          picture=new Picture(primaryKey,_mainDataSource.getPictureDataSource());
          fillPicture(picture,rs);
          List<PersonInPicture> persons=loadPersonsInPicture(primaryKey);
          picture.setPersonsInPicture(persons);
          ret.add(picture);
        }
      }
      catch (SQLException sqlException)
      {
        _logger.error("",sqlException);
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
   * Indicates if the picture identified by <code>primaryKey</code>
   * exists or not.
   * @param primaryKey Identifier for the targeted picture.
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
        rs=_psGetByPrimaryKey.executeQuery();
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
        _logger.error("",sqlException);
        CleanupManager.cleanup(_psGetByPrimaryKey);
      }
      finally
      {
        CleanupManager.cleanup(rs);
      }
      return ret;
    }
  }

  /**
   * Get the identifiers of pictures where the person identified
   * by <code>primaryKey</code> do appear.
   * @param primaryKey Identifier of the targeted person.
   * @return A list of picture identifiers.
   */
  public List<Long> getPicturesForPerson(long primaryKey)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      ArrayList<Long> ret=new ArrayList<Long>();
      Long pictureKey=null;
      ResultSet rs=null;
      try
      {
        _psGetOtherFromPerson.setLong(1,primaryKey);
        rs=_psGetOtherFromPerson.executeQuery();
        while (rs.next())
        {
          pictureKey=Long.valueOf(rs.getLong(1));
          ret.add(pictureKey);
        }
      }
      catch (SQLException sqlException)
      {
        _logger.error("",sqlException);
        CleanupManager.cleanup(_psGetOtherFromPerson);
      }
      finally
      {
        CleanupManager.cleanup(rs);
      }
      return ret;
    }
  }

  @Override
  public List<Long> getRelatedObjectIDs(String relationName, long primaryKey)
  {
    List<Long> ret=null;
    if (relationName.equals(Picture.PICTURES_FOR_PERSON_RELATION))
    {
      ret=getPicturesForPerson(primaryKey);
    }
    return ret;
  }

  @Override
  public void create(Picture picture)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      try
      {
        int n=1;
        long key=picture.getPrimaryKey();
        if (key==0) _psInsert.setNull(n,Types.INTEGER);
        else _psInsert.setLong(n,key);
        n++;
        _psInsert.setString(n,picture.getTitle());
        n++;
        _psInsert.setString(n,picture.getPath());
        n++;
        _psInsert.setString(n,picture.getComment());
        n++;
        _psInsert.executeUpdate();
        if (usesHSQLDB())
        {
          if (key==0)
          {
            long primaryKey=JDBCTools.getPrimaryKey(connection,1);
            picture.setPrimaryKey(primaryKey);
          }
        }
        else
        {
          ResultSet rs=_psInsert.getGeneratedKeys();
          if (rs.next())
          {
            long primaryKey=rs.getLong(1);
            picture.setPrimaryKey(primaryKey);
          }
        }
      }
      catch (SQLException sqlException)
      {
        _logger.error("",sqlException);
        CleanupManager.cleanup(_psInsert);
      }
    }
  }

  @Override
  public void update(Picture picture)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      try
      {
        int n=1;
        _psUpdate.setString(n,picture.getTitle());
        n++;
        _psUpdate.setString(n,picture.getPath());
        n++;
        _psUpdate.setString(n,picture.getComment());
        n++;
        long key=picture.getPrimaryKey();
        _psUpdate.setLong(n,key);
        n++;
        _psUpdate.executeUpdate();
      }
      catch (SQLException sqlException)
      {
        _logger.error("",sqlException);
        CleanupManager.cleanup(_psInsert);
      }
    }
  }
}
