package delta.genea.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import delta.common.framework.objects.data.DataObject;
import delta.common.framework.objects.data.ObjectSource;
import delta.genea.data.sources.GeneaDataSource;

/**
 * @author DAM
 */
public class ActsForPerson
{
  private GeneaDataSource _dataSource;
  private Person _rootPerson;
  private Act _birthAct;
  private Act _deathAct;
  private List<Union> _unions;
  private List<Act> _unionActs;
  private List<Act> _weddingContractActs;
  private List<Act> _otherActs;

  public ActsForPerson(GeneaDataSource dataSource, Person rootPerson)
  {
    _dataSource=dataSource;
    _rootPerson=rootPerson;
  }

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
    ObjectSource<Act> actsSource=_dataSource.getActDataSource();
    ObjectSource<Union> unionsSource=_dataSource.getUnionDataSource();

    // Load unions
    _unions=unionsSource.loadRelation(Union.UNIONS_RELATION,key);
    List<Act> acts=actsSource.loadRelation(Act.MAIN_ACTS_RELATION,key);
    _otherActs=actsSource.loadRelation(Act.OTHER_ACTS_RELATION,key);

    Act current;
    _unionActs=new ArrayList<Act>();
    _weddingContractActs=new ArrayList<Act>();
    for(int i=0;i<_unions.size();i++)
    {
      _unionActs.add(null);
      _weddingContractActs.add(null);
    }
    for(Iterator<Act> it=acts.iterator();it.hasNext();)
    {
      current=it.next();
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
        else if (current.getActTypeKey()==ActType.UNION)
        {
          int index=0;
          Union currentUnion;
          for(Iterator<Union> it2=_unions.iterator();it2.hasNext();)
          {
            currentUnion=it2.next();
            if ((DataObject.keysAreEqual(currentUnion.getManKey(),current.getP1Key())) ||
                (DataObject.keysAreEqual(currentUnion.getWomanKey(),current.getP2Key())))
            {
              _unionActs.set(index,current);
              it.remove();
              break;
            }
            index++;
          }
        }
        else if (current.getActTypeKey()==ActType.WEDDING_CONTRACT)
        {
          int index=0;
          Union currentUnion;
          for(Iterator<Union> it2=_unions.iterator();it2.hasNext();)
          {
            currentUnion=it2.next();
            if ((DataObject.keysAreEqual(currentUnion.getManKey(),current.getP1Key())) ||
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
    return true;
  }

  public Act getBirthAct()
  {
    return _birthAct;
  }

  public Act getDeathAct()
  {
    return _deathAct;
  }

  public List<Union> getUnions()
  {
    return _unions;
  }

  public List<Act> getUnionActs()
  {
    return _unionActs;
  }

  public List<Act> getWeddingContractActs()
  {
    return _weddingContractActs;
  }

  public List<Act> getOtherActs()
  {
    return _otherActs;
  }

  public Act getActOfUnionWith(Long otherKey)
  {
    if (otherKey==null)
    {
      return null;
    }
    Act act;
    for(Iterator<Act> it=_unionActs.iterator();it.hasNext();)
    {
      act=it.next();
      if (act!=null)
      {
        if (act.getP1Key()!=null)
        {
          long key=act.getP1Key().longValue();
          if (key==otherKey.longValue())
          {
            return act;
          }
        }
        if (act.getP2Key()!=null)
        {
          long key=act.getP2Key().longValue();
          if (key==otherKey.longValue())
          {
            return act;
          }
        }
      }
    }
    return null;
  }

  public Act getActOfWeddingContractWith(long otherKey)
  {
    if (otherKey==0)
    {
      return null;
    }
    Act act;
    int i=0;
    for(Iterator<Act> it=_weddingContractActs.iterator();it.hasNext();)
    {
      act=it.next();
      if (act!=null)
      {
        if ((act.getP1Key()==otherKey) || (act.getP2Key()==otherKey))
        {
          return _weddingContractActs.get(i);
        }
      }
      i++;
    }
    return null;
  }

  public Union getUnionWith(Long otherKey)
  {
    if (otherKey==null)
    {
      return null;
    }
    Union union;
    for(Iterator<Union> it=_unions.iterator();it.hasNext();)
    {
      union=it.next();
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
    Act current;
    for(Iterator<Act> it=_unionActs.iterator();it.hasNext();)
    {
      current=it.next();
      if (current!=null)
      {
        acts.add(current);
      }
    }
    acts.addAll(_otherActs);
    return acts;
  }
}
