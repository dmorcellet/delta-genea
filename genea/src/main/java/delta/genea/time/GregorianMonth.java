package delta.genea.time;

/**
 * Represents a gregorian month.
 * @author DAM
 */
public class GregorianMonth
{
  private int _month;

  /**
   * Constructor.
   * @param month Month number.
   */
  public GregorianMonth(int month)
  {
    _month=month;
  }

  /**
   * Get the month number.
   * @return a month number (starting at 1).
   */
  public int getMonth()
  {
    return _month;
  }
}
