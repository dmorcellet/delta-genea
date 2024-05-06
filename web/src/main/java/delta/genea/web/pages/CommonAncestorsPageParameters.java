package delta.genea.web.pages;

import delta.common.framework.web.PageParameters;

/**
 * Parameters for the 'common ancestors' page.
 * @author DAM
 */
public class CommonAncestorsPageParameters extends PageParameters
{
  /**
   * Value of action parameter. 
   */
  public static final String ACTION_VALUE="COMMON_ANCESTORS";
  private static final String KEY1="KEY1";
  private static final String KEY2="KEY2";
  private Long _key1;
  private Long _key2;

  /**
   * Constructor.
   * @param key1 Person 1 key.
   * @param key2 Person 2 key.
   */
  public CommonAncestorsPageParameters(Long key1, Long key2)
  {
    super("genea");
    setKey1(key1);
    setKey2(key2);
  }

  /**
   * Set person 1 key.
   * @param key1 Primary key to set.
   */
  public final void setKey1(Long key1)
  {
    _key1=key1;
    _parameters.put(KEY1,key1);
  }

  /**
   * Get the person 1 key.
   * @return A person primary key.
   */
  public Long getKey1()
  {
    return _key1;
  }

  /**
   * Set person 2 key.
   * @param key2 Primary key to set.
   */
  public final void setKey2(Long key2)
  {
    _key2=key2;
    _parameters.put(KEY2,key2);
  }

  /**
   * Get the person 2 key.
   * @return A person primary key.
   */
  public Long getKey2()
  {
    return _key2;
  }

  @Override
  public String getAction()
  {
    return ACTION_VALUE;
  }
}
