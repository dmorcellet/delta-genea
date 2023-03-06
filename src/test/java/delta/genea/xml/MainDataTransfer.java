package delta.genea.xml;

import java.io.File;
import java.util.List;

import delta.common.framework.objects.data.ObjectsManager;
import delta.common.framework.objects.xml.ObjectXmlDriver;
import delta.genea.data.Act;
import delta.genea.data.Place;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.data.sources.GeneaXmlDataSource;

/**
 * Tool to transfer data from a SQL source to a XML target.
 * @author DAM
 */
public class MainDataTransfer
{
  private static final String SOURCE_DATABASE="genea";
  private static final File ROOT_DIR=new File("c:\\dam\\data\\genea\\xml");

  private void doIt()
  {
    GeneaDataSource source=GeneaDataSource.getInstance(SOURCE_DATABASE);
    GeneaXmlDataSource target=GeneaXmlDataSource.getInstance(ROOT_DIR);
    handlePlaces(source,target);
    handleActs(source,target);
  }

  private void handlePlaces(GeneaDataSource source, GeneaXmlDataSource target)
  {
    ObjectsManager<Place> mgr=source.getManager(Place.class);
    List<Place> objects=mgr.loadAll();
    ObjectsManager<Place> targetMgr=target.getManager(Place.class);
    for(Place object : objects)
    {
      targetMgr.create(object);
    }
    ObjectXmlDriver<Place> driver=(ObjectXmlDriver<Place>)targetMgr.getDriver();
    driver.saveAll(targetMgr.getCache().getAll());
  }

  private void handleActs(GeneaDataSource source, GeneaXmlDataSource target)
  {
    ObjectsManager<Act> mgr=source.getManager(Act.class);
    List<Act> objects=mgr.loadAll();
    ObjectsManager<Act> targetMgr=target.getManager(Act.class);
    for(Act act : objects)
    {
      targetMgr.create(act);
    }
    ObjectXmlDriver<Act> driver=(ObjectXmlDriver<Act>)targetMgr.getDriver();
    driver.saveAll(targetMgr.getCache().getAll());
  }

  /**
   * Main method for this tool.
   * @param args Not used.
   */
  public static void main(String[] args)
  {
    new MainDataTransfer().doIt();
  }
}
