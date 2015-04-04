package delta.genea.web.pages;

import delta.common.framework.web.PageParameters;

/**
 * Parameters for the 'person' page.
 * @author DAM
 */
public class PersonPageParameters extends PageParameters
{
  /**
   * Value of action parameter. 
   */
  public static final String ACTION_VALUE="PERSON";
  /**
   * Main person key parameter.
   */
  public static final String PERSON_KEY="PERSON_KEY";
  private Long _mainPersonKey;

  /**
   * Constructor.
   * @param mainPersonKey Main person primary key.
   */
  public PersonPageParameters(Long mainPersonKey)
  {
    super("genea");
    setMainPersonKey(mainPersonKey);
  }

  /**
   * Set main person key.
   * @param mainPersonKey Primary key to set.
   */
  public final void setMainPersonKey(Long mainPersonKey)
  {
    _mainPersonKey=mainPersonKey;
    _parameters.put(PERSON_KEY,mainPersonKey);
  }

  /**
   * Get the person key.
   * @return A primary or <code>null</code>.
   */
  public Long getMainPersonKey()
  {
    return _mainPersonKey;
  }

  @Override
  public String getAction()
  {
    return ACTION_VALUE;
  }
}
