package delta.genea.web.pages;

import delta.common.framework.web.PageParameters;

/**
 * Parameters for the 'image' page.
 * @author DAM
 */
public class ImagePageParameters extends PageParameters
{
  /**
   * Value of action parameter. 
   */
  public static final String ACTION_VALUE="IMAGE";
  private static final String NAME="NAME";
  private static final String DIR="DIR";
  private String _dir;
  private String _name;

  /**
   * Constructor.
   * @param dir Directory.
   * @param name Image name.
   */
  public ImagePageParameters(String dir, String name)
  {
    super("genea");
    setDir(dir);
    setName(name);
  }

  /**
   * Set the image name.
   * @param name Name to set.
   */
  public final void setName(String name)
  {
    _name=name;
    _parameters.put(NAME,_name);
  }

  /**
   * Get the image name.
   * @return An image name.
   */
  public String getName()
  {
    return _name;
  }

  /**
   * Set the directory key (ACTS, PICTURES, ...).
   * @param dir Directory key to set.
   */
  public final void setDir(String dir)
  {
    _dir=dir;
    _parameters.put(DIR,_dir);
  }

  /**
   * Get the directory key.
   * @return the directory key.
   */
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
