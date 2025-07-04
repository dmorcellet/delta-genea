package delta.genea.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import delta.common.framework.objects.xml.ObjectXmlDriver;
import delta.genea.data.Person;
import delta.genea.data.Union;
import delta.genea.data.comparators.UnionOrderComparator;

/**
 * XML driver for unions.
 * @author DAM
 */
public class UnionXMLDriver extends ObjectXmlDriver<Union>
{
  @Override
  public List<Long> getRelatedObjectIDs(String relationName, Long primaryKey)
  {
    List<Long> ret=new ArrayList<Long>();
    if (relationName.equals(Union.UNIONS_RELATION))
    {
      ret=getFromPerson(primaryKey.longValue());
    }
    return ret;
  }

  /**
   * Get the identifiers of the children of the person identified
   * by <code>primaryKey</code>.
   * @param primaryKey Identifier of the targeted person.
   * @return A list of person identifiers.
   */
  public List<Long> getFromPerson(long primaryKey)
  {
    boolean isMan=true;
    List<Union> unions=new ArrayList<Union>();
    for(Union union : _objectsMgr.getCache().getAll())
    {
      // Man
      Long manKey=union.getManKey();
      if ((manKey!=null) && (manKey.longValue()==primaryKey))
      {
        unions.add(union);
        continue;
      }
      // Woman
      Long womanKey=union.getWomanKey();
      if ((womanKey!=null) && (womanKey.longValue()==primaryKey))
      {
        unions.add(union);
        isMan=false;
      }
    }
    List<Long> ret=new ArrayList<Long>();
    if (!unions.isEmpty())
    {
      Collections.sort(unions,new UnionOrderComparator(isMan));
      for(Union union : unions)
      {
        ret.add(union.getPrimaryKey());
      }
    }
    return ret;
  }

  @Override
  public List<Long> getObjectIDsSet(String setID, Object[] parameters)
  {
    List<Long> ret=new ArrayList<Long>();
    if (setID.equals(Union.NAME_AND_PLACE_SET))
    {
      String name=(String)parameters[0];
      Long placeKey=(Long)parameters[1];
      ret=getByNameAndPlace(name,placeKey);
    }
    return ret;
  }

  /**
   * Get the identifiers of unions where the last name of the man or the woman
   * is <code>name</code> and the place is <code>placeKey</code>.
   * @param name Last name of man or woman (<code>null</code> for no such filter).
   * @param placeKey Identifier of the targeted place (<code>null</code> for no such filter).
   * @return A list of union identifiers.
   */
  public List<Long> getByNameAndPlace(String name, Long placeKey)
  {
    ArrayList<Long> ret=new ArrayList<Long>();
    Pattern pattern=null;
    if ((name==null) || (name.length()==0) || ("%".equals(name)))
    {
      name=null;
    }
    else if (name.indexOf('%')!=-1)
    {
      pattern=Pattern.compile(name.replace('%','*'));
    }
    for(Union union : _objectsMgr.getCache().getAll())
    {
      if (useUnion(union,name,pattern,placeKey))
      {
        ret.add(union.getPrimaryKey());
      }
    }
    return ret;
  }

  private boolean useUnion(Union union, String name, Pattern pattern, Long placeKey)
  {
    // Check place
    if (placeKey!=null)
    {
      Long unionPlaceKey=union.getPlaceKey();
      if ((unionPlaceKey==null) || (unionPlaceKey.longValue()!=placeKey.longValue()))
      {
        return false;
      }
    }
    // Check name
    if (name!=null)
    {
      Person man=union.getMan();
      if ((man==null) || (!checkPerson(man,pattern,name)))
      {
        Person woman=union.getWoman();
        if ((woman==null) || (!checkPerson(woman,pattern,name)))
        {
          return false;
        }
      }
    }
    return true;
  }

  private boolean checkPerson(Person person, Pattern pattern, String name)
  {
    String lastName=person.getLastName();
    if (lastName==null)
    {
      return false;
    }
    if (pattern!=null)
    {
      return (pattern.matcher(lastName).matches());
    }
    return (lastName.equals(name));
  }
}
