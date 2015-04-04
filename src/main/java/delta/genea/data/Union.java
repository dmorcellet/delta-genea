package delta.genea.data;

import java.util.Date;

import delta.common.framework.objects.data.DataObject;
import delta.common.framework.objects.data.DataProxy;

/**
 * Union.
 * @author DAM
 */
public class Union extends DataObject<Union>
{
  /**
   * Class name.
   */
  public static final String CLASS_NAME="UNION";
  /**
   * Relation 'unions' (unions of a person).
   */
  public static final String UNIONS_RELATION="UNIONS_FOR_PERSON";
  // Sets
  /**
   * Set of the unions that involve a person with the given name, at the given place.
   */
  public static final String NAME_AND_PLACE_SET="NAME_AND_PLACE";

  // Date
  private GeneaDate _date;

  // Place
  private DataProxy<Place> _place;

  // Persons
  private DataProxy<Person> _man;
  private DataProxy<Person> _woman;

  // Wedding contract
  private DataProxy<Act> _weddingContract;

  // Comments
  private String _comments;

  @Override
  public String getClassName()
  {
    return CLASS_NAME;
  }

  /**
   * Constructor.
   */
  public Union()
  {
    super();
  }

  /**
   * Constructor.
   * @param primaryKey Primary key.
   */
  public Union(Long primaryKey)
  {
    super();
    setPrimaryKey(primaryKey);
  }

  /**
   * Get the date of this union.
   * @return A date or <code>null</code>.
   */
  public Long getDate()
  {
    if (_date==null)
    {
      return null;
    }
    return _date.getDate();
  }

  /**
   * Get the information about the date of this union.
   * @return An information string or <code>null</code>.
   */
  public String getInfos()
  {
    if (_date==null)
    {
      return null;
    }
    return _date.getInfosDate();
  }

  /**
   * Get the date of this union.
   * @return the date data or <code>null</code>.
   */
  public GeneaDate getGeneaDate()
  {
    return _date;
  }

  /**
   * Set the date data for this union.
   * @param date Date of this union.
   * @param infos Date information for this union.
   */
  public void setDate(Date date, String infos)
  {
    _date=new GeneaDate(date,infos);
  }

  /**
   * Set the date data for this union.
   * @param date Date data to set.
   */
  public void setDate(GeneaDate date)
  {
    _date=date;
  }

  /**
   * Get the proxy for the place of this union.
   * @return A place proxy or <code>null</code>.
   */
  public DataProxy<Place> getPlaceProxy()
  {
    return _place;
  }

  /**
   * Set the proxy for the place of this union.
   * @param place Place proxy to set or <code>null</code>.
   */
  public void setPlaceProxy(DataProxy<Place> place)
  {
    _place=place;
  }

  /**
   * Get the place of this union.
   * @return A place or <code>null</code>.
   */
  public Place getPlace()
  {
    if(_place!=null)
    {
      return _place.getDataObject();
    }
    return null;
  }

  /**
   * Get the proxy for the wedding contract associated to this union.
   * @return An act proxy or <code>null</code>.
   */
  public DataProxy<Act> getWeddingContractProxy()
  {
    return _weddingContract;
  }

  /**
   * Set the wedding contract proxy for this union.
   * @param weddingContract Proxy to set or <code>null</code>.
   */
  public void setWeddingContractProxy(DataProxy<Act> weddingContract)
  {
    _weddingContract=weddingContract;
  }

  /**
   * Get the wedding contract associated to this union.
   * @return An act or <code>null</code>.
   */
  public Act getWeddingContract()
  {
    if(_weddingContract!=null)
    {
      return _weddingContract.getDataObject();
    }
    return null;
  }

  /**
   * Get the proxy for the male person involved in this union.
   * @return A proxy or <code>null</code>.
   */
  public DataProxy<Person> getManProxy()
  {
    return _man;
  }

  /**
   * Set the proxy to the male person of this union.
   * @param man Proxy to set or <code>null</code>.
   */
  public void setManProxy(DataProxy<Person> man)
  {
    _man=man;
  }

  /**
   * Get the primary key for the male person in this union.
   * @return A person primary key or <code>null</code>.
   */
  public Long getManKey()
  {
    Long ret=null;
    if (_man!=null)
    {
      ret=_man.getPrimaryKey();
    }
    return ret;
  }

  /**
   * Get the male person in this union.
   * @return A person or <code>null</code>.
   */
  public Person getMan()
  {
    if(_man!=null)
    {
      return _man.getDataObject();
    }
    return null;
  }

  /**
   * Get the proxy for the female person involved in this union.
   * @return A proxy or <code>null</code>.
   */
  public DataProxy<Person> getWomanProxy()
  {
    return _woman;
  }

  /**
   * Set the proxy to the female person of this union.
   * @param woman Proxy to set or <code>null</code>.
   */
  public void setWomanProxy(DataProxy<Person> woman)
  {
    _woman=woman;
  }

  /**
   * Get the primary key for the female person in this union.
   * @return A person primary key or <code>null</code>.
   */
  public Long getWomanKey()
  {
    Long ret=null;
    if (_woman!=null)
    {
      ret=_woman.getPrimaryKey();
    }
    return ret;
  }

  /**
   * Get the female person in this union.
   * @return A person or <code>null</code>.
   */
  public Person getWoman()
  {
    if(_woman!=null)
    {
      return _woman.getDataObject();
    }
    return null;
  }

  /**
   * Get the comments attached to this union.
   * @return A comment string or <code>null</code>.
   */
  public String getComments()
  {
    return _comments;
  }

  /**
   * Set the comments for this union.
   * @param comments Comments to set or <code>null</code>.
   */
  public void setComments(String comments)
  {
    _comments=comments;
  }

  @Override
  public String toString()
  {
    Person man=getMan();
    String manName=(man!=null)?man.toString():"???";
    Person woman=getWoman();
    String womanName=(woman!=null)?woman.toString():"???";
    return manName+"/"+womanName;
  }
}
