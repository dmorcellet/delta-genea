package delta.genea.data.dates;

/**
 * Date information.
 * @author DAM
 */
public class DateInformation
{
  /**
   * Between separator.
   */
  private static final char BETWEEN_SEPARATOR='/';
  /**
   * Before.
   */
  private static final String AV="av";
  /**
   * After.
   */
  private static final String AP="ap";
  /**
   * About.
   */
  private static final String CA="ca";

  /**
   * Date relationship operator.
   * @author DAM
   */
  public enum DateOperator
  {
    /**
     * 'about' operator.
     */
    ABOUT,
    /**
     * 'before' operator.
     */
    BEFORE,
    /**
     * 'after' operator.
     */
    AFTER,
    /**
     * 'between' operator.
     */
    BETWEEN
  }
  
  private DateOperator _operator;
  private PartialDate _date1;
  private PartialDate _date2;
  
  /**
   * Build an 'about' date information.
   * @param date Partial date.
   * @return A date information.
   */
  public static DateInformation about(PartialDate date)
  {
    DateInformation ret=null;
    if (date!=null)
    {
      ret=new DateInformation();
      ret._operator=DateOperator.ABOUT;
      ret._date1=date;
    }
    return ret;
  }

  /**
   * Build an 'before' date information.
   * @param date Partial date.
   * @return A date information.
   */
  public static DateInformation before(PartialDate date)
  {
    DateInformation ret=null;
    if (date!=null)
    {
      ret=new DateInformation();
      ret._operator=DateOperator.BEFORE;
      ret._date1=date;
    }
    return ret;
  }

  /**
   * Build an 'after' date information.
   * @param date Partial date.
   * @return A date information.
   */
  public static DateInformation after(PartialDate date)
  {
    DateInformation ret=null;
    if (date!=null)
    {
      ret=new DateInformation();
      ret._operator=DateOperator.AFTER;
      ret._date1=date;
    }
    return ret;
  }

  /**
   * Build an 'between' date information.
   * @param date1 Partial date 1.
   * @param date2 Partial date 2.
   * @return A date information.
   */
  public static DateInformation between(PartialDate date1, PartialDate date2)
  {
    DateInformation ret=null;
    if ((date1!=null) && (date2!=null))
    {
      ret=new DateInformation();
      ret._operator=DateOperator.BETWEEN;
      ret._date1=date1;
      ret._date2=date2;
    }
    return ret;
  }

  /**
   * Get date operator.
   * @return the date operator.
   */
  public DateOperator getOperator()
  {
    return _operator;
  }

  /**
   * Get the partial date 1.
   * @return the partial date 1.
   */
  public PartialDate getDate1()
  {
    return _date1;
  }

  /**
   * Get the partial date 2.
   * @return the partial date 2.
   */
  public PartialDate getDate2()
  {
    return _date2;
  }
  
  @Override
  public String toString()
  {
    StringBuilder sb=new StringBuilder();
    if (_date1!=null)
    {
      sb.append(_date1);
    }
    else
    {
      sb.append("???");
    }
    if (_operator==DateOperator.ABOUT)
    {
      sb.append(CA);
    }
    else if (_operator==DateOperator.AFTER)
    {
      sb.append(AP);
    }
    else if (_operator==DateOperator.BEFORE)
    {
      sb.append(AV);
    }
    else if (_operator==DateOperator.BETWEEN)
    {
      sb.append(BETWEEN_SEPARATOR);
      if (_date2!=null)
      {
        sb.append(_date2);
      }
      else
      {
        sb.append("???");
      }
    }
    return sb.toString();
  }

  /**
   * Build a date information from a string representation.
   * Expected string format is :
   * <ul>
   * <li>{date}ca for an 'about' date information,
   * <li>{date}av for a 'before' date information,
   * <li>{date}ap for an 'after' date information,
   * <li>or {date1}/{date2} for a 'between' date information.
   * </ul>
   * Each date is expected to be in the partial date format..
   * @param dateInfoStr Input string.
   * @return A date information or <code>null</code> if the string is not valid.
   */
  public static DateInformation fromString(String dateInfoStr)
  {
    DateInformation ret=null;
    if ((dateInfoStr!=null) && (dateInfoStr.length()>0))
    {
      if (dateInfoStr.endsWith(CA))
      {
        PartialDate date=PartialDate.fromString(dateInfoStr.substring(0,dateInfoStr.length()-CA.length()));
        ret=about(date);
      }
      else if (dateInfoStr.endsWith(AV))
      {
        PartialDate date=PartialDate.fromString(dateInfoStr.substring(0,dateInfoStr.length()-AV.length()));
        ret=before(date);
      }
      else if (dateInfoStr.endsWith(AP))
      {
        PartialDate date=PartialDate.fromString(dateInfoStr.substring(0,dateInfoStr.length()-AP.length()));
        ret=after(date);
      }
      else
      {
        int index=dateInfoStr.indexOf(BETWEEN_SEPARATOR);
        if (index!=-1)
        {
          PartialDate date1=PartialDate.fromString(dateInfoStr.substring(0,index));
          PartialDate date2=PartialDate.fromString(dateInfoStr.substring(index+1));
          ret=between(date1,date2);
        }
      }
    }
    return ret;
  }
}
