package delta.genea.data.sources;

import java.util.HashMap;

import org.apache.log4j.Logger;

import delta.common.framework.objects.data.ObjectSource;
import delta.genea.data.Act;
import delta.genea.data.ActText;
import delta.genea.data.Person;
import delta.genea.data.Picture;
import delta.genea.data.Place;
import delta.genea.data.Union;
import delta.genea.data.trees.AncestorsTreesRegistry;
import delta.genea.misc.GeneaCfg;
import delta.genea.sql.GeneaSqlDriver;
import delta.genea.utils.GeneaLoggers;

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
  private GeneaSqlDriver _driver;

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

  @Deprecated
  public static GeneaDataSource getInstance()
  {
    String dbName=GeneaCfg.getInstance().getDbName();
    GeneaDataSource instance=getInstance(dbName);
    return instance;
  }

  private GeneaDataSource(String dbName)
  {
    _dbName=dbName;
    _ancestorsTreesRegistry=new AncestorsTreesRegistry(this);
    buildDrivers();
  }

  public String getDbName()
  {
    return _dbName;
  }

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
    }
    catch(Exception e)
    {
      _logger.error("",e);
    }
  }

  public ObjectSource<Person> getPersonDataSource()
  {
    return _personDataSource;
  }

  public ObjectSource<Union> getUnionDataSource()
  {
    return _unionDataSource;
  }

  public ObjectSource<Place> getPlaceDataSource()
  {
    return _placeDataSource;
  }

  public ObjectSource<Act> getActDataSource()
  {
    return _actDataSource;
  }

  public ObjectSource<Picture> getPictureDataSource()
  {
    return _pictureDataSource;
  }

  public ObjectSource<ActText> getTextDataSource()
  {
    return _textDataSource;
  }

  public AncestorsTreesRegistry getAncestorsTreesRegistry()
  {
    return _ancestorsTreesRegistry;
  }

  public void close()
  {
    if (_driver!=null)
    {
      _driver.close();
    }
  }

  public static void closeAll()
  {
    for(GeneaDataSource source : _sources.values())
    {
      source.close();
    }
    _sources.clear();
  }
}
