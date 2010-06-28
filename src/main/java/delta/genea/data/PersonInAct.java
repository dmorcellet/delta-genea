package delta.genea.data;

import delta.common.framework.objects.data.DataProxy;

public class PersonInAct
{
  private DataProxy<Person> _person;
  private String _presence;
  private String _signature;
  private String _link;
  private DataProxy<Person> _linkReference;

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

  public DataProxy<Person> getLinkRefProxy()
  {
    return _linkReference;
  }

  public void setLinkRefProxy(DataProxy<Person> proxy)
  {
    _linkReference=proxy;
  }

  public Person getLinkReference()
  {
    if(_linkReference!=null)
    {
      return _linkReference.getDataObject();
    }
    return null;
  }

  public String getLink()
  {
    return _link;
  }

  public void setLink(String link)
  {
    _link=link;
  }

  public String getPresence()
  {
    return _presence;
  }

  public void setPresence(String presence)
  {
    if (presence==null) presence="";
    _presence=presence;
  }

  public String getSignature()
  {
    return _signature;
  }

  public void setSignature(String signature)
  {
    _signature=signature;
  }
}
