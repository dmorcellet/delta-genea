package delta.genea.data;

import java.util.Date;

import delta.common.framework.objects.data.DataObject;
import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.data.ObjectSource;

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
   * @param source Attached objects source.
   */
  public Union(Long primaryKey, ObjectSource<Union> source)
  {
    super(primaryKey,source);
  }

  public Long getDate()
  {
    if (_date==null)
    {
      return null;
    }
    return _date.getDate();
  }

  public String getInfos()
  {
    if (_date==null)
    {
      return null;
    }
    return _date.getInfosDate();
  }

  public GeneaDate getGeneaDate()
  {
    return _date;
  }

  public void setDate(Date date, String infos)
  {
    _date=new GeneaDate(date,infos);
  }

  public void setDate(GeneaDate date)
  {
    _date=date;
  }

  public DataProxy<Place> getPlaceProxy()
  {
    return _place;
  }

  public void setPlaceProxy(DataProxy<Place> place)
  {
    _place=place;
  }

  public Place getPlace()
  {
    if(_place!=null)
    {
      return _place.getDataObject();
    }
    return null;
  }

  public DataProxy<Act> getWeddingContractProxy()
  {
    return _weddingContract;
  }

  public void setWeddingContractProxy(DataProxy<Act> weddingContract)
  {
    _weddingContract=weddingContract;
  }

  public Act getWeddingContract()
  {
    if(_weddingContract!=null)
    {
      return _weddingContract.getDataObject();
    }
    return null;
  }

  public DataProxy<Person> getManProxy()
  {
    return _man;
  }

  public void setManProxy(DataProxy<Person> man)
  {
    _man=man;
  }

  public Long getManKey()
  {
    Long ret=null;
    if (_man!=null)
    {
      ret=_man.getPrimaryKey();
    }
    return ret;
  }

  public Person getMan()
  {
    if(_man!=null)
    {
      return _man.getDataObject();
    }
    return null;
  }

  public DataProxy<Person> getWomanProxy()
  {
    return _woman;
  }

  public void setWomanProxy(DataProxy<Person> woman)
  {
    _woman=woman;
  }

  public Long getWomanKey()
  {
    Long ret=null;
    if (_woman!=null)
    {
      ret=_woman.getPrimaryKey();
    }
    return ret;
  }

  public Person getWoman()
  {
    if(_woman!=null)
    {
      return _woman.getDataObject();
    }
    return null;
  }

  public String getComments()
  {
    return _comments;
  }

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
