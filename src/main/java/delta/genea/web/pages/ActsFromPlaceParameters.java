package delta.genea.web.pages;

import delta.common.framework.web.PageParameters;

/**
 * Parameters for the 'acts from place' page.
 * @author DAM
 */
public class ActsFromPlaceParameters extends PageParameters
{
  public static final String ACTION_VALUE="ACTS_FROM_PLACE";
  public static final String KEY="KEY";
  private long _key;

  /**
   * Constructor.
   * @param key Place key.
   */
  public ActsFromPlaceParameters(long key)
  {
    super("genea");
    setKey(key);
  }

  public final void setKey(long key)
  {
    _key=key;
    _parameters.put(KEY,Long.valueOf(key));
  }

  public long getKey()
  {
    return _key;
  }

  @Override
  public String getAction()
  {
    return ACTION_VALUE;
  }
}
