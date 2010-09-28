package delta.genea.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import delta.common.framework.objects.data.DataObject;
import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.data.ObjectSource;

/**
 * Person.
 * @author DAM
 */
public class Person extends DataObject<Person>
{
  /**
   * Relation that gives the children of a person.
   */
  public static final String CHILDREN_RELATION="CHILDREN";
  /**
   * Relation that gives the god-children of a person.
   */
  public static final String GOD_CHILDREN_RELATION="GOD_CHILDREN";
  /**
   * Relation that gives the registered cousins of a person.
   */
  public static final String COUSINS_RELATION="COUSINS";
  /**
   * Set of the persons with a given name.
   */
  public static final String NAME_SET="NAME";
  /**
   * Class name.
   */
  public static final String CLASS_NAME="PERSON";

  private String _lastName; // Nom
  private String _firstName;  // Prénoms
  private Sex _sex;
  private String _signature;
  // Birth
  private final GeneaDate _birthDate;
  private DataProxy<Place> _birthPlace;
  // Death
  private final GeneaDate _deathDate;
  private DataProxy<Place> _deathPlace;
  // Descendants ?
  private boolean _noDescendants;

  // Related persons
  private DataProxy<Person> _father;
  private DataProxy<Person> _mother;
  private DataProxy<Person> _godFather;
  private DataProxy<Person> _godMother;

  // Known occupations
  private List<OccupationForPerson> _occupations;
  // Known homes
  private List<HomeForPerson> _homes;

  // Comments
  private String _comments;

  @Override
  public String getClassName() { return CLASS_NAME; }

  /**
   * Constructor.
   * @param primaryKey Primary key.
   * @param source Attached objects source.
   */
  public Person(long primaryKey, ObjectSource<Person> source)
  {
    super(primaryKey,source);
    _sex=Sex.MALE;
    _noDescendants=false;
    _lastName="";
    _firstName="";
    _signature="";
    _birthDate=new GeneaDate();
    _deathDate=new GeneaDate();
    _comments="";
  }

  public String getLastName()
  {
    return _lastName;
  }

  public void setLastName(String lastName)
  {
    if (lastName==null)
    {
      lastName="";
    }
    _lastName=lastName;
  }

  public String getFirstname()
  {
    return _firstName;
  }

  public void setFirstname(String firstname)
  {
    if (firstname==null)
    {
      firstname="";
    }
    _firstName=firstname;
  }

  @Override
  public String getLabel()
  {
    String ret=getFullName();
    return ret;
  }

  public String getFullName()
  {
    StringBuffer sb=new StringBuffer();
    sb.append(_firstName);
    sb.append(' ');
    sb.append(_lastName);
    return sb.toString();
  }

  /**
   * Get the sex of this person.
   * @return the sex of this person.
   */
  public Sex getSex()
  {
    return _sex;
  }

  public void setSex(Sex sex)
  {
    _sex=sex;
  }

  public String getSignature()
  {
    return _signature;
  }

  public void setSignature(String signature)
  {
    if (signature==null)
    {
      signature="";
    }
    _signature=signature;
  }

  /**
   * Get the birth date of this person.
   * @return the birth date of this person.
   */
  public GeneaDate getBirthGeneaDate()
  {
    return _birthDate;
  }

  public Long getBirthDate()
  {
    return _birthDate.getDate();
  }

  public void setBirthDate(Long date, String infosDate)
  {
    _birthDate.setDate(date);
    _birthDate.setInfosDate(infosDate);
  }

  public void setBirthDate(Date date, String infosDate)
  {
    _birthDate.setDate(date);
    _birthDate.setInfosDate(infosDate);
  }

  public String getBirthInfos()
  {
    return _birthDate.getInfosDate();
  }

  /**
   * Get the proxy for the birth place of this person.
   * @return the proxy for the birth place of this person.
   */
  public DataProxy<Place> getBirthPlaceProxy()
  {
    return _birthPlace;
  }

  public void setBirthPlaceProxy(DataProxy<Place> birthPlace)
  {
    _birthPlace=birthPlace;
  }

  public Place getBirthPlace()
  {
    if (_birthPlace!=null)
    {
      return _birthPlace.getDataObject();
    }
    return null;
  }

  public Long getDeathDate()
  {
    return _deathDate.getDate();
  }

  public GeneaDate getDeathGeneaDate()
  {
    return _deathDate;
  }

  public void setDeathDate(Long date, String infosDate)
  {
    _deathDate.setDate(date);
    _deathDate.setInfosDate(infosDate);
  }

  public void setDeathDate(Date date, String infosDate)
  {
    _deathDate.setDate(date);
    _deathDate.setInfosDate(infosDate);
  }

  public String getDeathInfos()
  {
    return _deathDate.getInfosDate();
  }

  public DataProxy<Place> getDeathPlaceProxy()
  {
    return _deathPlace;
  }

  public void setDeathPlaceProxy(DataProxy<Place> deathPlace)
  {
    _deathPlace=deathPlace;
  }

  public Place getDeathPlace()
  {
    if (_deathPlace!=null)
    {
      return _deathPlace.getDataObject();
    }
    return null;
  }

  public boolean getNoDescendants()
  {
    return _noDescendants;
  }

  public void setNoDescendants(boolean noDescendants)
  {
    _noDescendants=noDescendants;
  }

  public DataProxy<Person> getFatherProxy()
  {
    return _father;
  }

  public void setFatherProxy(DataProxy<Person> father)
  {
    _father=father;
  }

  public long getFatherKey()
  {
    long ret=0;
    if (_father!=null)
    {
      ret=_father.getPrimaryKey();
    }
    return ret;
  }

  public Person getFather()
  {
    if (_father!=null)
    {
      return _father.getDataObject();
    }
    return null;
  }

  public DataProxy<Person> getMotherProxy()
  {
    return _mother;
  }

  public void setMotherProxy(DataProxy<Person> mother)
  {
    _mother=mother;
  }

  public long getMotherKey()
  {
    long ret=0;
    if (_mother!=null)
    {
      ret=_mother.getPrimaryKey();
    }
    return ret;
  }

  public Person getMother()
  {
    if(_mother!=null)
    {
      return _mother.getDataObject();
    }
    return null;
  }

  public DataProxy<Person> getGodFatherProxy()
  {
    return _godFather;
  }

  public void setGodFatherProxy(DataProxy<Person> godFather)
  {
    _godFather=godFather;
  }

  public Person getGodFather()
  {
    if(_godFather!=null)
    {
      return _godFather.getDataObject();
    }
    return null;
  }

  public DataProxy<Person> getGodMotherProxy()
  {
    return _godMother;
  }

  public void setGodMotherProxy(DataProxy<Person> godMother)
  {
    _godMother=godMother;
  }

  public Person getGodMother()
  {
    if(_godMother!=null)
    {
      return _godMother.getDataObject();
    }
    return null;
  }

  public List<OccupationForPerson> getOccupations()
  {
    return _occupations;
  }

  public void setOccupations(List<OccupationForPerson> list)
  {
    _occupations=list;
  }

  public void addOccupation(OccupationForPerson o)
  {
    if (_occupations==null)
    {
      _occupations=new ArrayList<OccupationForPerson>();
    }
    _occupations.add(o);
  }

  public List<HomeForPerson> getHomes()
  {
    return _homes;
  }

  public void setHomes(List<HomeForPerson> list)
  {
    _homes=list;
  }

  public void addHome(HomeForPerson o)
  {
    if (_homes==null)
    {
      _homes=new ArrayList<HomeForPerson>();
    }
    _homes.add(o);
  }

  public String getComments()
  {
    return _comments;
  }

  public void setComments(String comments)
  {
    if (comments==null)
    {
      comments="";
    }
    _comments=comments;
  }

  @Override
  public String toString()
  {
    return _lastName+" "+_firstName;
  }
}
