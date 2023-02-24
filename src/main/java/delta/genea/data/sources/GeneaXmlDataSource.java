package delta.genea.data.sources;

import java.io.File;
import java.util.HashMap;

import org.apache.log4j.Logger;

import delta.common.framework.objects.xml.ObjectXmlDriver;
import delta.common.framework.objects.xml.XmlObjectsSource;
import delta.genea.data.ActType;
import delta.genea.data.trees.AncestorsTreesRegistry;
import delta.genea.xml.ActTypeXMLIO;

/**
 * Data source for the genea objects of a single XML database.
 * @author DAM
 */
public class GeneaXmlDataSource extends XmlObjectsSource
{
  private static final Logger LOGGER=Logger.getLogger(GeneaXmlDataSource.class);

  private static HashMap<String,GeneaXmlDataSource> _sources=new HashMap<String,GeneaXmlDataSource>();

  private AncestorsTreesRegistry _ancestorsTreesRegistry;

  /**
   * Get the data source that manages the genea objects
   * for the given database.
   * @param rootDirectory Root directory of the targeted database.
   * @return A genea objects data source.
   */
  public static GeneaXmlDataSource getInstance(File rootDirectory)
  {
    synchronized (_sources)
    {
      String name=rootDirectory.getAbsolutePath();
      GeneaXmlDataSource instance=_sources.get(name);
      if (instance==null)
      {
        instance=new GeneaXmlDataSource(rootDirectory);
        _sources.put(name,instance);
      }
      return instance;
    }
  }

  /**
   * Private constructor.
   * @param rootDir Root directory of the database to manage.
   */
  private GeneaXmlDataSource(File rootDir)
  {
    super(rootDir);
    _ancestorsTreesRegistry=new AncestorsTreesRegistry(this);
    buildDrivers();
    try
    {
      start();
    }
    catch(Exception e)
    {
      LOGGER.error("Cannot start genea data source!",e);
    }
  }

  /**
   * Build the drivers for all the object classes.
   */
  private void buildDrivers()
  {
    /*
    addClass(Person.class);
    addClass(Union.class);
    addClass(Place.class);
    addClass(Act.class);
    addClass(Picture.class);
    addClass(ActText.class);
    */
    // Act type
    {
      File xmlFile=getXmlFileForClass(ActType.CLASS_NAME);
      ActTypeXMLIO xmlIO=new ActTypeXMLIO();
      ObjectXmlDriver<ActType> driver=new ObjectXmlDriver<ActType>(xmlFile,xmlIO,xmlIO);
      addClass(ActType.class,driver);
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
    for(GeneaXmlDataSource source : _sources.values())
    {
      source.close();
    }
    _sources.clear();
  }
}