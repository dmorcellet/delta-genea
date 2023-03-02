package delta.genea.xml;

import java.io.File;
import java.util.List;

import delta.common.framework.objects.data.ObjectsManager;
import delta.common.framework.objects.xml.ObjectXmlDriver;
import delta.genea.data.Act;
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
    handleActs(source,target);
  }

  private void handleActs(GeneaDataSource source, GeneaXmlDataSource target)
  {
    ObjectsManager<Act> actsMgr=source.getManager(Act.class);
    List<Act> acts=actsMgr.loadAll();
    ObjectsManager<Act> targetActsMgr=target.getManager(Act.class);
    for(Act act : acts)
    {
      targetActsMgr.create(act);
    }
    ObjectXmlDriver<Act> driver=(ObjectXmlDriver<Act>)targetActsMgr.getDriver();
    driver.saveAll(targetActsMgr.getCache().getAll());
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
