package delta.genea.web.pages;

import delta.common.framework.web.PageParameters;

/**
 * Parameters for the 'ancestors list' page.
 * @author DAM
 */
public class AncestorsListPageParameters extends PageParameters
{
  public static final String ACTION_VALUE="ANCESTORS_LIST";
  static final String KEY="KEY";
  static final String DEPTH="DEPTH";
  private long _key;
  private int _depth;

  /**
   * Constructor.
   * @param key Root person key.
   * @param depth Number of generations.
   */
  public AncestorsListPageParameters(long key, int depth)
  {
    super("genea");
    setKey(key);
    setDepth(depth);
  }

  public final void setDepth(int depth)
  {
    _depth=depth;
    _parameters.put(DEPTH,Integer.valueOf(depth));
  }

  public int getDepth()
  {
    return _depth;
  }

  public final void setKey(long key)
  {
    _key=key;
    _parameters.put(KEY,Long.valueOf(key));
  }

  public long getMainPersonKey()
  {
    return _key;
  }

  @Override
  public String getAction()
  {
    return ACTION_VALUE;
  }
}
