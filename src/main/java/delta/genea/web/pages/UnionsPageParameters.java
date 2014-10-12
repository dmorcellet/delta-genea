package delta.genea.web.pages;

import delta.common.framework.web.PageParameters;

/**
 * Parameters for the 'unions' page.
 * @author DAM
 */
public class UnionsPageParameters extends PageParameters
{
  public static final String ACTION_VALUE="UNIONS";
  private static final String NAME="NAME";
  private String _name;
  private static final String KEY="KEY";
  private Long _key;

  public UnionsPageParameters(String name, Long key)
  {
    super("genea");
    setName(name);
    setKey(key);
  }

  public final void setName(String name)
  {
    _name=name;
    _parameters.put(NAME,_name);
  }

  public String getName()
  {
    return _name;
  }

  public final void setKey(Long key)
  {
    _key=key;
    _parameters.put(KEY,key);
  }

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
