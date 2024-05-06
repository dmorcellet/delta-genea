package delta.genea.web.pages;

import delta.common.framework.web.PageParameters;

/**
 * Parameters for the 'descendants' page.
 * @author DAM
 */
public class DescendantsPageParameters extends PageParameters
{
  /**
   * Value of action parameter. 
   */
  public static final String ACTION_VALUE="DESCENDANTS_TREE";
  /**
   * Main person key parameter.
   */
  public static final String MAIN_PERSON_KEY="KEY";
  /**
   * Depth parameter.
   */
  public static final String DEPTH="DEPTH";
  static final String SAME_NAME="SAME_NAME";
  private Long _key;
  private int _depth;
  private boolean _sameName;

  /**
   * Constructor.
   */
  public DescendantsPageParameters()
  {
    super("genea");
    init(null,100,false);
  }

  /**
   * Full constructor.
   * @param mainPersonKey Main person primary key.
   * @param depth Depth of the descendants tree.
   * @param sameName <code>true</code> to include only persons whose
   * name is the same as the name of the main person.
   */
  public DescendantsPageParameters(Long mainPersonKey, int depth, boolean sameName)
  {
    super("genea");
    init(mainPersonKey,depth,sameName);
  }

  private void init(Long mainPersonKey, int depth, boolean sameName)
  {
    setKey(mainPersonKey);
    setDepth(depth);
    setSameName(sameName);
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
    _parameters.put(MAIN_PERSON_KEY,key);
  }

  /**
   * Get the person key.
   * @return A primary or <code>null</code>.
   */
  public Long getKey()
  {
    return _key;
  }

  /**
   * Set the 'same name' flag.
   * @param sameName Value to set.
   */
  public final void setSameName(boolean sameName)
  {
    _sameName=sameName;
    _parameters.put(SAME_NAME,Boolean.valueOf(sameName));
  }

  /**
   * Get the 'same name' flag.
   * @return the flag value.
   */
  public boolean getSameName()
  {
    return _sameName;
  }

  @Override
  public String getAction()
  {
    return ACTION_VALUE;
  }
}
