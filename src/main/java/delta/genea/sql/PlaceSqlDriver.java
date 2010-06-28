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
import delta.genea.data.Place;
import delta.genea.data.PlaceLevel;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.utils.GeneaLoggers;

/**
 * SQL driver for places.
 * @author DAM
 */
public class PlaceSqlDriver extends ObjectSqlDriver<Place>
{
  private static final Logger _logger=GeneaLoggers.getGeneaSqlLogger();

  private PreparedStatement _psGetByPrimaryKey;
  private PreparedStatement _psGetAll;
  private PreparedStatement _psInsert;
  private GeneaDataSource _mainDataSource;

  PlaceSqlDriver(GeneaDataSource mainDataSource)
  {
    _mainDataSource=mainDataSource;
  }

  @Override
  protected void buildPreparedStatements(Connection newConnection)
  {
    try
    {
      String fields="cle,nom,nom_court,level,parent";
      String sql="SELECT "+fields+" FROM commune WHERE cle = ?";
      _psGetByPrimaryKey=newConnection.prepareStatement(sql);
      sql="SELECT "+fields+" FROM commune ORDER BY nom";
      _psGetAll=newConnection.prepareStatement(sql);
      sql="INSERT INTO commune (cle,nom,nom_court,level,parent) VALUES (?,?,?,?,?)";
      if (usesHSQLDB())
      {
        _psInsert=newConnection.prepareStatement(sql);
      }
      else
      {
        _psInsert=newConnection.prepareStatement(sql,
            Statement.RETURN_GENERATED_KEYS);
      }
    }
    catch (SQLException sqlException)
    {
      _logger.error("Exception while building prepared statements for class Place",sqlException);
    }
  }

  @Override
  public Place getByPrimaryKey(long primaryKey)
  {
    Connection connection=getConnection();
    synchronized (connection)
    {
      Place ret=null;
      ResultSet rs=null;
      try
      {
        _psGetByPrimaryKey.setLong(1,primaryKey);
        rs=_psGetByPrimaryKey.executeQuery();
        if (rs.next())
        {
          ret=new Place(primaryKey,_mainDataSource.getPlaceDataSource());
          fillPlace(ret,rs);
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
          place=new Place(rs.getLong(1),_mainDataSource.getPlaceDataSource());
          fillPlace(place,rs);
          list.add(place);
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
    long parentPlaceKey=rs.getLong(n);
    DataProxy<Place> parentPlaceProxy=null;
    if (!rs.wasNull()) parentPlaceProxy=new DataProxy<Place>(parentPlaceKey,
        place.getSource());
    place.setParentPlaceProxy(parentPlaceProxy);
    n++;
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
        long key=place.getPrimaryKey();
        if (key==0) _psInsert.setNull(n,Types.INTEGER);
        else _psInsert.setLong(n,key);
        n++;
        _psInsert.setString(n,place.getName());
        n++;
        _psInsert.setString(n,place.getShortName());
        n++;
        _psInsert.setInt(n,place.getLevel().getValue());
        n++;
        DataProxy<Place> parent=place.getParentPlaceProxy();
        if (parent!=null) _psInsert.setLong(n,parent.getPrimaryKey());
        else _psInsert.setNull(n,Types.INTEGER);
        n++;
        _psInsert.executeUpdate();
        if (usesHSQLDB())
        {
          if (key==0)
          {
            long primaryKey=JDBCTools.getPrimaryKey(connection,1);
            place.setPrimaryKey(primaryKey);
          }
        }
        else
        {
          ResultSet rs=_psInsert.getGeneratedKeys();
          if (rs.next())
          {
            long primaryKey=rs.getLong(1);
            place.setPrimaryKey(primaryKey);
          }
          else
          {
            _logger.error("No generated key for Place create");
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
}
