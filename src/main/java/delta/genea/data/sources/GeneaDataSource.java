package delta.genea.data.sources;

import java.util.HashMap;

import org.apache.log4j.Logger;

import delta.common.framework.objects.sql.DatabaseConfiguration;
import delta.common.framework.objects.sql.SqlObjectsSource;
import delta.genea.data.Act;
import delta.genea.data.ActText;
import delta.genea.data.ActType;
import delta.genea.data.Person;
import delta.genea.data.Picture;
import delta.genea.data.Place;
import delta.genea.data.Union;
import delta.genea.data.trees.AncestorsTreesRegistry;
import delta.genea.sql.ActSqlDriver;
import delta.genea.sql.ActTypeSqlDriver;
import delta.genea.sql.PersonSqlDriver;
import delta.genea.sql.PictureSqlDriver;
import delta.genea.sql.PlaceSqlDriver;
import delta.genea.sql.TextSqlDriver;
import delta.genea.sql.UnionSqlDriver;
import delta.genea.utils.GeneaLoggers;

/**
 * Data source for the genea objects of a single database.
 * @author DAM
 */
public class GeneaDataSource extends SqlObjectsSource
{
  private static final Logger _logger=GeneaLoggers.getGeneaLogger();

  private static HashMap<String,GeneaDataSource> _sources=new HashMap<String,GeneaDataSource>();

  private AncestorsTreesRegistry _ancestorsTreesRegistry;

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
    super(dbName);
    _ancestorsTreesRegistry=new AncestorsTreesRegistry(this);
    buildDrivers();
    try
    {
      start();
    }
    catch(Exception e)
    {
      _logger.error("Cannot start genea data source!",e);
    }
  }

  @Override
  protected DatabaseConfiguration buildDatabaseConfiguration(String dbName)
  {
    return new DatabaseConfiguration("delta/genea/misc/database.properties");
  }

  /**
   * Build the drivers for all the object classes.
   */
  private void buildDrivers()
  {
    try
    {
      addClass(Person.class,new PersonSqlDriver(this));
      addClass(Union.class,new UnionSqlDriver(this));
      addClass(Place.class,new PlaceSqlDriver(this));
      addClass(Act.class,new ActSqlDriver(this));
      addClass(Picture.class,new PictureSqlDriver(this));
      addClass(ActText.class,new TextSqlDriver());
      addClass(ActType.class,new ActTypeSqlDriver());
    }
    catch(Exception e)
    {
      _logger.error("",e);
    }
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
