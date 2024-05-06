package delta.genea.data;

import delta.common.framework.objects.data.DataProxy;

/**
 * Storage for a person present in a picture.
 * @author DAM
 */
public class PersonInPicture
{
  private DataProxy<Person> _person;

  /**
   * Get the person proxy.
   * @return A person proxy or <code>null</code>.
   */
  public DataProxy<Person> getPersonProxy()
  {
    return _person;
  }

  /**
   * Set the person proxy.
   * @param proxy Proxy to set (may be <code>null</code>).
   */
  public void setPersonProxy(DataProxy<Person> proxy)
  {
    _person=proxy;
  }

  /**
   * Get the targeted person.
   * @return A person or <code>null</code>.
   */
  public Person getPerson()
  {
    if(_person!=null)
    {
      return _person.getDataObject();
    }
    return null;
  }
}
