package delta.genea.web.pages;

import java.util.Collections;
import java.util.List;

import delta.genea.data.Act;
import delta.genea.data.ActsForPerson;
import delta.genea.data.Person;
import delta.genea.data.Picture;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.data.trees.AncestorsTree;
import delta.genea.data.trees.AncestorsTreesRegistry;

/**
 * Gathers data needed for the 'person' page.
 * @author DAM
 */
public class PersonPageData
{
  private Long _key;
  private Long _deCujusKey;

  private GeneaDataSource _dataSource;
  private Person _main;
  private Person _father;
  private Person _mother;
  private Person _godFather;
  private Person _godMother;
  private List<Person> _children;
  private List<Person> _godChildren;
  private ActsForPerson _acts;
  private AncestorsTree _tree;
  private List<Picture> _pictures;

  /**
   * Constructor.
   * @param key Main person key.
   * @param deCujusKey Key for the 'de cujus' person.
   */
  public PersonPageData(Long key, Long deCujusKey)
  {
    _key=key;
    _deCujusKey=deCujusKey;
  }

  public boolean load(GeneaDataSource source)
  {
    _dataSource=source;
    _main=source.load(Person.class,_key);
    if (_main==null) return false;

    _father=_main.getFather();
    _mother=_main.getMother();
    _godFather=_main.getGodFather();
    _godMother=_main.getGodMother();

    _main.getBirthPlace();
    _main.getDeathPlace();

    _children=source.loadRelation(Person.class,Person.CHILDREN_RELATION,_key);
    _godChildren=source.loadRelation(Person.class,Person.GOD_CHILDREN_RELATION,_key);
    _acts=new ActsForPerson(_dataSource,_main);
    _acts.build();
    List<Act> acts=_acts.getAllActs();
    for(Act current : acts)
    {
      if (current!=null)
      {
        current.getP1();
        current.getP2();
      }
    }
    AncestorsTreesRegistry registry=_dataSource.getAncestorsTreesRegistry();
    _tree=registry.getTree(_deCujusKey);
    // Pictures
    _pictures=source.loadRelation(Picture.class,Picture.PICTURES_FOR_PERSON_RELATION,_key);

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

  public ActsForPerson getActs()
  {
    return _acts;
  }

  public List<Picture> getPictures()
  {
    return _pictures;
  }
}
