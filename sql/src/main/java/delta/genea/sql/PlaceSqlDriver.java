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

import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.data.ObjectsSource;
import delta.common.framework.objects.sql.ObjectSqlDriver;
import delta.common.utils.jdbc.CleanupManager;
import delta.common.utils.jdbc.JDBCTools;
import delta.genea.data.Place;
import delta.genea.data.PlaceLevel;

/**
 * SQL driver for places.
 * @author DAM
 */
public class PlaceSqlDriver extends ObjectSqlDriver<Place>
{
  private static final Logger LOGGER=LoggerFactory.getLogger(PlaceSqlDriver.class);

  private PreparedStatement _psGetByPrimaryKey;
  private PreparedStatement _psGetAll;
  private PreparedStatement _psInsert;
  private ObjectsSource _mainDataSource;

  /**
   * Constructor.
   * @param mainDataSource Main data source.
   */
  public PlaceSqlDriver(ObjectsSource mainDataSource)
  {
    _mainDataSource=mainDataSource;
  }

  @Override
  protected void buildPreparedStatements(Connection newConnection)
  {
    try
    {
      String fields="cle,nom,nom_court,level,parent";
      // Select
      String sql="SELECT "+fields+" FROM commune WHERE cle = ?";
      _psGetByPrimaryKey=newConnection.prepareStatement(sql);
      // Select all
      sql="SELECT "+fields+" FROM commune ORDER BY nom";
      _psGetAll=newConnection.prepareStatement(sql);
      // Insert
      sql="INSERT INTO commune (cle,nom,nom_court,level,parent) VALUES (?,?,?,?,?)";
      if (usesHSQLDB())
      {
        _psInsert=newConnection.prepareStatement(sql);
      }
      else
      {
        _psInsert=newConnection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
      }
    }
    catch (SQLException sqlException)
    {
      LOGGER.error("Exception while building prepared statements for class Place",sqlException);
    }
  }

  @Override
  public Place getByPrimaryKey(Long primaryKey)
  {
    if (primaryKey==null)
    {
      return null;
    }
    Connection connection=getConnection();
    synchronized (connection)
    {
      Place ret=null;
      ResultSet rs=null;
      try
      {
        _psGetByPrimaryKey.setLong(1,primaryKey.longValue());
        rs=_psGetByPrimaryKey.executeQuery();
        if (rs.next())
        {
          ret=new Place(primaryKey);
          fillPlace(ret,rs);
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
  public List<Place> getAll()
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      List<Place> list=new ArrayList<Place>();
      Place place=null;
      ResultSet rs=null;
      try
      {
        rs=_psGetAll.executeQuery();
        while (rs.next())
        {
          place=new Place(Long.valueOf(rs.getLong(1)));
          fillPlace(place,rs);
          list.add(place);
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

  private void fillPlace(Place place, ResultSet rs) throws SQLException
  {
    int n=2;
    place.setName(rs.getString(n));
    n++;
    place.setShortName(rs.getString(n));
    n++;
    place.setLevel(PlaceLevel.getFromValue(rs.getInt(n)));
    n++;
    DataProxy<Place> parentPlaceProxy=null;
    long parentPlaceKey=rs.getLong(n);
    if (!rs.wasNull())
    {
      parentPlaceProxy=_mainDataSource.buildProxy(Place.class,Long.valueOf(parentPlaceKey));
    }
    place.setParentPlaceProxy(parentPlaceProxy);
  }

  @Override
  public void create(Place place)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      try
      {
        int n=1;
        Long key=place.getPrimaryKey();
        if (key==null)
        {
          _psInsert.setNull(n,Types.INTEGER);
        }
        else
        {
          _psInsert.setLong(n,key.longValue());
        }
        n++;
        _psInsert.setString(n,place.getName());
        n++;
        _psInsert.setString(n,place.getShortName());
        n++;
        _psInsert.setInt(n,place.getLevel().getValue());
        n++;
        DataProxy<Place> parent=place.getParentPlaceProxy();
        if ((parent!=null) && (parent.getPrimaryKey()!=null))
        {
          _psInsert.setLong(n,parent.getPrimaryKey().longValue());
        }
        else
        {
          _psInsert.setNull(n,Types.INTEGER);
        }
        _psInsert.executeUpdate();
        if (key==null)
        {
          if (usesHSQLDB())
          {
            Long primaryKey=JDBCTools.getPrimaryKey(connection,1);
            place.setPrimaryKey(primaryKey);
          }
          else
          {
            ResultSet rs=_psInsert.getGeneratedKeys();
            if (rs.next())
            {
              long primaryKey=rs.getLong(1);
              place.setPrimaryKey(Long.valueOf(primaryKey));
            }
            else
            {
              LOGGER.error("No generated key for Place create");
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
}
