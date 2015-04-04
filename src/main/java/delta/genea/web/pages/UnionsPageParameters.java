package delta.genea.web.pages;

import delta.common.framework.web.PageParameters;

/**
 * Parameters for the 'unions' page.
 * @author DAM
 */
public class UnionsPageParameters extends PageParameters
{
  /**
   * Value of action parameter. 
   */
  public static final String ACTION_VALUE="UNIONS";
  private static final String NAME="NAME";
  private String _name;
  private static final String KEY="KEY";
  private Long _key;

  /**
   * Constructor.
   * @param name Person name filter (may be <code>null</code>).
   * @param key Place primary key.
   */
  public UnionsPageParameters(String name, Long key)
  {
    super("genea");
    setName(name);
    setKey(key);
  }

  /**
   * Set the person name filter.
   * @param name A name filter or <code>null</code>.
   */
  public final void setName(String name)
  {
    _name=name;
    _parameters.put(NAME,_name);
  }

  /**
   * Get the person name filter.
   * @return A name filter or <code>null</code>.
   */
  public String getName()
  {
    return _name;
  }

  /**
   * Set union key.
   * @param key Primary key to set.
   */
  public final void setKey(Long key)
  {
    _key=key;
    _parameters.put(KEY,key);
  }

  /**
   * Get the union key.
   * @return A primary or <code>null</code>.
   */
  public Long getKey()
  {
    return _key;
  }

  @Override
  public String getAction()
  {
    return ACTION_VALUE;
  }
}
