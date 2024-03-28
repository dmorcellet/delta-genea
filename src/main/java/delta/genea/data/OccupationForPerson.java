package delta.genea.data;

import delta.common.framework.objects.data.DataProxy;

/**
 * A single occupation data for a person.
 * @author DAM
 */
public class OccupationForPerson
{
  private String _occupation;
  private int _year;
  private DataProxy<Place> _place;

  /**
   * Get a proxy to the place of this occupation.
   * @return A proxy or <code>null</code>.
   */
  public DataProxy<Place> getPlaceProxy()
  {
    return _place;
  }

  /**
   * Set the proxy to the place of this occupation.
   * @param proxy Proxy to set.
   */
  public void setPlaceProxy(DataProxy<Place> proxy)
  {
    _place=proxy;
  }

  /**
   * Get the place for this occupation.
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
   * Get the occupation.
   * @return the occupation or <code>null</code>.
   */
  public String getOccupation()
  {
    return _occupation;
  }

  /**
   * Set occupation.
   * @param occupation Occupation to set.
   */
  public void setOccupation(String occupation)
  {
    _occupation=occupation;
  }

  /**
   * Get the year associated with this occupation data.
   * @return A year (<code>0</code> means <code>null</code>).
   */
  public int getYear()
  {
    return _year;
  }

  /**
   * Set the year associated with this occupation data.
   * @param year Year to set (<code>0</code> means <code>null</code>).
   */
  public void setYear(int year)
  {
    _year=year;
  }

  /**
   * Get a readable string that shows this occupation data.
   * @return A string.
   */
  public String getLabel()
  {
    StringBuilder sb=new StringBuilder();
    sb.append(_occupation);
    if (_year!=0)
    {
      sb.append(" (");
      sb.append(_year);
      sb.append(')');
    }
    Place p=getPlace();
    if (p!=null)
    {
      sb.append(" (");
      sb.append(p.getFullName());
      sb.append(')');
    }
    return sb.toString();
  }
}
