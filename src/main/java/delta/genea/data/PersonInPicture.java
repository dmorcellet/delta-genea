package delta.genea.data;

import delta.common.framework.objects.data.DataProxy;

public class PersonInPicture
{
  private DataProxy<Person> _person;

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
}
