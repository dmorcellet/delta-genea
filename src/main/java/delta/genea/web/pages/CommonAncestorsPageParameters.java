package delta.genea.web.pages;

import delta.common.framework.web.PageParameters;

/**
 * Parameters for the 'common ancestors' page.
 * @author DAM
 */
public class CommonAncestorsPageParameters extends PageParameters
{
  public static final String ACTION_VALUE="COMMON_ANCESTORS";
  private static final String KEY1="KEY1";
  private static final String KEY2="KEY2";
  private long _key1;
  private long _key2;

  /**
   * Constructor.
   * @param key1 Person 1 key.
   * @param key2 Person 2 key.
   */
  public CommonAncestorsPageParameters(long key1, long key2)
  {
    super("genea");
    setKey1(key1);
    setKey2(key2);
  }

  public final void setKey1(long key1)
  {
    _key1=key1;
    _parameters.put(KEY1,Long.valueOf(key1));
  }

  public long getKey1()
  {
    return _key1;
  }

  public final void setKey2(long key2)
  {
    _key2=key2;
    _parameters.put(KEY2,Long.valueOf(key2));
  }

  public long getKey2()
  {
    return _key2;
  }

  @Override
  public String getAction()
  {
    return ACTION_VALUE;
  }
}
