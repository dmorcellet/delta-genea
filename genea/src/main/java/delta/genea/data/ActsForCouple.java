package delta.genea.data;

import delta.common.framework.objects.data.ObjectsSource;

/**
 * Gathers all acts for a couple. 
 * @author DAM
 */
public class ActsForCouple
{
  private ObjectsSource _source;
  private Person _man;
  private Person _woman;
  private ActsForPerson _actsForMan;
  private ActsForPerson _actsForWoman;
  private Act _union;

  /**
   * Constructor.
   * @param source Associated source.
   * @param man Man.
   * @param woman Woman.
   */
  public ActsForCouple(ObjectsSource source, Person man, Person woman)
  {
    _source=source;
    _man=man;
    _woman=woman;
  }

  /**
   * Load data for the managed couple.
   * @return <code>true</code> if it succeeded, <code>false</code> otherwise.
   */
  public boolean build()
  {
    _actsForMan=null;
    _actsForWoman=null;
    if (_man!=null)
    {
      _actsForMan=new ActsForPerson(_source,_man);
      _actsForMan.build();
    }
    if (_woman!=null)
    {
      _actsForWoman=new ActsForPerson(_source,_woman);
      _actsForWoman.build();
    }

    // Find union
    if ((_actsForMan!=null) && (_actsForWoman!=null))
    {
      _union=_actsForMan.getActOfUnionWith(_woman.getPrimaryKey());
    }
    return true;
  }

  /**
   * Get the birth act for the man.
   * @return a birth act or <code>null</code> if not found.
   */
  public Act getBirthActForMan()
  {
    if (_actsForMan!=null)
    {
      return _actsForMan.getBirthAct();
    }
    return null;
  }

  /**
   * Get the death act for the man.
   * @return a death act or <code>null</code> if not found.
   */
  public Act getDeathActForMan()
  {
    if (_actsForMan!=null)
    {
      return _actsForMan.getDeathAct();
    }
    return null;
  }

  /**
   * Get the birth act for the woman.
   * @return a birth act or <code>null</code> if not found.
   */
  public Act getBirthActForWoman()
  {
    if (_actsForWoman!=null)
    {
      return _actsForWoman.getBirthAct();
    }
    return null;
  }

  /**
   * Get the death act for the man.
   * @return a death act or <code>null</code> if not found.
   */
  public Act getDeathActForWoman()
  {
    if (_actsForWoman!=null)
    {
      return _actsForWoman.getDeathAct();
    }
    return null;
  }

  /**
   * Get the union act for this couple.
   * @return a union act or <code>null</code> if not found.
   */
  public Act getUnionAct()
  {
    return _union;
  }
}
