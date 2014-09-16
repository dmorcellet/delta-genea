package delta.genea.web.pages;

import delta.common.framework.web.PageParameters;

/**
 * Parameters for the 'acts from place' page.
 * @author DAM
 */
public class ActsFromPlaceParameters extends PageParameters
{
  /**
   * Value of action parameter. 
   */
  public static final String ACTION_VALUE="ACTS_FROM_PLACE";
  /**
   * Place key parameter name.
   */
  public static final String KEY="KEY";
  private Long _key;

  /**
   * Constructor.
   * @param key Place key.
   */
  public ActsFromPlaceParameters(Long key)
  {
    super("genea");
    setKey(key);
  }

  public final void setKey(Long key)
  {
    _key=key;
    _parameters.put(KEY,key);
  }

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
