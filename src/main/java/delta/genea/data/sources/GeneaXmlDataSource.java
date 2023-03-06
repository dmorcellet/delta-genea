package delta.genea.data.sources;

import java.io.File;
import java.util.HashMap;

import org.apache.log4j.Logger;

import delta.common.framework.objects.xml.ObjectXmlDriver;
import delta.common.framework.objects.xml.XmlObjectsSource;
import delta.genea.data.Act;
import delta.genea.data.ActType;
import delta.genea.data.Place;
import delta.genea.data.Union;
import delta.genea.data.trees.AncestorsTreesRegistry;
import delta.genea.xml.ActTypeXMLIO;
import delta.genea.xml.ActXMLIO;
import delta.genea.xml.PlaceXMLIO;
import delta.genea.xml.UnionXMLIO;

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
    addClass(Picture.class);
    addClass(ActText.class);
    */
    // Act type
    {
      File xmlFile=getXmlFileForClass(ActType.CLASS_NAME);
      ActTypeXMLIO xmlIO=new ActTypeXMLIO();
      xmlIO.setObjectSource(this);
      ObjectXmlDriver<ActType> driver=new ObjectXmlDriver<ActType>(xmlFile,xmlIO,xmlIO);
      addClass(ActType.class,driver);
    }
    // Place
    {
      File xmlFile=getXmlFileForClass(Place.CLASS_NAME);
      PlaceXMLIO xmlIO=new PlaceXMLIO();
      xmlIO.setObjectSource(this);
      ObjectXmlDriver<Place> driver=new ObjectXmlDriver<Place>(xmlFile,xmlIO,xmlIO);
      addClass(Place.class,driver);
    }
    // Act
    {
      File xmlFile=getXmlFileForClass(Act.CLASS_NAME);
      ActXMLIO xmlIO=new ActXMLIO();
      xmlIO.setObjectSource(this);
      ObjectXmlDriver<Act> driver=new ObjectXmlDriver<Act>(xmlFile,xmlIO,xmlIO);
      addClass(Act.class,driver);
    }
    // Union
    {
      File xmlFile=getXmlFileForClass(Union.CLASS_NAME);
      UnionXMLIO xmlIO=new UnionXMLIO();
      xmlIO.setObjectSource(this);
      ObjectXmlDriver<Union> driver=new ObjectXmlDriver<Union>(xmlFile,xmlIO,xmlIO);
      addClass(Union.class,driver);
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
