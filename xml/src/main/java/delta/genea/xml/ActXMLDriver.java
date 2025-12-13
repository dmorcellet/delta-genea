package delta.genea.xml;

import java.util.ArrayList;
import java.util.List;

import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.xml.ObjectXmlDriver;
import delta.genea.data.Act;
import delta.genea.data.Person;
import delta.genea.data.PersonInAct;

/**
 * XML driver for acts.
 * @author DAM
 */
public class ActXMLDriver extends ObjectXmlDriver<Act>
{
  /**
   * Get the identifiers of acts where one of the main person is
   * the person identified by <code>primaryKey</code>.
   * @param primaryKey Identifier of the targeted person.
   * @return A list of act identifiers.
   */
  public List<Long> getMainFromPerson(Long primaryKey)
  {
    List<Long> ret=new ArrayList<Long>();
    for(Act act : _objectsMgr.getCache().getAll())
    {
      // P1
      Long p1Key=act.getP1Key();
      if ((p1Key!=null) && (p1Key.longValue()==primaryKey.longValue()))
      {
        ret.add(act.getPrimaryKey());
        continue;
      }
      // P2
      Long p2Key=act.getP2Key();
      if ((p2Key!=null) && (p2Key.longValue()==primaryKey.longValue()))
      {
        ret.add(act.getPrimaryKey());
      }
    }
    return ret;
  }

  /**
   * Get the identifiers of acts where the person identified
   * by <code>primaryKey</code> do appear.
   * @param primaryKey Identifier of the targeted person.
   * @return A list of act identifiers.
   */
  public List<Long> getOtherFromPerson(Long primaryKey)
  {
    List<Long> ret=new ArrayList<Long>();
    for(Act act : _objectsMgr.getCache().getAll())
    {
      for(PersonInAct pia : act.getPersonsInAct())
      {
        DataProxy<Person> person=pia.getPersonProxy();
        if ((person!=null) && (primaryKey.equals(person.getPrimaryKey())))
        {
          ret.add(act.getPrimaryKey());
          break;
        }
        DataProxy<Person> refLink=pia.getLinkRefProxy();
        if ((refLink!=null) && (primaryKey.equals(refLink.getPrimaryKey())))
        {
          ret.add(act.getPrimaryKey());
          break;
        }
      }
    }
    return ret;
  }

  /**
   * Get the identifiers of acts whose place is identified
   * by <code>primaryKey</code>.
   * @param primaryKey Identifier of the targeted place.
   * @return A list of act identifiers.
   */
  public List<Long> getActsFromPlace(Long primaryKey)
  {
    List<Long> ret=new ArrayList<Long>();
    for(Act act : _objectsMgr.getCache().getAll())
    {
      Long placeKey=act.getPlaceKey();
      if (primaryKey.equals(placeKey))
      {
        ret.add(act.getPrimaryKey());
      }
    }
    // TODO Sort by type and date (as in the SQL version)
    return ret;
  }

  @Override
  public List<Long> getRelatedObjectIDs(String relationName, Long primaryKey)
  {
    List<Long> ret=null;
    if (relationName.equals(Act.MAIN_ACTS_RELATION))
    {
      ret=getMainFromPerson(primaryKey);
    }
    else if (relationName.equals(Act.OTHER_ACTS_RELATION))
    {
      ret=getOtherFromPerson(primaryKey);
    }
    else if (relationName.equals(Act.ACTS_FROM_PLACE))
    {
      ret=getActsFromPlace(primaryKey);
    }
    return ret;
  }
}
