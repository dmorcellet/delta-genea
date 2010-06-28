package delta.genea.time;

/**
 * Represents a gregorian year.
 * @author DAM
 */
public class GregorianYear
{
  private static final int FIRST_GREGORIAN_YEAR=1582;
  private int _year;

  /**
   * Constructor.
   * @param year Year number.
   */
  public GregorianYear(int year)
  {
    _year=year;
  }

  /**
   * Get the year number.
   * @return a year number (starting at 0).
   */
  public int getYear()
  {
    return _year;
  }

  public boolean isBissextile()
  {
    if((_year<FIRST_GREGORIAN_YEAR)&&(_year%100==0))
    {
      return true;
    }
    if((_year>FIRST_GREGORIAN_YEAR)&&(_year%400==0))
    {
      return true;
    }
    if((_year%100>0)&&(_year%4==0))
    {
      return true;
    }
    return false;
  }
}
