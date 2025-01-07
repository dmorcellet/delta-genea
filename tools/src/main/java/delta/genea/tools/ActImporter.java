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
    long actType;
    String name=fileName.getName(); 
    String prefix=""; // or "j_" or "ninie/"
    String newName=prefix;
    if (name.endsWith(".jpg"))
    {
      name=name.substring(0,name.length()-4);
    }
    if (name.startsWith("an"))
    {
      actType=ActType.BIRTH;
    }
    else if (name.startsWith("ad"))
    {
      actType=ActType.DEATH;
    }
    else if (name.startsWith("am"))
    {
      actType=ActType.UNION;
    }
    else
    {
      return;
    }
    newName=newName+name.substring(0,2);
    name=name.substring(2);
    long sosa1=0;
    long sosa2=0;
    {
      int index=0;
      int n=name.length();
      while (index<n)
      {
        char c=name.charAt(index);
        if (Character.isDigit(c))
          sosa1=(sosa1*10)+(c-'0');
        else break;
        index++;
      }
      name=name.substring(index);
    }
    newName=newName+convertSosa(sosa1);
    if (actType==ActType.UNION)
    {
      if ((name.length()==0) || (name.charAt(0)!='-'))
      {
        return;
      }
      name=name.substring(1);
      {
        int index=0;
        int n=name.length();
        while (index<n)
        {
          char c=name.charAt(index);
          if (Character.isDigit(c))
          {
            sosa2=(sosa2*10)+(c-'0');
          }
          else
          {
            break;
          }
          index++;
        }
        name=name.substring(index);
      }
      newName=newName+"-"+convertSosa(sosa2);
    }
    int pageIndex=0;
    if ((name.length()>1) && (name.charAt(0)=='-'))
    {
      name=name.substring(1);
      int index=0;
      int n=name.length();
      while (index<n)
      {
        char c=name.charAt(index);
        if (Character.isDigit(c))
          pageIndex=(pageIndex*10)+(c-'0');
        else break;
        index++;
      }
    }
    else
    {
      pageIndex=1;
    }
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("Act {}, sosa1={},sosa2={},pageIndex={}",Long.valueOf(actType),Long.valueOf(sosa1),Long.valueOf(sosa2),Long.valueOf(pageIndex));
    }

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
    Long date=null;
    Place place=null;
    Act act=null;
    if (actType==ActType.BIRTH)
    {
      act=_map.get(newName);
      if (act==null)
      {
        date=p1.getBirthDate();
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
        place=p1.getBirthPlace();
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
      if (pageIndex>act.getNbFiles()) act.setNbFiles(pageIndex);
    }
    else if (actType==ActType.DEATH)
    {
      act=_map.get(newName);
      if (act==null)
      {
        date=p1.getDeathDate();
        if (date==null)
        {
          LOGGER.warn("Date is null for death of sosa {}",Long.valueOf(sosa1));
        }
        if (new GregorianDate(date).isBefore(CHANGE_DATE))
        {
          actType=ActType.BURIAL;
        }
        place=p1.getDeathPlace();
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
      if (pageIndex>act.getNbFiles()) act.setNbFiles(pageIndex);
    }
    else if (actType==ActType.UNION)
    {
      if ((p1!=null) && (p2!=null))
      {
        act=_map.get(newName);
        if (act==null)
        {
          Union u=acts.getUnionWith(p2.getPrimaryKey());
          date=u.getDate();
          if (date==null)
          {
            LOGGER.warn("Date is null for union of sosas {}/{}",Long.valueOf(sosa1),Long.valueOf(sosa2));
          }
          place=u.getPlace();
          act=acts.getActOfUnionWith(p2.getPrimaryKey());
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
            if (!DataObject.keysAreEqual(Long.valueOf(actType),act.getActTypeKey()))
            {
              LOGGER.warn(BAD_ACT_TYPE,Long.valueOf(actType),act.getActType());
            }
          }
          act.setPath(newName);
        }
        if ((act!=null) && (pageIndex>act.getNbFiles())) 
        {
          act.setNbFiles(pageIndex);
        }
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

  private void doIt()
  {
    _root=new File("/home/dm/tmp/actes/ninie");
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
    new ActImporter().doIt();
  }

  private long convertSosa(long sosa)
  {
    return sosa;
  }
}
