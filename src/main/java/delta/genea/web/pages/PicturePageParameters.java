package delta.genea.web.pages;

import delta.common.framework.web.PageParameters;

/**
 * Parameters for the 'picture' page.
 * @author DAM
 */
public class PicturePageParameters extends PageParameters
{
  /**
   * Value of action parameter. 
   */
  public static final String ACTION_VALUE="PICTURE";
  private static final String KEY="KEY";
  private Long _key;

  /**
   * Constructor.
   * @param key Picture primary key.
   */
  public PicturePageParameters(Long key)
  {
    super("genea");
    setKey(key);
  }

  /**
   * Set the picture key parameter.
   * @param key Picture primary key or <code>null</code>.
   */
  public final void setKey(Long key)
  {
    _key=key;
    _parameters.put(KEY,key);
  }

  /**
   * Get the picture primary key.
   * @return A picture primary or <code>null</code>.
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
