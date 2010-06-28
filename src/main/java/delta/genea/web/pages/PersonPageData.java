package delta.genea.web.pages;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import delta.genea.data.Act;
import delta.genea.data.ActsForPerson;
import delta.genea.data.Person;
import delta.genea.data.Picture;
import delta.genea.data.Union;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.data.trees.AncestorsTree;
import delta.genea.data.trees.AncestorsTreesRegistry;

public class PersonPageData
{
  private long _key;
  private long _deCujusKey;

  private GeneaDataSource _dataSource;
  private Person _main;
  private Person _father;
  private Person _mother;
  private Person _godFather;
  private Person _godMother;
  private List<Union> _unions;
  private List<Person> _children;
  private List<Person> _godChildren;
  private ActsForPerson _acts;
  private AncestorsTree _tree;
  private List<Picture> _pictures;

  public PersonPageData(long key, long deCujusKey)
  {
    _key=key;
    _deCujusKey=deCujusKey;
  }

  public boolean load(GeneaDataSource source)
  {
    _dataSource=source;
    _main=source.getPersonDataSource().load(_key);
    if (_main==null) return false;

    _father=_main.getFather();
    _mother=_main.getMother();
    _godFather=_main.getGodFather();
    _godMother=_main.getGodMother();

    _main.getBirthPlace();
    _main.getDeathPlace();

    _unions=source.getUnionDataSource()
        .loadRelation(Union.UNIONS_RELATION,_key);

    if ((_unions!=null)&&(_unions.size()>0))
    {
      Union u;
      for(Iterator<Union> it=_unions.iterator();it.hasNext();)
      {
        u=it.next();
        u.getMan();
        u.getWoman();
        u.getPlace();
        u.getWeddingContract();
      }
    }

    _children=source.getPersonDataSource().loadRelation(
        Person.CHILDREN_RELATION,_key);
    _godChildren=source.getPersonDataSource().loadRelation(
        Person.GOD_CHILDREN_RELATION,_key);
    _acts=new ActsForPerson(_dataSource,_main);
    _acts.build();
    List<Act> acts=_acts.getAllActs();
    Act current;
    for(Iterator<Act> it=acts.iterator();it.hasNext();)
    {
      current=it.next();
      if (current!=null)
      {
        current.getP1();
        current.getP2();
      }
    }
    AncestorsTreesRegistry registry=_dataSource.getAncestorsTreesRegistry();
    _tree=registry.getTree(_deCujusKey);
    // Pictures
    _pictures=source.getPictureDataSource().loadRelation(Picture.PICTURES_FOR_PERSON_RELATION,_key);

    return true;
  }

  public String getSosas(long key)
  {
    String ret="";

    if (_tree!=null)
    {
      List<Long> sosas=_tree.getSosas(key);
      Collections.sort(sosas);
      StringBuffer sb=new StringBuffer();
      int nb=sosas.size();
      for(int i=0;i<nb;i++)
      {
        if (i>0) sb.append(' ');
        sb.append(sosas.get(i));
      }
      ret=sb.toString();
    }
    return ret;
  }

  public Person getMain()
  {
    return _main;
  }

  public Person getFather()
  {
    return _father;
  }

  public Person getMother()
  {
    return _mother;
  }

  public Person getGodFather()
  {
    return _godFather;
  }

  public Person getGodMother()
  {
    return _godMother;
  }

  public List<Person> getChildren()
  {
    return _children;
  }

  public List<Person> getGodChildren()
  {
    return _godChildren;
  }

  public List<Union> getUnions()
  {
    return _unions;
  }

  public ActsForPerson getActs()
  {
    return _acts;
  }

  public List<Picture> getPictures()
  {
    return _pictures;
  }
}
