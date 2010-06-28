package delta.genea.data;

import delta.genea.data.sources.GeneaDataSource;

/**
 * @author DAM
 */
public class ActsForCouple
{
  private GeneaDataSource _dataSource;
  private Person _man;
  private Person _woman;
  private ActsForPerson _actsForMan;
  private ActsForPerson _actsForWoman;
  private Act _union;

  public ActsForCouple(GeneaDataSource dataSource, Person man, Person woman)
  {
    _dataSource=dataSource;
    _man=man;
    _woman=woman;
  }

  public boolean build()
  {
    if (_man!=null)
    {
      _actsForMan=new ActsForPerson(_dataSource,_man);
      _actsForMan.build();
    }
    if (_woman!=null)
    {
      _actsForWoman=new ActsForPerson(_dataSource,_woman);
      _actsForWoman.build();
    }

    // Find union
    if ((_actsForMan!=null) && (_actsForWoman!=null))
    {
      _union=_actsForMan.getActOfUnionWith(_woman.getPrimaryKey());
    }
    return true;
  }

  public Act getBirthActForMan()
  {
    if (_actsForMan!=null)
    {
      return _actsForMan.getBirthAct();
    }
    return null;
  }

  public Act getDeathActForMan()
  {
    if (_actsForMan!=null)
    {
      return _actsForMan.getDeathAct();
    }
    return null;
  }

  public Act getBirthActForWoan()
  {
    if (_actsForWoman!=null)
    {
      return _actsForWoman.getBirthAct();
    }
    return null;
  }

  public Act getDeathActForWoman()
  {
    if (_actsForWoman!=null)
    {
      return _actsForWoman.getDeathAct();
    }
    return null;
  }

  public Act getUnionAct()
  {
    return _union;
  }
}
