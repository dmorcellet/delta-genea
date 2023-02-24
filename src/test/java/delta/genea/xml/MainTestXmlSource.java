package delta.genea.xml;

import java.io.File;
import java.util.List;

import delta.common.framework.objects.data.ObjectsManager;
import delta.common.framework.objects.xml.ObjectXmlDriver;
import delta.genea.data.ActType;
import delta.genea.data.sources.GeneaXmlDataSource;

/**
 * Test for XML sources.
 * @author DAM
 */
public class MainTestXmlSource
{
  private static final File ROOT_DIR=new File("d:\\dam\\data\\genea\\xml");

  private void createActType(ObjectsManager<ActType> actTypesMgr, Long id, String type)
  {
    ActType actType=new ActType(id);
    actType.setType(type);
    actTypesMgr.create(actType);
  }

  private void doIt()
  {
    GeneaXmlDataSource source=GeneaXmlDataSource.getInstance(ROOT_DIR);
    ObjectsManager<ActType> actTypesMgr=source.getManager(ActType.class);
    List<ActType> actTypes=actTypesMgr.loadAll();
    for(ActType actType : actTypes)
    {
      System.out.println("Act type: "+actType);
    }
    createActType(actTypesMgr,ActType.BAPTEM,"Baptême");
    ObjectXmlDriver<ActType> driver=(ObjectXmlDriver<ActType>)actTypesMgr.getDriver();
    driver.saveAll(actTypesMgr.getCache().getAll());
  }

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    new MainTestXmlSource().doIt();
  }
}
