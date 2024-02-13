package delta.genea.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import delta.common.framework.objects.xml.ObjectXmlDriver;
import delta.genea.data.Person;

/**
 * @author dm
 */
public class PersonXMLDriver extends ObjectXmlDriver<Person>
{
  @Override
  public List<Long> getObjectIDsSet(String setID, Object[] parameters)
  {
    List<Long> ret=new ArrayList<Long>();
    if (setID.equals(Person.NAME_SET))
    {
      String name=(String)parameters[0];
      ret=getByName(name);
    }
    return ret;
  }

  private List<Long> getByName(String name)
  {
    List<Long> ret=new ArrayList<Long>();
    List<Person> persons=_objectsMgr.getCache().getAll();
    if (name.indexOf('%')!=-1)
    {
      Pattern pattern=Pattern.compile(name.replace('%','*'));
      for(Person person : persons)
      {
        String lastName=person.getLastName();
        if (lastName!=null)
        {
          if (pattern.matcher(lastName).matches())
          {
            ret.add(person.getPrimaryKey());
          }
        }
      }
    }
    else
    {
      for(Person person : persons)
      {
        if (name.equals(person.getLastName()))
        {
          ret.add(person.getPrimaryKey());
        }
      }
    }
    return ret;
  }

  @Override
  public List<Long> getRelatedObjectIDs(String relationName, Long primaryKey)
  {
    if (primaryKey==null)
    {
      return null;
    }
    List<Long> ret=null;
    if (relationName.equals(Person.CHILDREN_RELATION))
    {
      ret=getChildren(primaryKey.longValue());
    }
    else if (relationName.equals(Person.GOD_CHILDREN_RELATION))
    {
      ret=getGodChildren(primaryKey.longValue());
    }
    else if (relationName.equals(Person.COUSINS_RELATION))
    {
      ret=getCousins(primaryKey.longValue());
    }
    return ret;
  }

  /**
   * Get the identifiers of the children of the person identified
   * by <code>primaryKey</code>.
   * @param primaryKey Identifier of the targeted person.
   * @return A list of person identifiers.
   */
  public List<Long> getChildren(long primaryKey)
  {
    List<Long> ret=new ArrayList<Long>();
    for(Person person : _objectsMgr.getCache().getAll())
    {
      // Father
      Long fatherKey=person.getFatherKey();
      if ((fatherKey!=null) && (fatherKey.longValue()==primaryKey))
      {
        ret.add(person.getPrimaryKey());
        continue;
      }
      // Mother
      Long motherKey=person.getMotherKey();
      if ((motherKey!=null) && (motherKey.longValue()==primaryKey))
      {
        ret.add(person.getPrimaryKey());
        continue;
      }
    }
    return ret;
  }

  /**
   * Get the identifiers of the god children of the person identified
   * by <code>primaryKey</code>.
   * @param primaryKey Identifier of the targeted person.
   * @return A list of person identifiers.
   */
  public List<Long> getGodChildren(long primaryKey)
  {
    List<Long> ret=new ArrayList<Long>();
    for(Person person : _objectsMgr.getCache().getAll())
    {
      // God-father
      Long godFatherKey=person.getGodFatherKey();
      if ((godFatherKey!=null) && (godFatherKey.longValue()==primaryKey))
      {
        ret.add(person.getPrimaryKey());
        continue;
      }
      // God-mother
      Long godMotherKey=person.getGodMotherKey();
      if ((godMotherKey!=null) && (godMotherKey.longValue()==primaryKey))
      {
        ret.add(person.getPrimaryKey());
        continue;
      }
    }
    return ret;
  }

  /**
   * Get the identifiers of the cousins of the person identified
   * by <code>primaryKey</code>.
   * @param primaryKey Identifier of the targeted person.
   * @return A list of person identifiers.
   */
  public List<Long> getCousins(long primaryKey)
  {
    return new ArrayList<Long>();
  }
}
