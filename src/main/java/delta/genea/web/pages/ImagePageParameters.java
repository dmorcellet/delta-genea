package delta.genea.web.pages;

import delta.common.framework.web.PageParameters;

/**
 * Parameters for the 'image' page.
 * @author DAM
 */
public class ImagePageParameters extends PageParameters
{
  public static final String ACTION_VALUE="IMAGE";
  private static final String NAME="NAME";
  private static final String DIR="DIR";
  private String _dir;
  private String _name;

  public ImagePageParameters(String dir, String name)
  {
    super("genea");
    setDir(dir);
    setName(name);
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

  public final void setDir(String dir)
  {
    _dir=dir;
    _parameters.put(DIR,_dir);
  }

  public String getDir()
  {
    return _dir;
  }

  @Override
  public String getAction()
  {
    return ACTION_VALUE;
  }
}
