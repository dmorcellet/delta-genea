package delta.genea.web.pages;

import delta.common.framework.web.PageParameters;

/**
 * Parameters for the 'name' page.
 * @author DAM
 */
public class NamePageParameters extends PageParameters
{
  /**
   * Value of action parameter. 
   */
  public static final String ACTION_VALUE="NAME_INDEX";
  static final String NAME="NAME";
  private String _name;
  static final String NO_DESCENDANTS="NO_DESCENDANTS";
  private boolean _noDescendants;

  /**
   * Constructor.
   * @param name Name to use.
   */
  public NamePageParameters(String name)
  {
    super("genea");
    setName(name);
    _noDescendants=true;
  }

  /**
   * Set the name parameter.
   * @param name Name to set.
   */
  public final void setName(String name)
  {
    _name=name;
    _parameters.put(NAME,_name);
  }

  /**
   * Set the 'no descendants' flag.
   * @param noDescendants Flag value.
   */
  public final void setNoDescendants(boolean noDescendants)
  {
    _noDescendants=noDescendants;
    _parameters.put(NO_DESCENDANTS,Boolean.valueOf(noDescendants));
  }

  /**
   * Get the value of the name parameter.
   * @return A name.
   */
  public String getName()
  {
    return _name;
  }

  /**
   * Get the value of the 'no descendants' flag.
   * @return Flag value.
   */
  public boolean getNoDescendants()
  {
    return _noDescendants;
  }

  @Override
  public String getAction()
  {
    return ACTION_VALUE;
  }
}
