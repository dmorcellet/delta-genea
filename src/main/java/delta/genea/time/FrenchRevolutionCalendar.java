package delta.genea.time;

/**
 * French revolution calendar.
 * @author DAM
 */
public class FrenchRevolutionCalendar
{
  /**
   * First day of this calendar (as a gregorian date).
   */
  public static final GregorianDate FIRST_DAY=new GregorianDate((byte)22, (byte)9, 1792);
  /**
   * Last day of this calendar (as a gregorian date).
   */
  public static final GregorianDate LAST_DAY=new GregorianDate((byte)1, (byte)1, 1806);

  /**
   * Convert a gregorian date to a French revolution date.
   * @param gregorianDate Source gregorian date.
   * @return A French revolution date, or <code>null</code> if the specified gregorian
   * date has no associated French revolution date.
   */
  public static FrenchRevolutionDate convert(GregorianDate gregorianDate)
  {
    FrenchRevolutionDate ret=null;

    if ((FIRST_DAY.isBefore(gregorianDate))
        && (gregorianDate.isBefore(LAST_DAY)))
    {
      long delta=GregorianDate.getDifferenceInDays(gregorianDate, FIRST_DAY);

      int year=1;
      while(delta>=FrenchRevolutionYear.getNumberOfDays(year))
      {
        delta-=FrenchRevolutionYear.getNumberOfDays(year);
        year++;
      }

      // Month
      byte monthIndex=1;
      while(delta>=30)
      {
        delta-=30;
        monthIndex++;
      }

      // Day
      byte day=(byte)(delta+1);
      FrenchRevolutionMonth month=FrenchRevolutionMonth.fromIndex(monthIndex);
      ret=new FrenchRevolutionDate(day, month, year);
    }

    return ret;
  }

  /**
   * Convert a French revolution date into a gregorian date.
   * @param frenchRevolutionDate Source French revolution date.
   * @return A gregorian date.
   */
  public static GregorianDate convert(FrenchRevolutionDate frenchRevolutionDate)
  {
    int nbDays=0;
    int a=frenchRevolutionDate.getYear();
    for(int i=1;i<a;i++)
    {
      nbDays+=FrenchRevolutionYear.getNumberOfDays(i);
    }
    nbDays+=frenchRevolutionDate.getDayInYear();
    nbDays--;

    GregorianDate dateBase=new GregorianDate((byte)22, (byte)9, 1792);
    dateBase.addDays(nbDays);
    return dateBase;
  }
}
