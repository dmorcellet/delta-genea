package delta.genea.time;

/**
 * Tool methods related to French revolution years.
 * @author DAM
 */
public class FrenchRevolutionYear
{
  /**
   * Indicates if the indicated year has one extra day or not.
   * @param year Targeted year (starting at 1).
   * @return <code>true</code> if it does, <code>false</code> otherwise.
   */
  public static boolean isSextile(int year)
  {
    return(((year-3)%4)==0);
  }

  /**
   * Get the number of days for a French revolution year.
   * @param year Targeted year (starting at 1).
   * @return A number of days.
   */
  public static int getNumberOfDays(int year)
  {
    if (isSextile(year))
    {
      return 366;
    }
    return 365;
  }
}
