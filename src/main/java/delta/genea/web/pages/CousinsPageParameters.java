package delta.genea.web.pages;

import delta.common.framework.web.PageParameters;

/**
 * Parameters for the 'cousins' page.
 * @author DAM
 */
public class CousinsPageParameters extends PageParameters
{
  public static final String ACTION_VALUE="COUSINS";
  private static final String KEY="KEY";
  private long _key;

  /**
   * Constructor.
   * @param key Person key.
   */
  public CousinsPageParameters(long key)
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
