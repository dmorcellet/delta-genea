package delta.genea.web.pages;

import delta.common.framework.web.PageParameters;

/**
 * Parameters for the 'act' page.
 * @author DAM
 */
public class ActPageParameters extends PageParameters
{
  public static final String ACTION_VALUE="ACT";
  public static final String KEY="KEY";
  private long _key;

  /**
   * Constructor.
   * @param key Act key.
   */
  public ActPageParameters(long key)
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
