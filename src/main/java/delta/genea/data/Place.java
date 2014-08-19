package delta.genea.data;

import delta.common.framework.objects.data.DataObject;
import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.data.ObjectSource;

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
   * @param source Attached objects source.
   */
  public Place(Long primaryKey, ObjectSource<Place> source)
  {
    super(primaryKey,source);
  }

  public String getName()
  {
    return _name;
  }

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

  public String getFullName()
  {
    if (_level==PlaceLevel.DEPARTMENT)
    {
      return _shortName;
    }
    if (_parent==null) return _name;
    StringBuffer sb=new StringBuffer();
    sb.append(_name);
    Place parent=getParentPlace();
    if (parent!=null)
    {
      sb.append(',');
      sb.append(parent.getFullName());
    }
    return sb.toString();
  }

  public void setName(String name)
  {
    _name=name;
  }

  public void setShortName(String shortName)
  {
    _shortName=shortName;
  }

  public PlaceLevel getLevel()
  {
    return _level;
  }

  public void setLevel(PlaceLevel level)
  {
    _level=level;
  }

  public Place getParentPlace()
  {
    if(_parent!=null)
    {
      return _parent.getDataObject();
    }
    return null;
  }

  public Place getParentPlaceForLevel(PlaceLevel level)
  {
    Place current=this;
    while ((current!=null) && (current._level!=level))
    {
      if (current._parent!=null) current=current._parent.getDataObject();
      else current=null;
    }
    return current;
  }

  public DataProxy<Place> getParentPlaceProxy()
  {
    return _parent;
  }

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
