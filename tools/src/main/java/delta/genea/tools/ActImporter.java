package delta.genea.tools;

import java.io.File;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.framework.objects.data.DataObject;
import delta.common.framework.objects.data.DataProxy;
import delta.genea.data.Act;
import delta.genea.data.ActType;
import delta.genea.data.ActsForPerson;
import delta.genea.data.Person;
import delta.genea.data.Place;
import delta.genea.data.Union;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.data.trees.AncestorsTree;
import delta.genea.time.FrenchRevolutionCalendar;
import delta.genea.time.GregorianDate;

/**
 * Import some act files in a database.
 * @author DAM
 */
public class ActImporter
{
  private static final Logger LOGGER=LoggerFactory.getLogger(ActImporter.class);

  private static final String BAD_ACT_TYPE="Bad actType: {}!={}";
  private static final String BAD_P1="Bad P1: {}!={}";

  private File _root;
  private AncestorsTree _tree;
  private GeneaDataSource _dataSource;
  private HashMap<String,Act> _map;

  private static final GregorianDate CHANGE_DATE=FrenchRevolutionCalendar.FIRST_DAY;

  /**
   * Constructor.
   * @param from Input directory.
   */
  private ActImporter(File from)
  {
    _root=from;
  }

  private void init(Long id)
  {
    _map=new HashMap<String,Act>();
    try
    {
      _dataSource=GeneaDataSource.getInstance("genea_ninie");
      DataProxy<Person> pp=_dataSource.buildProxy(Person.class,id);
      Person moi=pp.getDataObject();
      _tree=new AncestorsTree(moi,1000);
      _tree.build();
    }
    catch (Exception e)
    {
      LOGGER.error("Error!",e);
    }
  }

  private void handleFile(File fileName)
  {
    LOGGER.info("Handling file [{}]",fileName);
    String name=fileName.getName();
    ActPartId id=ActPartId.build(name);
    if (id==null)
    {
      return;
    }
    LOGGER.debug("Act: {}",id);

    long sosa1=id.getSosa1();
    Person p1=null;
    Person p2=null;
    if (sosa1!=0)
    {
      p1=_tree.getSosa(sosa1);
      if (p1==null)
      {
        LOGGER.error("Pas de sosa {}",Long.valueOf(sosa1));
        return;
      }
    }
    long sosa2=id.getSosa2();
    if (sosa2!=0)
    {
      p2=_tree.getSosa(sosa2);
      if (p2==null)
      {
        LOGGER.error("Pas de sosa {}",Long.valueOf(sosa2));
        return;
      }
    }

    ActsForPerson acts=new ActsForPerson(_dataSource.getObjectsSource(),p1);
    acts.build();
    Act act=null;
    String newName=id.getName();
    long actType=id.getActType();
    int pageIndex=id.getPageIndex();
    if (actType==ActType.BIRTH)
    {
      act=handleBirth(newName,acts,p1,sosa1,pageIndex);
    }
    else if (actType==ActType.DEATH)
    {
      act=handleDeath(newName,acts,p1,sosa1,pageIndex);
    }
    else if (actType==ActType.UNION)
    {
      if ((p1!=null) && (p2!=null))
      {
        act=handleUnion(newName,acts,p1,sosa1,p2,sosa2,pageIndex);
      }
    }
    if (act!=null)
    {
      _map.put(newName,act);
    }
    String newFileName=newName;
    if (pageIndex>1) newFileName=newFileName+"-"+pageIndex;
    newFileName=newFileName+".jpg";
    boolean same=(newFileName.equals(fileName.getName()));
    if (!same)
    {
      LOGGER.info("Renommage : {} to {}",fileName.getName(),newFileName);
      boolean ok=fileName.renameTo(new File(_root,newFileName));
      if (!ok)
      {
        LOGGER.error("Erreur renommage : {} to {}",fileName.getName(),newFileName);
      }
    }
  }

  private Act handleBirth(final String newName, ActsForPerson acts, final Person p1, long sosa1, int pageIndex)
  {
    long actType=ActType.BIRTH;
    Act act=_map.get(newName);
    if (act==null)
    {
      Long date=p1.getBirthDate();
      if (date==null)
      {
        LOGGER.warn("Date is null for birth of sosa {}",Long.valueOf(sosa1));
      }
      else
      {
        if (new GregorianDate(date).isBefore(CHANGE_DATE))
        {
          actType=ActType.BAPTEM;
        }
      }
      Place place=p1.getBirthPlace();
      act=acts.getBirthAct();
      if (act==null)
      {
        act=new Act(null);
        act.setActTypeProxy(_dataSource.buildProxy(ActType.class,Long.valueOf(actType)));
        act.setDate(date);
        if (place!=null)
        {
          act.setPlaceProxy(_dataSource.buildProxy(Place.class,place.getPrimaryKey()));
        }
        act.setNbFiles(pageIndex);
        act.setTraite(false);
        act.setP1Proxy(_dataSource.buildProxy(Person.class,p1.getPrimaryKey()));
      }
      else
      {
        if (!DataObject.keysAreEqual(act.getP1Key(),p1.getPrimaryKey()))
        {
          LOGGER.warn(BAD_P1,act.getP1Key(),p1.getPrimaryKey());
        }
        if (!DataObject.keysAreEqual(Long.valueOf(actType),act.getActTypeKey()))
        {
          LOGGER.warn(BAD_ACT_TYPE,Long.valueOf(actType),act.getActType());
        }
      }
      act.setPath(newName);
    }
    if (pageIndex>act.getNbFiles())
    {
      act.setNbFiles(pageIndex);
    }
    return act;
  }

  private Act handleDeath(final String newName, ActsForPerson acts, final Person p1, long sosa1, int pageIndex)
  {
    long actType=ActType.DEATH;
    Act act=_map.get(newName);
    if (act==null)
    {
      Long date=p1.getDeathDate();
      if (date==null)
      {
        LOGGER.warn("Date is null for death of sosa {}",Long.valueOf(sosa1));
      }
      if (new GregorianDate(date).isBefore(CHANGE_DATE))
      {
        actType=ActType.BURIAL;
      }
      Place place=p1.getDeathPlace();
      act=acts.getDeathAct();
      if (act==null)
      {
        act=new Act(null);
        act.setActTypeProxy(_dataSource.buildProxy(ActType.class,Long.valueOf(actType)));
        act.setDate(date);
        if (place!=null)
        {
          act.setPlaceProxy(_dataSource.buildProxy(Place.class,place.getPrimaryKey()));
        }
        act.setNbFiles(pageIndex);
        act.setP1Proxy(_dataSource.buildProxy(Person.class,p1.getPrimaryKey()));
      }
      else
      {
        Long p1Key=act.getP1Key();
        if (!DataObject.keysAreEqual(p1Key,p1.getPrimaryKey()))
        {
          LOGGER.warn(BAD_P1,p1Key,p1.getPrimaryKey());
        }
        if (!DataObject.keysAreEqual(Long.valueOf(actType),act.getActTypeKey()))
        {
          LOGGER.warn(BAD_ACT_TYPE,Long.valueOf(actType),act.getActType());
        }
      }
      act.setPath(newName);
    }
    if (pageIndex>act.getNbFiles())
    {
      act.setNbFiles(pageIndex);
    }
    return act;
  }

  private Act handleUnion(String newName, ActsForPerson acts, Person p1, long sosa1, Person p2, long sosa2, int pageIndex)
  {
    Act act=_map.get(newName);
    if (act==null)
    {
      Union u=acts.getUnionWith(p2.getPrimaryKey());
      Long date=u.getDate();
      if (date==null)
      {
        LOGGER.warn("Date is null for union of sosas {}/{}",Long.valueOf(sosa1),Long.valueOf(sosa2));
      }
      Place place=u.getPlace();
      act=acts.getActOfUnionWith(p2.getPrimaryKey());
      if (act==null)
      {
        act=new Act(null);
        act.setActTypeProxy(_dataSource.buildProxy(ActType.class,Long.valueOf(ActType.UNION)));
        act.setDate(date);
        if (place!=null)
        {
          act.setPlaceProxy(_dataSource.buildProxy(Place.class,place.getPrimaryKey()));
        }
        act.setNbFiles(pageIndex);
        act.setP1Proxy(_dataSource.buildProxy(Person.class,p1.getPrimaryKey()));
        act.setP2Proxy(_dataSource.buildProxy(Person.class,p2.getPrimaryKey()));
      }
      else
      {
        Long p1Key=act.getP1Key();
        if (DataObject.keysAreEqual(p1Key,p1.getPrimaryKey()))
        {
          LOGGER.warn(BAD_P1,p1Key,p1.getPrimaryKey());
        }
        Long p2Key=act.getP2Key();
        if (DataObject.keysAreEqual(p2Key,p2.getPrimaryKey()))
        {
          LOGGER.warn("Bad P2: {}!={}",p2Key,p2.getPrimaryKey());
        }
        if (!DataObject.keysAreEqual(Long.valueOf(ActType.UNION),act.getActTypeKey()))
        {
          LOGGER.warn(BAD_ACT_TYPE,Long.valueOf(ActType.UNION),act.getActType());
        }
      }
      act.setPath(newName);
    }
    if ((act!=null) && (pageIndex>act.getNbFiles())) 
    {
      act.setNbFiles(pageIndex);
    }
    return act;
  }

  private void doIt()
  {
    init(Long.valueOf(3));
    File[] files=_root.listFiles();
    for(int i=0;i<files.length;i++)
    {
      handleFile(files[i]);
    }
    LOGGER.info("Loaded {} acts.",Integer.valueOf(_map.size()));

    for(Act act : _map.values())
    {
      if (act.getPrimaryKey()==null)
      {
        _dataSource.create(Act.class,act);
      }
      else
      {
        _dataSource.update(Act.class,act);
      }
    }
  }

  /**
   * Main method of this tool.
   * @param args Not used.
   */
  public static void main(String[] args)
  {
    File from=new File(args[0]);
    new ActImporter(from).doIt();
  }
}
