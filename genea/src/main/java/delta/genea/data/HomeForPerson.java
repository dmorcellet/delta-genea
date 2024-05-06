package delta.genea.data;

import delta.common.framework.objects.data.DataProxy;

/**
 * A single home data for a person.
 * @author DAM
 */
public class HomeForPerson
{
  private int _year;
  private String _placeDetails;
  private DataProxy<Place> _place;

  /**
   * Constructor.
   */
  public HomeForPerson()
  {
    _year=0;
    _placeDetails=null;
    _place=null;
  }

  /**
   * Get a proxy to the place of this home.
   * @return A proxy or <code>null</code>.
   */
  public DataProxy<Place> getPlaceProxy()
  {
    return _place;
  }

  /**
   * Set the proxy to the place of this home.
   * @param proxy Proxy to set.
   */
  public void setPlaceProxy(DataProxy<Place> proxy)
  {
    _place=proxy;
  }

  /**
   * Get the place of this home.
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
   * Get the details for the place of this home.
   * @return some details as a string, or <code>null</code>.
   */
  public String getPlaceDetails()
  {
    return _placeDetails;
  }

  /**
   * Set the details for the place of this home.
   * @param placeDetails Details to set.
   */
  public void setPlaceDetails(String placeDetails)
  {
    _placeDetails=placeDetails;
  }

  /**
   * Get the year associated with this home data.
   * @return A year (<code>0</code> means <code>null</code>).
   */
  public int getYear()
  {
    return _year;
  }

  /**
   * Set the year associated with this home data.
   * @param year Year to set (<code>0</code> means <code>null</code>).
   */
  public void setYear(int year)
  {
    _year=year;
  }

  /**
   * Get a readable string that shows this home data.
   * @return A string.
   */
  public String getLabel()
  {
    StringBuilder sb=new StringBuilder();
    if (_year!=0)
    {
      sb.append("(");
      sb.append(_year);
      sb.append(')');
    }
    if ((_placeDetails!=null) && (_placeDetails.length()>0))
    {
      sb.append(' ');
      sb.append(_placeDetails);
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
