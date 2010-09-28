package delta.genea.data.sources;

import java.util.HashMap;

import org.apache.log4j.Logger;

import delta.common.framework.objects.data.ObjectSource;
import delta.genea.data.Act;
import delta.genea.data.ActText;
import delta.genea.data.ActType;
import delta.genea.data.Person;
import delta.genea.data.Picture;
import delta.genea.data.Place;
import delta.genea.data.Union;
import delta.genea.data.trees.AncestorsTreesRegistry;
import delta.genea.sql.GeneaSqlDriver;
import delta.genea.utils.GeneaLoggers;

/**
 * Data source for the genea objects of a single database.
 * @author DAM
 */
public class GeneaDataSource
{
  private static final Logger _logger=GeneaLoggers.getGeneaLogger();

  private static HashMap<String,GeneaDataSource> _sources=new HashMap<String,GeneaDataSource>();

  private String _dbName;
  private AncestorsTreesRegistry _ancestorsTreesRegistry;
  private ObjectSource<Person> _personDataSource;
  private ObjectSource<Union> _unionDataSource;
  private ObjectSource<Place> _placeDataSource;
  private ObjectSource<Act> _actDataSource;
  private ObjectSource<Picture> _pictureDataSource;
  private ObjectSource<ActText> _textDataSource;
  private ObjectSource<ActType> _actTypeDataSource;
  private GeneaSqlDriver _driver;

  /**
   * Get the data source that manages the genea objects
   * for the given database.
   * @param dbName Name of the targeted database.
   * @return A genea objects data source.
   */
  public static GeneaDataSource getInstance(String dbName)
  {
    synchronized (_sources)
    {
      GeneaDataSource instance=_sources.get(dbName);
      if (instance==null)
      {
        instance=new GeneaDataSource(dbName);
        _sources.put(dbName,instance);
      }
      return instance;
    }
  }

  /**
   * Private constructor.
   * @param dbName Name of the database to manage.
   */
  private GeneaDataSource(String dbName)
  {
    _dbName=dbName;
    _ancestorsTreesRegistry=new AncestorsTreesRegistry(this);
    buildDrivers();
  }

  /**
   * Get the name of the managed database.
   * @return the name of the managed database.
   */
  public String getDbName()
  {
    return _dbName;
  }

  /**
   * Build the drivers for all the object classes.
   */
  private void buildDrivers()
  {
    try
    {
      _driver=new GeneaSqlDriver(this);
      _personDataSource=new ObjectSource<Person>(_driver.getPersonDriver());
      _unionDataSource=new ObjectSource<Union>(_driver.getUnionDriver());
      _placeDataSource=new ObjectSource<Place>(_driver.getPlaceDriver());
      _actDataSource=new ObjectSource<Act>(_driver.getActDriver());
      _pictureDataSource=new ObjectSource<Picture>(_driver.getPictureDriver());
      _textDataSource=new ObjectSource<ActText>(_driver.getTextDriver());
      _actTypeDataSource=new ObjectSource<ActType>(_driver.getActTypesDriver());
    }
    catch(Exception e)
    {
      _logger.error("",e);
    }
  }

  /**
   * Get the objects source for persons.
   * @return the objects source for persons.
   */
  public ObjectSource<Person> getPersonDataSource()
  {
    return _personDataSource;
  }

  /**
   * Get the objects source for unions.
   * @return the objects source for unions.
   */
  public ObjectSource<Union> getUnionDataSource()
  {
    return _unionDataSource;
  }

  /**
   * Get the objects source for places.
   * @return the objects source for places.
   */
  public ObjectSource<Place> getPlaceDataSource()
  {
    return _placeDataSource;
  }

  /**
   * Get the objects source for acts.
   * @return the objects source for acts.
   */
  public ObjectSource<Act> getActDataSource()
  {
    return _actDataSource;
  }

  /**
   * Get the objects source for pictures.
   * @return the objects source for pictures.
   */
  public ObjectSource<Picture> getPictureDataSource()
  {
    return _pictureDataSource;
  }

  /**
   * Get the objects source for texts.
   * @return the objects source for texts.
   */
  public ObjectSource<ActText> getTextDataSource()
  {
    return _textDataSource;
  }

  /**
   * Get the objects source for act types.
   * @return the objects source for act types.
   */
  public ObjectSource<ActType> getActTypeDataSource()
  {
    return _actTypeDataSource;
  }

  /**
   * Get the registry for ancestors tree.
   * @return the registry for ancestors tree.
   */
  public AncestorsTreesRegistry getAncestorsTreesRegistry()
  {
    return _ancestorsTreesRegistry;
  }

  /**
   * Close this data source.
   */
  public void close()
  {
    if (_driver!=null)
    {
      _driver.close();
    }
  }

  /**
   * Close all data sources.
   */
  public static void closeAll()
  {
    for(GeneaDataSource source : _sources.values())
    {
      source.close();
    }
    _sources.clear();
  }
}
