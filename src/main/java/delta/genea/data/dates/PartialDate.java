package delta.genea.data.dates;

import delta.common.utils.NumericTools;
import delta.common.utils.text.StringSplitter;

/**
 * Represents a partial date.
 * i.e. a date that can cope with missing information.
 * @author DAM
 */
public class PartialDate
{
  private Integer _dayOfMonth;
  private Integer _month;
  private Integer _year;

  /**
   * Constructor.
   */
  public PartialDate()
  {
    _dayOfMonth=null;
    _month=null;
    _year=null;
  }

  /**
   * Full constructor.
   * @param dayOfMonth Day of month.
   * @param month Month.
   * @param year Year.
   */
  public PartialDate(Integer dayOfMonth, Integer month, Integer year)
  {
    _dayOfMonth=dayOfMonth;
    _month=month;
    _year=year;
  }

  /**
   * Get the day of month.
   * @return the day of month.
   */
  public Integer getDayOfMonth()
  {
    return _dayOfMonth;
  }
  
  /**
   * Set the day of month.
   * @param value Value to set (may be <code>null</code>).
   */
  public void setDayOfMonth(Integer value)
  {
    _dayOfMonth=value;
  }

  /**
   * Get the month.
   * @return the month.
   */
  public Integer getMonth()
  {
    return _month;
  }
  
  /**
   * Set the month.
   * @param value Value to set (may be <code>null</code>).
   */
  public void setMonth(Integer value)
  {
    _month=value;
  }

  /**
   * Get the year.
   * @return the year.
   */
  public Integer getYear()
  {
    return _year;
  }
  
  /**
   * Set the year.
   * @param value Value to set (may be <code>null</code>).
   */
  public void setYear(Integer value)
  {
    _year=value;
  }

  /**
   * Indicates if this date has all possible information,
   * i.e day of month, month and year.
   * @return <code>true</code> if it does, <code>false</code> otherwise.
   */
  public boolean isComplete()
  {
    return ((_dayOfMonth!=null) && (_month!=null) && (_year!=null));
  }

  /**
   * Indicates if this date has no information,
   * i.e no day of month, nor month nor year.
   * @return <code>true</code> if it does, <code>false</code> otherwise.
   */
  public boolean isEmpty()
  {
    return ((_dayOfMonth==null) && (_month==null) && (_year==null));
  }

  @Override
  public String toString()
  {
    StringBuilder sb=new StringBuilder();
    if (_dayOfMonth!=null)
    {
      // append 2 digits day value
      int day=_dayOfMonth.intValue();
      sb.append((char)('0'+((day/10)%10)));
      sb.append((char)('0'+(day%10)));
      sb.append('-');
      
    }
    if ((sb.length()>0) || (_month!=null))
    {
      if (_month!=null)
      {
        // append 2 digits month value
        int month=_month.intValue();
        sb.append((char)('0'+((month/10)%10)));
        sb.append((char)('0'+(month%10)));
        sb.append('-');
      }
      else
      {
        sb.append("??-");
      }
    }
    if ((sb.length()>0) || (_year!=null))
    {
      if (_year!=null)
      {
        int year=_year.intValue();
        // append 4 digits year value
        sb.append((char)('0'+((year/1000)%10)));
        sb.append((char)('0'+((year/100)%10)));
        sb.append((char)('0'+((year/10)%10)));
        sb.append((char)('0'+(year%10)));
      }
      else
      {
        sb.append("????");
      }
    }
    return sb.toString();
  }

  /**
   * Build a partial date from a string representation.
   * Expected string format is :
   * <ul>
   * <li>DD-MM-AAAA for a full date,
   * <li>MM-AAAA for month and date,
   * <li>AAAA for a year.
   * </ul>
   * For each part: if it contains a valid numeric value, it is used as the value for the associated partial date field,
   * otherwise, it this field is set to <code>null</code>.
   * @param dateStr Input string.
   * @return A partial date or <code>null</code> if the string is not valid.
   */
  public static PartialDate fromString(String dateStr)
  {
    String[] parts=StringSplitter.split(dateStr,'-');

    PartialDate date=null;
    if (parts.length==3)
    {
      // Full date
      date=new PartialDate();
      date._dayOfMonth=NumericTools.parseInteger(parts[0],false);
      date._month=NumericTools.parseInteger(parts[1],false);
      date._year=NumericTools.parseInteger(parts[2],false);
    }
    else if (parts.length==2)
    {
      // Month+year
      date=new PartialDate();
      date._dayOfMonth=null;
      date._month=NumericTools.parseInteger(parts[0],false);
      date._year=NumericTools.parseInteger(parts[1],false);
    }
    else if (parts.length==1)
    {
      // Year
      date=new PartialDate();
      date._dayOfMonth=null;
      date._month=null;
      date._year=NumericTools.parseInteger(parts[0],false);
    }
    if ((date!=null) && (date.isEmpty()))
    {
      date=null;
    }
    return date;
  }
}
