package delta.genea.web.pages;

import delta.common.framework.web.PageParameters;

/**
 * Parameters for the 'act' page.
 * @author DAM
 */
public class ActPageParameters extends PageParameters
{
  /**
   * Value of action parameter. 
   */
  public static final String ACTION_VALUE="ACT";
  /**
   * Act key parameter.
   */
  public static final String KEY="KEY";
  private Long _key;

  /**
   * Constructor.
   * @param key Act key.
   */
  public ActPageParameters(Long key)
  {
    super("genea");
    setKey(key);
  }

  /**
   * Set act key.
   * @param key Primary key to set.
   */
  public final void setKey(Long key)
  {
    _key=key;
    _parameters.put(KEY,key);
  }

  /**
   * Get the act key.
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
