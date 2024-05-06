package delta.genea.web.pages;

import delta.common.framework.web.PageParameters;

/**
 * Parameters for the 'dates to search' page.
 * @author DAM
 */
public class DatestoSearchPageParameters extends PageParameters
{
  /**
   * Value of action parameter. 
   */
  public static final String ACTION_VALUE="DATES_TO_SEARCH";
  private static final String KEY="KEY";
  private static final String DEPTH="DEPTH";
  private Long _key;
  private int _depth;

  /**
   * Constructor.
   * @param key Root person key.
   * @param depth Number of generations.
   */
  public DatestoSearchPageParameters(Long key, int depth)
  {
    super("genea");
    setKey(key);
    setDepth(depth);
  }

  /**
   * Set the depth parameter.
   * @param depth Depth to set.
   */
  public final void setDepth(int depth)
  {
    _depth=depth;
    _parameters.put(DEPTH,Integer.valueOf(depth));
  }

  /**
   * Get the depth parameter.
   * @return Depth to use.
   */
  public int getDepth()
  {
    return _depth;
  }

  /**
   * Set root person key.
   * @param key Primary key to set.
   */
  public final void setKey(Long key)
  {
    _key=key;
    _parameters.put(KEY,key);
  }

  /**
   * Get the person key.
   * @return A primary or <code>null</code>.
   */
  public Long getMainPersonKey()
  {
    return _key;
  }

  @Override
  public String getAction()
  {
    return ACTION_VALUE;
  }
}
