package delta.genea.data.sources;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.framework.objects.xml.ObjectXmlDriver;
import delta.common.framework.objects.xml.XmlObjectsSource;
import delta.genea.data.Act;
import delta.genea.data.ActText;
import delta.genea.data.ActType;
import delta.genea.data.Person;
import delta.genea.data.Picture;
import delta.genea.data.Place;
import delta.genea.data.Union;
import delta.genea.xml.ActTextXMLIO;
import delta.genea.xml.ActTypeXMLIO;
import delta.genea.xml.ActXMLDriver;
import delta.genea.xml.ActXMLIO;
import delta.genea.xml.PersonXMLDriver;
import delta.genea.xml.PersonXMLIO;
import delta.genea.xml.PictureXMLDriver;
import delta.genea.xml.PictureXMLIO;
import delta.genea.xml.PlaceXMLIO;
import delta.genea.xml.UnionXMLDriver;
import delta.genea.xml.UnionXMLIO;

/**
 * Data source for the genea objects of a single XML database.
 * @author DAM
 */
public class GeneaXmlDataSource extends XmlObjectsSource
{
  private static final Logger LOGGER=LoggerFactory.getLogger(GeneaXmlDataSource.class);

  /**
   * Private constructor.
   * @param rootDir Root directory of the database to manage.
   */
  public GeneaXmlDataSource(File rootDir)
  {
    super(rootDir);
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
    // Person
    {
      File xmlFile=getXmlFileForClass(Person.CLASS_NAME);
      PersonXMLIO xmlIO=new PersonXMLIO();
      xmlIO.setObjectSource(this);
      ObjectXmlDriver<Person> driver=new PersonXMLDriver();
      driver.setup(xmlFile,xmlIO,xmlIO);
      addClass(Person.class,driver);
    }
    // Act
    {
      File xmlFile=getXmlFileForClass(Act.CLASS_NAME);
      ActXMLIO xmlIO=new ActXMLIO();
      xmlIO.setObjectSource(this);
      ObjectXmlDriver<Act> driver=new ActXMLDriver();
      driver.setup(xmlFile,xmlIO,xmlIO);
      addClass(Act.class,driver);
    }
    // Act text
    {
      File xmlFile=getXmlFileForClass(ActText.CLASS_NAME);
      ActTextXMLIO xmlIO=new ActTextXMLIO();
      xmlIO.setObjectSource(this);
      ObjectXmlDriver<ActText> driver=new ObjectXmlDriver<ActText>(xmlFile,xmlIO,xmlIO);
      addClass(ActText.class,driver);
    }
    // Union
    {
      File xmlFile=getXmlFileForClass(Union.CLASS_NAME);
      UnionXMLIO xmlIO=new UnionXMLIO();
      xmlIO.setObjectSource(this);
      ObjectXmlDriver<Union> driver=new UnionXMLDriver();
      driver.setup(xmlFile,xmlIO,xmlIO);
      addClass(Union.class,driver);
    }
    // Picture
    {
      File xmlFile=getXmlFileForClass(Picture.CLASS_NAME);
      PictureXMLIO xmlIO=new PictureXMLIO();
      xmlIO.setObjectSource(this);
      ObjectXmlDriver<Picture> driver=new PictureXMLDriver();
      driver.setup(xmlFile,xmlIO,xmlIO);
      addClass(Picture.class,driver);
    }
  }
}
