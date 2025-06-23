package delta.genea.data;

/**
 * A single title data for a person.
 * @author DAM
 */
public class TitleForPerson
{
  private Integer _year;
  private String _title;

  /**
   * Constructor.
   */
  public TitleForPerson()
  {
    _year=null;
    _title="";
  }

  /**
   * Get the title.
   * @return A title (may be empty by never <code>null</code>).
   */
  public String getTitle()
  {
    return _title;
  }

  /**
   * Set the title.
   * @param title Title to set.
   */
  public void setTitle(String title)
  {
    if (title==null)
    {
      title="";
    }
    _title=title;
  }

  /**
   * Get the year associated with this title.
   * @return A year (may be <code>null</code>).
   */
  public Integer getYear()
  {
    return _year;
  }

  /**
   * Set the year associated with this title.
   * @param year Year to set (may be <code>null</code>).
   */
  public void setYear(Integer year)
  {
    _year=year;
  }

  /**
   * Get a readable string that shows this title.
   * @return A string.
   */
  public String getLabel()
  {
    StringBuilder sb=new StringBuilder();
    sb.append(_title);
    if (_year!=null)
    {
      sb.append(" (");
      sb.append(_year);
      sb.append(')');
    }
    return sb.toString();
  }
}
