package delta.genea.xml;

import java.io.File;
import java.util.List;

import delta.common.framework.objects.data.Identifiable;
import delta.common.framework.objects.data.ObjectsManager;
import delta.common.framework.objects.xml.ObjectXmlDriver;
import delta.genea.data.Act;
import delta.genea.data.ActText;
import delta.genea.data.ActType;
import delta.genea.data.Place;
import delta.genea.data.Union;
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
    handleClass(source,target,Place.class);
    handleClass(source,target,ActType.class);
    handleClass(source,target,Act.class);
    handleClass(source,target,Union.class);
    handleClass(source,target,ActText.class);
  }

  private <E extends Identifiable<Long>> void handleClass(GeneaDataSource source, GeneaXmlDataSource target, Class<E> c)
  {
    ObjectsManager<E> mgr=source.getManager(c);
    List<E> objects=mgr.loadAll();
    ObjectsManager<E> targetMgr=target.getManager(c);
    for(E object : objects)
    {
      targetMgr.create(object);
    }
    ObjectXmlDriver<E> driver=(ObjectXmlDriver<E>)targetMgr.getDriver();
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
