package delta.genea.xml;

import java.io.File;
import java.util.Date;
import java.util.List;

import delta.common.framework.objects.data.ObjectsManager;
import delta.common.framework.objects.xml.ObjectXmlDriver;
import delta.genea.data.Act;
import delta.genea.data.ActType;
import delta.genea.data.sources.GeneaDataSource;

/**
 * Test for XML sources.
 * @author DAM
 */
public class MainTestXmlSource
{
  private static final File ROOT_DIR=new File("data\\xml\\genea");

  private void createActType(ObjectsManager<ActType> actTypesMgr, Long id, String type)
  {
    ActType actType=new ActType(id);
    actType.setType(type);
    actTypesMgr.create(actType);
  }

  private void doIt()
  {
    testLoadActs();
  }

  void testWriteActTypes()
  {
    GeneaDataSource source=GeneaDataSource.getInstance(ROOT_DIR);
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

  private void testLoadActs()
  {
    GeneaDataSource source=GeneaDataSource.getInstance(ROOT_DIR);
    ObjectsManager<Act> actsMgr=source.getManager(Act.class);
    List<Act> acts=actsMgr.loadAll();
    System.out.println("Nb acts: "+acts.size());
    for(Act act : acts)
    {
      Long pk=act.getPrimaryKey();
      ActType type=act.getActType();
      Long date=act.getDate();
      Date d=(date!=null)?new Date(date.longValue()):null;
      System.out.println("ID="+pk+" => type="+type+", date="+d);
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    new MainTestXmlSource().doIt();
  }
}
