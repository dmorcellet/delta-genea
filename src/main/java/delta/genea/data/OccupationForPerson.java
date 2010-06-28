package delta.genea.data;

import delta.common.framework.objects.data.DataProxy;

public class OccupationForPerson
{
  private DataProxy<Person> _person;
  private String _occupation;
  private int _year;
  private DataProxy<Place> _place;

  public DataProxy<Person> getPersonProxy()
  {
    return _person;
  }

  public void setPersonProxy(DataProxy<Person> proxy)
  {
    _person=proxy;
  }

  public Person getPerson()
  {
    if(_person!=null)
    {
      return _person.getDataObject();
    }
    return null;
  }

  public DataProxy<Place> getPlaceProxy()
  {
    return _place;
  }

  public void setPlaceProxy(DataProxy<Place> proxy)
  {
    _place=proxy;
  }

  public Place getPlace()
  {
    if(_place!=null)
    {
      return _place.getDataObject();
    }
    return null;
  }

  public String getOccupation()
  {
    return _occupation;
  }

  public void setOccupation(String occupation)
  {
    _occupation=occupation;
  }

  public int getYear()
  {
    return _year;
  }

  public void setYear(int year)
  {
    _year=year;
  }

  public String getLabel()
  {
    StringBuffer sb=new StringBuffer();
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
