package delta.genea.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import delta.common.framework.objects.data.DataObject;
import delta.common.framework.objects.data.DataProxy;

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
  private GeneaDate _birthDate;
  private DataProxy<Place> _birthPlace;
  // Death
  private GeneaDate _deathDate;
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
   */
  public Person(Long primaryKey)
  {
    super();
    setPrimaryKey(primaryKey);
    _sex=Sex.MALE;
    _noDescendants=false;
    _lastName="";
    _firstName="";
    _signature="";
    _birthDate=new GeneaDate();
    _deathDate=new GeneaDate();
    _comments="";
  }

  /**
   * Get the person's last name.
   * @return a last name (or empty string if not set).
   */
  public String getLastName()
  {
    return _lastName;
  }

  /**
   * Set the last name for this person.
   * @param lastName Last name to set.
   */
  public void setLastName(String lastName)
  {
    if (lastName==null)
    {
      lastName="";
    }
    _lastName=lastName;
  }

  /**
   * Get the person's first name.
   * @return a first name (or empty string if not set).
   */
  public String getFirstname()
  {
    return _firstName;
  }

  /**
   * Set the first name for this person.
   * @param firstname Last name to set.
   */
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

  /**
   * Get a displayable full name for this person.
   * This is first name, space and last name.
   * @return A readable string.
   */
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

  /**
   * Set the sex of this person.
   * @param sex Sex to set.
   */
  public void setSex(Sex sex)
  {
    _sex=sex;
  }

  /**
   * Get the signature of this person.
   * @return a signature (or empty string if not set).
   */
  public String getSignature()
  {
    return _signature;
  }

  /**
   * Set the signature of this person.
   * @param signature Signature to set.
   */
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

  /**
   * Get the birth date of this person.
   * @return A birth date (timestamp) or <code>null</code> if not set.
   */
  public Long getBirthDate()
  {
    return _birthDate.getDate();
  }

  /**
   * Set the birth date of this person.
   * @param date Date to set (timestamp), or <code>null</code>.
   * @param infosDate Date information.
   */
  public void setBirthDate(Long date, String infosDate)
  {
    _birthDate.setDate(date);
    _birthDate.setInfosDate(infosDate);
  }

  /**
   * Set the birth date of this person.
   * @param date Date to set (timestamp), or <code>null</code>.
   * @param infosDate Date information.
   */
  public void setBirthDate(Date date, String infosDate)
  {
    _birthDate.setDate(date);
    _birthDate.setInfosDate(infosDate);
  }

  /**
   * Set the birth date of this person.
   * @param date Date to set.
   */
  public void setBirthDate(GeneaDate date)
  {
    _birthDate=date;
  }

  /**
   * Get the birth information for this person.
   * @return A birth information string.
   */
  public String getBirthInfos()
  {
    return _birthDate.getInfosDate();
  }

  /**
   * Get the proxy for the birth place of this person.
   * @return A place proxy or <code>null</code>.
   */
  public DataProxy<Place> getBirthPlaceProxy()
  {
    return _birthPlace;
  }

  /**
   * Set the proxy for the birth place.
   * @param birthPlace Proxy to set (may be <code>null</code>).
   */
  public void setBirthPlaceProxy(DataProxy<Place> birthPlace)
  {
    _birthPlace=birthPlace;
  }

  /**
   * Get the birth place.
   * @return A place or <code>null</code>.
   */
  public Place getBirthPlace()
  {
    if (_birthPlace!=null)
    {
      return _birthPlace.getDataObject();
    }
    return null;
  }

  /**
   * Get the death date of this person.
   * @return A death date (timestamp) or <code>null</code> if not set.
   */
  public Long getDeathDate()
  {
    return _deathDate.getDate();
  }

  /**
   * Get the death date of this person.
   * @return the death date of this person.
   */
  public GeneaDate getDeathGeneaDate()
  {
    return _deathDate;
  }

  /**
   * Set the death date of this person.
   * @param date Date to set (timestamp), or <code>null</code>.
   * @param infosDate Date information.
   */
  public void setDeathDate(Long date, String infosDate)
  {
    _deathDate.setDate(date);
    _deathDate.setInfosDate(infosDate);
  }

  /**
   * Set the death date of this person.
   * @param date Date to set (timestamp), or <code>null</code>.
   * @param infosDate Date information.
   */
  public void setDeathDate(Date date, String infosDate)
  {
    _deathDate.setDate(date);
    _deathDate.setInfosDate(infosDate);
  }

  /**
   * Set the death date of this person.
   * @param date Date to set.
   */
  public void setDeathDate(GeneaDate date)
  {
    _deathDate=date;
  }

  /**
   * Get the death information for this person.
   * @return A death information string.
   */
  public String getDeathInfos()
  {
    return _deathDate.getInfosDate();
  }

  /**
   * Get the proxy for the death place of this person.
   * @return A place proxy or <code>null</code>.
   */
  public DataProxy<Place> getDeathPlaceProxy()
  {
    return _deathPlace;
  }

  /**
   * Set the proxy for the death place.
   * @param deathPlace Proxy to set (may be <code>null</code>).
   */
  public void setDeathPlaceProxy(DataProxy<Place> deathPlace)
  {
    _deathPlace=deathPlace;
  }

  /**
   * Get the death place.
   * @return A place or <code>null</code>.
   */
  public Place getDeathPlace()
  {
    if (_deathPlace!=null)
    {
      return _deathPlace.getDataObject();
    }
    return null;
  }

  /**
   * Indicates if this person has no descendants.
   * @return <code>true</code> if it has no descendants, <code>false</code> otherwise.
   */
  public boolean getNoDescendants()
  {
    return _noDescendants;
  }

  /**
   * Set the value of the 'no descendants' flag.
   * @param noDescendants Value to set.
   */
  public void setNoDescendants(boolean noDescendants)
  {
    _noDescendants=noDescendants;
  }

  /**
   * Get the proxy for the father of this person.
   * @return A person proxy or <code>null</code>.
   */
  public DataProxy<Person> getFatherProxy()
  {
    return _father;
  }

  /**
   * Set the father proxy.
   * @param father Proxy to set (may be <code>null</code>).
   */
  public void setFatherProxy(DataProxy<Person> father)
  {
    _father=father;
  }

  /**
   * Get the primary key for the father.
   * @return A primary key or <code>null</code>.
   */
  public Long getFatherKey()
  {
    Long ret=null;
    if (_father!=null)
    {
      ret=_father.getPrimaryKey();
    }
    return ret;
  }

  /**
   * Get the father of this person.
   * @return A person or <code>null</code>.
   */
  public Person getFather()
  {
    if (_father!=null)
    {
      return _father.getDataObject();
    }
    return null;
  }

  /**
   * Get the proxy for the mother of this person.
   * @return A person proxy or <code>null</code>.
   */
  public DataProxy<Person> getMotherProxy()
  {
    return _mother;
  }

  /**
   * Set the mother proxy.
   * @param mother Proxy to set (may be <code>null</code>).
   */
  public void setMotherProxy(DataProxy<Person> mother)
  {
    _mother=mother;
  }

  /**
   * Get the primary key for the mother.
   * @return A primary key or <code>null</code>.
   */
  public Long getMotherKey()
  {
    Long ret=null;
    if (_mother!=null)
    {
      ret=_mother.getPrimaryKey();
    }
    return ret;
  }

  /**
   * Get the mother of this person.
   * @return A person or <code>null</code>.
   */
  public Person getMother()
  {
    if(_mother!=null)
    {
      return _mother.getDataObject();
    }
    return null;
  }

  /**
   * Get the proxy for the godfather of this person.
   * @return A person proxy or <code>null</code>.
   */
  public DataProxy<Person> getGodFatherProxy()
  {
    return _godFather;
  }

  /**
   * Set the godfather proxy.
   * @param godFather Proxy to set (may be <code>null</code>).
   */
  public void setGodFatherProxy(DataProxy<Person> godFather)
  {
    _godFather=godFather;
  }

  /**
   * Get the primary key for the godfather.
   * @return A primary key or <code>null</code>.
   */
  public Long getGodFatherKey()
  {
    Long ret=null;
    if (_godFather!=null)
    {
      ret=_godFather.getPrimaryKey();
    }
    return ret;
  }

  /**
   * Get the godfather of this person.
   * @return A person or <code>null</code>.
   */
  public Person getGodFather()
  {
    if(_godFather!=null)
    {
      return _godFather.getDataObject();
    }
    return null;
  }

  /**
   * Get the proxy for the godmother of this person.
   * @return A person proxy or <code>null</code>.
   */
  public DataProxy<Person> getGodMotherProxy()
  {
    return _godMother;
  }

  /**
   * Set the godmother proxy.
   * @param godMother Proxy to set (may be <code>null</code>).
   */
  public void setGodMotherProxy(DataProxy<Person> godMother)
  {
    _godMother=godMother;
  }

  /**
   * Get the primary key for the godmother.
   * @return A primary key or <code>null</code>.
   */
  public Long getGodMotherKey()
  {
    Long ret=null;
    if (_godMother!=null)
    {
      ret=_godMother.getPrimaryKey();
    }
    return ret;
  }

  /**
   * Get the godmother of this person.
   * @return A person or <code>null</code>.
   */
  public Person getGodMother()
  {
    if(_godMother!=null)
    {
      return _godMother.getDataObject();
    }
    return null;
  }

  /**
   * Get all occupations for this person.
   * @return A possibly empty list of occupations.
   */
  public List<OccupationForPerson> getOccupations()
  {
    return _occupations;
  }

  /**
   * Set the occupations for this person.
   * @param list Occupations to set.
   */
  public void setOccupations(List<OccupationForPerson> list)
  {
    _occupations=list;
  }

  /**
   * Add an occupations for this person.
   * @param o Occupation to add.
   */
  public void addOccupation(OccupationForPerson o)
  {
    if (_occupations==null)
    {
      _occupations=new ArrayList<OccupationForPerson>();
    }
    _occupations.add(o);
  }

  /**
   * Get all homes for this person.
   * @return A possibly empty list of homes.
   */
  public List<HomeForPerson> getHomes()
  {
    return _homes;
  }

  /**
   * Set the homes for this person.
   * @param list Homes to set.
   */
  public void setHomes(List<HomeForPerson> list)
  {
    _homes=list;
  }

  /**
   * Add an home for this person.
   * @param h Home to add.
   */
  public void addHome(HomeForPerson h)
  {
    if (_homes==null)
    {
      _homes=new ArrayList<HomeForPerson>();
    }
    _homes.add(h);
  }

  /**
   * Get the comments for this person.
   * @return A possibly empty comment string.
   */
  public String getComments()
  {
    return _comments;
  }

  /**
   * Set the comments for this person.
   * @param comments Comments to set.
   */
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
