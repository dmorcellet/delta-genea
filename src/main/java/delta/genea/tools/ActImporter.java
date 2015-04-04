package delta.genea.tools;

import java.io.File;
import java.util.HashMap;

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
      e.printStackTrace();
    }
  }

  private void handleFile(File fileName)
  {
    System.out.println("Handling file ["+fileName+"]");
    Long actType=ActType.BIRTH;
    String name=fileName.getName();
    //String newName="j_";
    //String newName="ninie/";
    String newName="";
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
        return;
      name=name.substring(1);
      {
        int index=0; int n=name.length();
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
      int index=0; int n=name.length();
      while (index<n)
      {
        char c=name.charAt(index);
        if (Character.isDigit(c))
          pageIndex=(pageIndex*10)+(c-'0');
        else break;
        index++;
      }
      //name=name.substring(index);
    }
    else
    {
      pageIndex=1;
    }
    //System.out.println("Act "+actType+", sosa1="+sosa1+",sosa2="+sosa2+",pageIndex="+pageIndex);

    Person p1=null;
    Person p2=null;
    if (sosa1!=0)
    {
      p1=_tree.getSosa(sosa1);
      if (p1==null)
      {
        System.err.println("Erreur : pas de sosa "+sosa1);
      }
    }
    if (sosa2!=0)
    {
      p2=_tree.getSosa(sosa2);
      if (p2==null)
      {
        System.err.println("Erreur : pas de sosa "+sosa2);
      }
    }

    ActsForPerson acts=new ActsForPerson(_dataSource,p1);
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
          System.err.println("Date is null for birth of sosa "+sosa1);
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
          act.setActTypeProxy(_dataSource.buildProxy(ActType.class,actType));
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
            System.err.println("Bad P1 : "+act.getP1Key()+"!="+p1.getPrimaryKey());
          }
          if (!DataObject.keysAreEqual(actType,act.getActTypeKey()))
          {
            System.err.println("Bad actType : "+actType+"!="+act.getActType());
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
        if (date==null) System.err.println("Date is null for death of sosa "+sosa1);
        if (new GregorianDate(date).isBefore(CHANGE_DATE))
        {
          actType=ActType.BURIAL;
        }
        place=p1.getDeathPlace();
        act=acts.getDeathAct();
        if (act==null)
        {
          act=new Act(null);
          act.setActTypeProxy(_dataSource.buildProxy(ActType.class,actType));
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
            System.err.println("Bad P1 : "+p1Key+"!="+p1.getPrimaryKey());
          }
          if (actType!=act.getActTypeKey())
          {
            System.err.println("Bad actType : "+actType+"!="+act.getActType());
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
          if (date==null) System.err.println("Date is null for union of sosas "+sosa1+"/"+sosa2);
          place=u.getPlace();
          act=acts.getActOfUnionWith(p2.getPrimaryKey());
          if (act==null)
          {
            act=new Act(null);
            act.setActTypeProxy(_dataSource.buildProxy(ActType.class,actType));
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
              System.err.println("Bad P1 : "+p1Key+"!="+p1.getPrimaryKey());
            }
            Long p2Key=act.getP2Key();
            if (DataObject.keysAreEqual(p2Key,p2.getPrimaryKey()))
            {
              System.err.println("Bad P2 : "+p2Key+"!="+p2.getPrimaryKey());
            }
            if (actType!=act.getActTypeKey())
            {
              System.err.println("Bad actType : "+actType+"!="+act.getActType());
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
    //System.out.println("Renaming "+fileName.getName()+" to "+newFileName);
    boolean same=(newFileName.equals(fileName.getName()));
    if (!same)
    {
      System.out.println("Renommage : "+fileName.getName()+" to "+newFileName);
      boolean ok=fileName.renameTo(new File(_root,newFileName));
      if (!ok)
      {
        System.err.println("Erreur renommage : "+fileName.getName()+" to "+newFileName);
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
    //System.out.println(_map.size());

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
    /*
    double log10=Math.log(sosa);
    double log2=Math.log(2);
    double log=log10/log2;
    double same=Math.pow(2,Math.floor(log));
    long tmp=sosa-(long)(same);
    return tmp;
    */
  }
}
