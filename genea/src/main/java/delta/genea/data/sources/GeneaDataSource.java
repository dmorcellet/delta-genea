package delta.genea.data.sources;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.data.Identifiable;
import delta.common.framework.objects.data.ObjectsManager;
import delta.common.framework.objects.data.ObjectsSource;
import delta.genea.data.trees.AncestorsTreesRegistry;

/**
 * Data source for the genea objects of a single database.
 * @author DAM
 */
public class GeneaDataSource
{
  private static HashMap<String,GeneaDataSource> _sources=new HashMap<String,GeneaDataSource>();

  private AncestorsTreesRegistry _ancestorsTreesRegistry;
  private ObjectsSource _source;

  public static GeneaDataSource getByName(String name)
  {
    GeneaDataSource dataSource;
    File xmlDir=new File(name);
    if (xmlDir.exists())
    {
      dataSource=GeneaDataSource.getInstance(xmlDir);
    }
    else
    {
      dataSource=GeneaDataSource.getInstance(name);
    }
    return dataSource;
  }

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
        ObjectsSource source=new GeneaSqlDataSource(dbName);
        instance=new GeneaDataSource(source);
        _sources.put(dbName,instance);
      }
      return instance;
    }
  }

  /**
   * Get the data source that manages the genea objects
   * for the given database.
   * @param rootDirectory Root directory of the targeted database.
   * @return A genea objects data source.
   */
  public static GeneaDataSource getInstance(File rootDirectory)
  {
    synchronized (_sources)
    {
      String name=rootDirectory.getAbsolutePath();
      GeneaDataSource instance=_sources.get(name);
      if (instance==null)
      {
        ObjectsSource source=new GeneaXmlDataSource(rootDirectory);
        instance=new GeneaDataSource(source);
        _sources.put(name,instance);
      }
      return instance;
    }
  }

  /**
   * Constructor.
   * @param source Objects source to use.
   */
  private GeneaDataSource(ObjectsSource source)
  {
    _source=source;
    _ancestorsTreesRegistry=new AncestorsTreesRegistry(source);
  }

  /**
   * Get the objects source.
   * @return the objects source.
   */
  public ObjectsSource getObjectsSource()
  {
    return _source;
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
   * Build a proxy for an object.
   * @param c Class of object to be proxified.
   * @param primaryKey Identifying key for the targeted object.
   * @return A proxy.
   */
  public <E extends Identifiable<Long>> DataProxy<E> buildProxy(Class<E> c, Long primaryKey)
  {
    return _source.buildProxy(c,primaryKey);
  }

  /**
   * Get the manager for a class.
   * @param c Class of objects.
   * @return A manager or <code>null</code> if not found.
   */
  public <E extends Identifiable<Long>> ObjectsManager<E> getManager(Class<E> c)
  {
    return _source.getManager(c);
  }

  /**
   * Create an object.
   * @param c Class of object to create.
   * @param object Object to create.
   */
  public <E extends Identifiable<Long>> void create(Class<E> c, E object)
  {
    _source.create(c,object);
  }

  /**
   * Update an object.
   * @param c Class of object to update.
   * @param object Object to update.
   */
  public <E extends Identifiable<Long>> void update(Class<E> c, E object)
  {
    _source.update(c,object);
  }

  /**
   * Load an object.
   * @param c Class of object to load.
   * @param primaryKey Identifying key for the targeted object.
   * @return The loaded object or <code>null</code> if not found.
   */
  public <E extends Identifiable<Long>> E load(Class<E> c, Long primaryKey)
  {
    return _source.load(c,primaryKey);
  }

  /**
   * Get all the objects of the managed class.
   * @param c Class of objects to load.
   * @return a list of such objects.
   */
  public <E extends Identifiable<Long>> List<E> loadAll(Class<E> c)
  {
    return _source.loadAll(c);
  }

  /**
   * Get the objects related to object whose primary
   * key is <code>primaryKey</code> using the designated relation.
   * @param c Class of objects to load.
   * @param relationName Name of the relation to use.
   * @param primaryKey Primary key of the root object.
   * @return A list of such objects.
   */
  public <E extends Identifiable<Long>> List<E> loadRelation(Class<E> c, String relationName, Long primaryKey)
  {
    return getManager(c).loadRelation(relationName,primaryKey);
  }

  /**
   * Get the objects that belong to the designated set, using the given parameters
   * (the number and types of the parameters depend on the nature of the designated
   * set).
   * @param c Class of objects to load.
   * @param setID Name of the set to use.
   * @param parameters Parameters for this set.
   * @return A list of such objects.
   */
  public <E extends Identifiable<Long>> List<E> loadObjectSet(Class<E> c, String setID, Object... parameters)
  {
    return getManager(c).loadObjectSet(setID,parameters);
  }

  /**
   * Delete an object in the managed persistence system.
   * @param c Class of objects to delete.
   * @param primaryKey Primary of the object to delete.
   */
  public <E extends Identifiable<Long>> void delete(Class<E> c, Long primaryKey)
  {
    _source.delete(c,primaryKey);
  }

  /**
   * Close.
   */
  public void close()
  {
    _source.close();
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
