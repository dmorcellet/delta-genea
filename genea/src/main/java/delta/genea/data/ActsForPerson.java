package delta.genea.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import delta.common.framework.objects.data.DataObject;
import delta.common.framework.objects.data.ObjectsSource;
import delta.genea.data.comparators.ActDateComparator;

/**
 * Gathers all acts for a single person.
 * @author DAM
 */
public class ActsForPerson
{
  private ObjectsSource _dataSource;
  private Person _rootPerson;
  private Act _birthAct;
  private Act _deathAct;
  private List<Union> _unions;
  private List<Act> _unionActs;
  private List<Act> _weddingContractActs;
  private List<Act> _otherActs;

  /**
   * Constructor.
   * @param dataSource Data source.
   * @param rootPerson Targeted person.
   */
  public ActsForPerson(ObjectsSource dataSource, Person rootPerson)
  {
    _dataSource=dataSource;
    _rootPerson=rootPerson;
  }

  /**
   * Load data for the managed person.
   * @return <code>true</code> if it succeeded, <code>false</code> otherwise.
   */
  public boolean build()
  {
    if (_rootPerson==null)
    {
      return false;
    }
    Long key=_rootPerson.getPrimaryKey();
    if (key==null)
    {
      return false;
    }

    // Load unions
    _unions=_dataSource.loadRelation(Union.class,Union.UNIONS_RELATION,key);
    if ((_unions!=null)&&(!_unions.isEmpty()))
    {
      for(Union u : _unions)
      {
        u.getMan();
        u.getWoman();
        u.getPlace();
        u.getWeddingContract();
      }
    }
    List<Act> acts=_dataSource.loadRelation(Act.class,Act.MAIN_ACTS_RELATION,key);
    _otherActs=_dataSource.loadRelation(Act.class,Act.OTHER_ACTS_RELATION,key);

    _unionActs=new ArrayList<Act>();
    _weddingContractActs=new ArrayList<Act>();
    for(int i=0;i<_unions.size();i++)
    {
      _unionActs.add(null);
      _weddingContractActs.add(null);
    }
    for(Iterator<Act> it=acts.iterator();it.hasNext();)
    {
      Act current=it.next();
      current.getP1();
      current.getP2();
      ActType type=current.getActType();
      if (type!=null) {
        if (type.isBirthAct())
        {
          _birthAct=current;
          it.remove();
        }
        else if (type.isDeathAct())
        {
          _deathAct=current;
          it.remove();
        }
        else if (DataObject.keysAreEqual(current.getActTypeKey(),Long.valueOf(ActType.UNION)))
        {
          int index=0;
          for(Union currentUnion : _unions)
          {
            if ((DataObject.keysAreEqual(currentUnion.getManKey(),current.getP1Key())) &&
                (DataObject.keysAreEqual(currentUnion.getWomanKey(),current.getP2Key())))
            {
              _unionActs.set(index,current);
              it.remove();
              break;
            }
            index++;
          }
        }
        else if (DataObject.keysAreEqual(current.getActTypeKey(),Long.valueOf(ActType.WEDDING_CONTRACT)))
        {
          int index=0;
          for(Union currentUnion : _unions)
          {
            if ((DataObject.keysAreEqual(currentUnion.getManKey(),current.getP1Key())) &&
                (DataObject.keysAreEqual(currentUnion.getWomanKey(),current.getP2Key())))
            {
              _weddingContractActs.set(index,current);
              it.remove();
              break;
            }
            index++;
          }
        }
      }
    }
    _otherActs.addAll(acts);
    Collections.sort(_otherActs,new ActDateComparator());
    return true;
  }

  /**
   * Get the birth act.
   * @return an act or <code>null</code>.
   */
  public Act getBirthAct()
  {
    return _birthAct;
  }

  /**
   * Get the death act.
   * @return an act or <code>null</code>.
   */
  public Act getDeathAct()
  {
    return _deathAct;
  }

  /**
   * Get all the unions for the managed person.
   * @return a possibly empty list of unions.
   */
  public List<Union> getUnions()
  {
    return _unions;
  }

  /**
   * Get a list of all union acts for this person.
   * The size of the returned list is the number of unions for
   * the managed person.
   * <code>null</code> items indicate unions with no associated union act.
   * @return a list of union acts.
   */
  public List<Act> getUnionActs()
  {
    return _unionActs;
  }

  /**
   * Get a list of all wedding contract acts for this person.
   * The size of the returned list is the number of unions for
   * the managed person.
   * <code>null</code> items indicate unions with no associated
   * wedding contract act.
   * @return a list of union acts.
   */
  public List<Act> getWeddingContractActs()
  {
    return _weddingContractActs;
  }

  /**
   * Get all other acts for the managed person
   * (acts not related to birth, union or death).
   * @return A possibly empty list of acts.
   */
  public List<Act> getOtherActs()
  {
    return _otherActs;
  }

  /**
   * Get the (first) act of union with person designated by <code>otherKey</code>.
   * @param otherKey Primary of the 'other' person in the targeted union.
   * @return An act or <code>null</code> if not found.
   */
  public Act getActOfUnionWith(Long otherKey)
  {
    if (otherKey==null)
    {
      return null;
    }
    for(Act act : _unionActs)
    {
      if (act!=null)
      {
        Long a1=act.getP1Key();
        if (a1!=null)
        {
          if (DataObject.keysAreEqual(a1,otherKey))
          {
            return act;
          }
        }
        Long a2=act.getP2Key();
        if (a2!=null)
        {
          if (DataObject.keysAreEqual(a2,otherKey))
          {
            return act;
          }
        }
      }
    }
    return null;
  }

  /**
   * Get the (first) act of wedding contract with person designated by <code>otherKey</code>.
   * @param otherKey Primary of the 'other' person in the targeted union.
   * @return A wedding contract act or <code>null</code> if not found.
   */
  public Act getActOfWeddingContractWith(Long otherKey)
  {
    if (otherKey==null)
    {
      return null;
    }
    int i=0;
    for(Act act : _weddingContractActs)
    {
      if (act!=null)
      {
        Long p1=act.getP1Key();
        Long p2=act.getP1Key();
        if ((DataObject.keysAreEqual(p1,otherKey)) || (DataObject.keysAreEqual(p2,otherKey)))
        {
          return _weddingContractActs.get(i);
        }
      }
      i++;
    }
    return null;
  }

  /**
   * Get the (first) union with person designated by <code>otherKey</code>.
   * @param otherKey Primary of the 'other' person in the targeted union.
   * @return A union or <code>null</code> if not found.
   */
  public Union getUnionWith(Long otherKey)
  {
    if (otherKey==null)
    {
      return null;
    }
    for(Union union : _unions)
    {
      if (union!=null)
      {
        if (((union.getManKey()!=null) && (union.getManKey().longValue()==otherKey.longValue())) ||
            ((union.getWomanKey()!=null) && (union.getWomanKey().longValue()==otherKey.longValue())))
        {
          return union;
        }
      }
    }
    return null;
  }

  /**
   * Get all the acts for the managed person.
   * @return A possibly empty list of acts.
   */
  public List<Act> getAllActs()
  {
    ArrayList<Act> acts=new ArrayList<Act>();
    if (_birthAct!=null)
    {
      acts.add(_birthAct);
    }
    if (_deathAct!=null)
    {
      acts.add(_deathAct);
    }
    for(Act current : _unionActs)
    {
      if (current!=null)
      {
        acts.add(current);
      }
    }
    acts.addAll(_otherActs);
    return acts;
  }
}
