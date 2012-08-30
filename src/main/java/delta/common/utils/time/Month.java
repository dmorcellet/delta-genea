package delta.common.utils.time;

public abstract class Month
{
  private static final String[] UK_MONTH_TRIGRAMS={"JAN",
    "FEB", "MAR", "APR", "MAY",
    "JUN", "JUL", "AUG", "SEP",
    "OCT", "NOV", "DEC" };

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
