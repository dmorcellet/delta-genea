package delta.genea.web.pages;

import delta.common.framework.web.PageParameters;

/**
 * Parameters for the 'person' page.
 * @author DAM
 */
public class PersonPageParameters extends PageParameters
{
  public static final String ACTION_VALUE="PERSON";
  public static final String PERSON_KEY="PERSON_KEY";
  private long _mainPersonKey;

  public PersonPageParameters(long mainPersonKey)
  {
    super("genea");
    setMainPersonKey(mainPersonKey);
  }

  public final void setMainPersonKey(long mainPersonKey)
  {
    _mainPersonKey=mainPersonKey;
    _parameters.put(PERSON_KEY,Long.valueOf(mainPersonKey));
  }

  public long getMainPersonKey()
  {
    return _mainPersonKey;
  }

  @Override
  public String getAction()
  {
    return ACTION_VALUE;
  }
}
