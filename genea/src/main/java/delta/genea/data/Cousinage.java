package delta.genea.data;

import delta.common.framework.objects.data.DataObject;
import delta.common.framework.objects.data.DataProxy;

/**
 * Cousinage.
 * @author DAM
 */
public class Cousinage extends DataObject<Cousinage>
{
  /**
   * Relation that gives the registered cousinages of a person.
   */
  public static final String COUSINAGES_RELATION="COUSINAGE";

  /**
   * Class name.
   */
  public static final String CLASS_NAME="COUSINAGE";

  private DataProxy<Person> _cousin1;
  private DataProxy<Person> _cousin2;

  @Override
  public String getClassName() { return CLASS_NAME; }

  /**
   * Constructor.
   * @param primaryKey Primary key.
   */
  public Cousinage(Long primaryKey)
  {
    super();
    setPrimaryKey(primaryKey);
  }

  /**
   * Get cousin 1.
   * @return a proxy for cousin 1.
   */
  public DataProxy<Person> getCousin1()
  {
    return _cousin1;
  }

  /**
   * Set the proxy for the cousin 1.
   * @param cousin1 Proxy to set.
   */
  public void setCousin1(DataProxy<Person> cousin1)
  {
    _cousin1=cousin1;
  }

  /**
   * Get cousin 2.
   * @return a proxy for cousin 2.
   */
  public DataProxy<Person> getCousin2()
  {
    return _cousin2;
  }

  /**
   * Set the proxy for the cousin 2.
   * @param cousin2 Proxy to set.
   */
  public void setCousin2(DataProxy<Person> cousin2)
  {
    _cousin2=cousin2;
  }

  /**
   * Get the identifier of cousin 1.
   * @return an identifier or 0.
   */
  public long getCousin1ID()
  {
    Long pk=(_cousin1!=null)?_cousin1.getPrimaryKey():null;
    return (pk!=null)?pk.longValue():0;
  }

  /**
   * Get the identifier of cousin 2.
   * @return an identifier or 0.
   */
  public long getCousin2ID()
  {
    Long pk=(_cousin2!=null)?_cousin2.getPrimaryKey():null;
    return (pk!=null)?pk.longValue():0;
  }

  /**
   * Get the cousin person.
   * @param source Other person.
   * @return A person.
   */
  public Person getCousin(long source)
  {
    long cousin1=getCousin1ID();
    if (source==cousin1)
    {
      return _cousin2.getDataObject();
    }
    long cousin2=getCousin2ID();
    if (source==cousin2)
    {
      return _cousin1.getDataObject();
    }
    return null;
  }

  @Override
  public String toString()
  {
    return "Cousinage: "+_cousin1+" / "+_cousin2;
  }
}
