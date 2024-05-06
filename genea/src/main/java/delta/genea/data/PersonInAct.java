package delta.genea.data;

import delta.common.framework.objects.data.DataProxy;

/**
 * Storage for a person mentioned in an act.
 * @author DAM
 */
public class PersonInAct
{
  private DataProxy<Person> _person;
  private String _presence;
  private String _signature;
  private String _link;
  private DataProxy<Person> _linkReference;

  /**
   * Constructor.
   */
  public PersonInAct()
  {
    _person=null;
    _presence="";
    _signature="";
    _link=null;
    _linkReference=null;
  }

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

  /**
   * Get the proxy to the person referenced in the link.
   * @return A person proxy or <code>null</code>.
   */
  public DataProxy<Person> getLinkRefProxy()
  {
    return _linkReference;
  }

  /**
   * Set the referenced person proxy.
   * @param proxy Proxy to set (may be <code>null</code>).
   */
  public void setLinkRefProxy(DataProxy<Person> proxy)
  {
    _linkReference=proxy;
  }

  /**
   * Get the person referenced in the link.
   * @return A person or <code>null</code>.
   */
  public Person getLinkReference()
  {
    if(_linkReference!=null)
    {
      return _linkReference.getDataObject();
    }
    return null;
  }

  /**
   * Get the link between person and referenced person.
   * @return A link description (may be <code>null</code>).
   */
  public String getLink()
  {
    return _link;
  }

  /**
   * Set the link.
   * @param link Lin description to set (may be <code>null</code>).
   */
  public void setLink(String link)
  {
    _link=link;
  }

  /**
   * Get the presence indicator.
   * @return A presence indicator ("O", "N", "+" or "")
   */
  public String getPresence()
  {
    return _presence;
  }

  /**
   * Set the presence indicator.
   * @param presence Indicator to set.
   */
  public void setPresence(String presence)
  {
    if (presence==null)
    {
      presence="";
    }
    _presence=presence;
  }

  /**
   * Get the signature indicator.
   * @return A signature indicator( "S", "M", "N", "-" or "").
   */
  public String getSignature()
  {
    return _signature;
  }

  /**
   * Set the signature indicator.
   * @param signature Indicator to set.
   */
  public void setSignature(String signature)
  {
    _signature=signature;
  }
}
