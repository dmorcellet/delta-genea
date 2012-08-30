package delta.common.utils.places;

public class FrenchDepartment
{
  private String _shortLabel;
  private String _label;

  public FrenchDepartment(String shortLabel, String label)
  {
    _shortLabel=shortLabel;
    _label=label;
  }

  public String getShortLabel()
  {
    return _shortLabel;
  }

  public String getLabel()
  {
    return _label;
  }
}
