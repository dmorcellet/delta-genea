package delta.common.utils.places;

/**
 * French department.
 * @author DAM
 */
public class FrenchDepartment
{
  private String _shortLabel;
  private String _label;

  /**
   * Constructor.
   * @param shortLabel Short label.
   * @param label Long label.
   */
  public FrenchDepartment(String shortLabel, String label)
  {
    _shortLabel=shortLabel;
    _label=label;
  }

  /**
   * Get the short label for this department.
   * @return a short label.
   */
  public String getShortLabel()
  {
    return _shortLabel;
  }

  /**
   * Get the long label for this department.
   * @return a long label.
   */
  public String getLabel()
  {
    return _label;
  }
}
