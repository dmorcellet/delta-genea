package delta.common.utils.time;

/**
 * Month-related tools.
 * @author DAM
 */
public abstract class Month
{
  private static final String[] UK_MONTH_TRIGRAMS={"JAN",
    "FEB", "MAR", "APR", "MAY",
    "JUN", "JUL", "AUG", "SEP",
    "OCT", "NOV", "DEC" };

  /**
   * Decode a month trigram.
   * @param month Month trigram to use.
   * @return A month index (starting at 1), or 0 if not found.
   */
  public static int decodeEnglishMonth(String month)
  {
    for(int i=0;i<UK_MONTH_TRIGRAMS.length;i++)
    {
      if (UK_MONTH_TRIGRAMS[i].equals(month))
        return i+1;
    }
    return 0;
  }
}
