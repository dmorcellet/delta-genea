package delta.genea.data;

import delta.common.framework.objects.data.DataObject;
import delta.common.framework.objects.data.DataProxy;

/**
 * Place.
 * @author DAM
 */
public class Place extends DataObject<Place>
{
  /**
   * Class name.
   */
  public static final String CLASS_NAME="PLACE";

  private String _name;
  private String _shortName;
  private PlaceLevel _level;
  private DataProxy<Place> _parent;

  @Override
  public String getClassName()
  {
    return CLASS_NAME;
  }

  /**
   * Constructor.
   */
  public Place()
  {
    super();
  }

  /**
   * Constructor.
   * @param primaryKey Primary key.
   */
  public Place(Long primaryKey)
  {
    super();
    setPrimaryKey(primaryKey);
  }

  /**
   * Get the name of this place.
   * @return A place name.
   */
  public String getName()
  {
    return _name;
  }

  /**
   * Get a short name for this place (department code, country code...).
   * @return a short name or <code>null</code>.
   */
  public String getShortName()
  {
    return _shortName;
  }

  @Override
  public String getLabel()
  {
    String ret=getFullName();
    return ret;
  }

  /**
   * Get a full description of this place.
   * @return A displayable string.
   */
  public String getFullName()
  {
    if (_level==PlaceLevel.DEPARTMENT)
    {
      return _shortName;
    }
    if (_parent==null) return _name;
    StringBuilder sb=new StringBuilder();
    sb.append(_name);
    Place parent=getParentPlace();
    if (parent!=null)
    {
      sb.append(',');
      sb.append(parent.getFullName());
    }
    return sb.toString();
  }

  /**
   * Set the name of this place.
   * @param name Name to set.
   */
  public void setName(String name)
  {
    _name=name;
  }

  /**
   * Set the short name of this place.
   * @param shortName Short name to set.
   */
  public void setShortName(String shortName)
  {
    _shortName=shortName;
  }

  /**
   * Get the level of this place.
   * @return A place level or <code>null</code> if not set.
   */
  public PlaceLevel getLevel()
  {
    return _level;
  }

  /**
   * Set the level of this place.
   * @param level Level to set.
   */
  public void setLevel(PlaceLevel level)
  {
    _level=level;
  }

  /**
   * Get the parent place, if any.
   * @return A place or <code>null</code>.
   */
  public Place getParentPlace()
  {
    if(_parent!=null)
    {
      return _parent.getDataObject();
    }
    return null;
  }

  /**
   * Get the parent place at specified level.
   * @param level Level to search.
   * @return A place or <code>null</code> if not found.
   */
  public Place getParentPlaceForLevel(PlaceLevel level)
  {
    Place current=this;
    while ((current!=null) && (current._level!=level))
    {
      if (current._parent!=null)
      {
        current=current._parent.getDataObject();
      }
      else
      {
        current=null;
      }
    }
    return current;
  }

  /**
   * Get the proxy for the parent place.
   * @return A place proxy or <code>null</code>.
   */
  public DataProxy<Place> getParentPlaceProxy()
  {
    return _parent;
  }

  /**
   * Set the proxy for the parent place.
   * @param parent A place proxy or <code>null</code>.
   */
  public void setParentPlaceProxy(DataProxy<Place> parent)
  {
    _parent=parent;
  }

  @Override
  public String toString()
  {
    return getFullName();
  }
}
