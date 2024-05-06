package delta.genea.time;

import delta.common.utils.misc.LatineNumbers;

/**
 * Represents a date in the French revolution calendar.
 * @author DAM
 */
public class FrenchRevolutionDate
{
  private byte _day;
  private FrenchRevolutionMonth _month;
  private int _year;

  /**
   * Constructor.
   * @param day Day of the month.
   * @param month Month.
   * @param year Year.
   */
  public FrenchRevolutionDate(byte day, FrenchRevolutionMonth month, int year)
  {
    boolean ok=false;
    if(((day>=1)&&(day<=30))&&(year>=1))
    {
      if(month==FrenchRevolutionMonth.SANS_CULLOTIDES)
      {
        byte dayMax=5;
        if(FrenchRevolutionYear.isSextile(year))
        {
          dayMax++;
        }
        if(day<=dayMax)
        {
          ok=true;
        }
      }
      else
      {
        ok=true;
      }
    }

    if(!ok)
    {
      throw new IllegalArgumentException();
    }
    _day=day;
    _month=month;
    _year=year;
  }

  /**
   * Get the index of this date in the year.
   * @return 1 to 365 or 366.
   */
  public int getDayInYear()
  {
    int month=_month.getIndex();
    return(((month-1)*30)+_day);
  }

  /**
   * Get the day of the month.
   * @return the day of the month (1..30).
   */
  public byte getDay()
  {
    return _day;
  }

  /**
   * Get the month.
   * @return the month (FrenchRevolutionMonth.VENDEMIAIRE to FrenchRevolutionMonth.SANS_CULLOTIDES).
   */
  public FrenchRevolutionMonth getMonth()
  {
    return _month;
  }

  /**
   * Get the year.
   * @return the year (starting at 1).
   */
  public int getYear()
  {
    return _year;
  }

  @Override
  public String toString()
  {
    StringBuilder sb=new StringBuilder();
    sb.append(_day);
    sb.append(' ');
    sb.append(_month.getLabelForMonth());
    sb.append(' ');
    sb.append(LatineNumbers.convert(_year));
    return sb.toString();
  }
}
