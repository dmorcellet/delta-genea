package delta.genea.time;

import java.util.Calendar;
import java.util.Date;

/**
 * Represents a date in the gregorian calendar.
 * @author DAM
 */
public class GregorianDate
{
  private static final int FIRST_GREGORIAN_YEAR=1582;
  private static final byte FIRST_GREGORIAN_DAY=15;
  private static final byte FIRST_GREGORIAN_MONTH=10;

  private static final byte[] DAYS_FOR_MONTH=
      {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
  private static final String[] MONTH_LABELS=
      {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
  private static final String[] DAY_OF_THE_WEEK_LABELS=
      {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};

  private byte _dayOfMonth;
  private byte _month;
  private int _year;

  /**
   * Constructor.
   */
  public GregorianDate()
  {
    _dayOfMonth=1;
    _month=1;
    _year=0;
  }

  /**
   * Constructor.
   * @param date Number of mlliseconds since Epoch.
   */
  public GregorianDate(Long date)
  {
    Calendar calendar=Calendar.getInstance();
    long ms=0;
    if (date!=null)
    {
      ms=date.longValue();
    }
    calendar.setTimeInMillis(ms);
    _dayOfMonth=(byte)calendar.get(Calendar.DAY_OF_MONTH);
    _month=(byte)(1+calendar.get(Calendar.MONTH));
    _year=calendar.get(Calendar.YEAR);
    check();
  }

  /**
   * Constructor.
   * @param date Java date.
   */
  public GregorianDate(Date date)
  {
    this((date==null)?null:Long.valueOf(date.getTime()));
  }

  /**
   * Copy constructor.
   * @param date Source date.
   */
  public GregorianDate(GregorianDate date)
  {
    _dayOfMonth=date._dayOfMonth;
    _month=date._month;
    _year=date._year;
    check();
  }

  /**
   * Full constructor.
   * @param dayOfMonth Day in the month (starting at 1).
   * @param month Month (starting at 1).
   * @param year Year (starting at 0).
   */
  public GregorianDate(byte dayOfMonth, byte month, int year)
  {
    _dayOfMonth=dayOfMonth;
    _month=month;
    _year=year;
    check();
  }

  /**
   * Check if this instance is valid.<br>
   * Throws an IllegalArgumentException if it is not.
   */
  private void check()
  {
    int maxDay=GregorianDate.getDaysForMonthAndYear(_month, _year);
    if(_dayOfMonth>maxDay)
    {
      throw new IllegalArgumentException("Bad dayOfMonth value for month/year :"+_dayOfMonth+" (max="+maxDay+")");
    }

    // Julian/gregorian calendar
    if(_year==FIRST_GREGORIAN_YEAR)
    {
      if(_month==FIRST_GREGORIAN_MONTH)
      {
        if((_dayOfMonth>4)&&(_dayOfMonth<FIRST_GREGORIAN_DAY))
        {
          throw new IllegalArgumentException("Date does not exist (step between julian & gregorian calendar) !");
        }
      }
    }
  }

  /**
   * Get the day of month.
   * @return a day number (starting at 1).
   */
  public byte getDayOfMonth()
  {
    return _dayOfMonth;
  }

  /**
   * Get the month.
   * @return a month number (starting at 1).
   */
  public byte getMonth()
  {
    return _month;
  }

  /**
   * Get the label of the month.
   * @return the label of the month.
   */
  public String getMonthLabel()
  {
    return getLabelForMonth(_month);
  }

  /**
   * Get the year.
   * @return a year number (starting at 0).
   */
  public int getYear()
  {
    return _year;
  }

  /**
   * Get the number of days left in the current month.
   * @return A number of days.
   */
  public int getDaysLeftInMonth()
  {
    return(getDaysForMonthAndYear(_month, _year)-_dayOfMonth);
  }

  /**
   * Get the number of days left in the current year.
   * @return A number of days.
   */
  public int getDaysLeftInYear()
  {
    int ret=getDaysLeftInMonth();

    byte m=_month;
    while(m<12)
    {
      m++;
      ret+=GregorianDate.getDaysForMonthAndYear(m, _year);
    }

    return ret;
  }

  /**
   * Get the day in this year.
   * @return A day index, starting at one.
   */
  public int getDayInYear()
  {
    int ret=0;
    for(byte i=1;i<_month;i++)
    {
      ret+=getDaysForMonthAndYear(i, _year);
    }

    ret+=_dayOfMonth;

    return ret;
  }

  /**
   * Get the day of the week.
   * @return A day of the week (monday=1).
   */
  public byte getDayInWeek()
  {
    if(_year>=2500)
    {
      throw new IllegalStateException();
    }
    byte[] centuryCode=
        {4, 5, 0, 2, 4, 5, 0, 2, 4, 5};
    byte[] monthCode=
        {0, 3, 3, 6, 1, 4, 6, 2, 5, 0, 3, 5, 6, 2};

    int month=_month;
    int year=_year%100;
    int century=_year/100;

    if(!isJulian())
    {
      century=centuryCode[century-15];
    }
    int code=((((year>>2)*5+(year%4))%7)-century+27)%7;
    if((isBissextile(_year)&&(month<3)))
    {
      month+=12;
    }
    code=(code+monthCode[month-1])%7;
    return(byte)(1+((4+code+_dayOfMonth)%7));
  }

  /**
   * Get a label for the day of the week.
   * @return A displayable label.
   */
  public String getDayOfWeekLabel()
  {
    byte day=getDayInWeek();
    return getLabelForDayOfWeek(day);
  }

  /**
   * Set this date to the current day.
   */
  public void now()
  {
    Calendar c=Calendar.getInstance();
    c.setTimeInMillis(System.currentTimeMillis());
    _dayOfMonth=(byte)c.get(Calendar.DAY_OF_MONTH);
    _month=(byte)(c.get(Calendar.MONTH)+1);
    _year=c.get(Calendar.YEAR);
  }

  /**
   * Indicates if this date is in the gregorian era or not.
   * @return <code>true</code> if it is a gregorian date,
   * <code>false</code> otherwise.
   */
  public boolean isGregorian()
  {
    if(_year>FIRST_GREGORIAN_YEAR)
    {
      return true;
    }
    if(_year<FIRST_GREGORIAN_YEAR)
    {
      return false;
    }

    // We're in 1582
    if(_month>FIRST_GREGORIAN_MONTH)
    {
      return true;
    }
    if(_month<FIRST_GREGORIAN_MONTH)
    {
      return false;
    }

    // We're in October
    if(_dayOfMonth>=FIRST_GREGORIAN_DAY)
    {
      return true;
    }
    return (_dayOfMonth>4);
  }

  /**
   * Indicates if this date is in the julian era or not.
   * @return <code>true</code> if it is a julian date,
   * <code>false</code> otherwise.
   */
  public boolean isJulian()
  {
    if(_year>1582)
    {
      return false;
    }
    if(_year<1582)
    {
      return true;
    }

    // We're in 1582
    if(_month>FIRST_GREGORIAN_MONTH)
    {
      return false;
    }
    if(_month<FIRST_GREGORIAN_MONTH)
    {
      return true;
    }

    // We're in October
    if(_dayOfMonth>=FIRST_GREGORIAN_DAY)
    {
      return false;
    }
    return (_dayOfMonth<=4);
  }

  /**
   * Indicates if the given year is a leap year or not.
   * @param year Year to use.
   * @return <code>true</code> for a leap year, <code>false</code> otherwise.
   */
  public static boolean isBissextile(int year)
  {
    if((year<FIRST_GREGORIAN_YEAR)&&(year%100==0))
    {
      return true;
    }
    if((year>FIRST_GREGORIAN_YEAR)&&(year%400==0))
    {
      return true;
    }
    return ((year%100>0)&&(year%4==0));
  }

  /**
   * Indicates if the given date is before this one.
   * @param d Date to test.
   * @return <code>true</code> if it is, <code>false</code> otherwise.
   */
  public boolean isBefore(GregorianDate d)
  {
    if(_year<d._year)
    {
      return true;
    }
    if(_year>d._year)
    {
      return false;
    }

    if(_month<d._month)
    {
      return true;
    }
    if(_month>d._month)
    {
      return false;
    }
    return (_dayOfMonth<d._dayOfMonth);
  }

  /**
   * Indicates if the given date is before or equal to this one.
   * @param d Date to test.
   * @return <code>true</code> if it is, <code>false</code> otherwise.
   */
  public boolean isBeforeOrEqual(GregorianDate d)
  {
    if(_year<d._year)
    {
      return true;
    }
    if(_year>d._year)
    {
      return false;
    }

    if(_month<d._month)
    {
      return true;
    }
    if(_month>d._month)
    {
      return false;
    }
    return (_dayOfMonth<=d._dayOfMonth);
  }

  /**
   * Indicates if the given date is equal to this one.
   * @param date Date to test.
   * @return <code>true</code> if it is, <code>false</code> otherwise.
   */
  public boolean isEqual(GregorianDate date)
  {
    return((_year==date._year)&&(_month==date._month)&&(_dayOfMonth==date._dayOfMonth));
  }

  /**
   * Remove a day from this date.
   */
  public void removeADay()
  {
    // Special handling for julian->gregorian transition
    // 15/10/1582 is considered as 5/10/1582.
    if((_year==FIRST_GREGORIAN_YEAR)&&(_month==FIRST_GREGORIAN_MONTH)&&(_dayOfMonth==FIRST_GREGORIAN_DAY))
    {
      _dayOfMonth=4;
    }
    else
    {
      // Les autres dates ont un comportement 'normal'
      if(_dayOfMonth>1)
      {
        _dayOfMonth--;
      }
      else
      {
        if(_month>1)
        {
          _month--;
        }
        else
        {
          _month=12;
          if(_year>0)
          {
            _year--;
          }
          else
          {
            throw new IllegalStateException();
          }
        }
        _dayOfMonth=GregorianDate.getDaysForMonthAndYear(_month, _year);
      }
    }
  }

  /**
   * Get the number of days in the given month and year.
   * @param month Month to use.
   * @param year Year to use.
   * @return A count of days.
   */
  public static byte getDaysForMonthAndYear(byte month, int year)
  {
    if((month==2)&&(isBissextile(year)))
    {
      return 29;
    }
    return DAYS_FOR_MONTH[month-1];
  }

  /**
   * Get the number of days in the given year.
   * @param year Year to use.
   * @return A count of days.
   */
  public static int getDaysForYear(int year)
  {
    if (isBissextile(year))
    {
      return 366;
    }
    return 365;
  }

  /**
   * Add a day to this date.
   */
  public void addADay()
  {
    // Special handling for julian->gregorian transition
    // The day after 4/10/1582 is 15/10/1582.
    if((_year==FIRST_GREGORIAN_YEAR)&&(_month==FIRST_GREGORIAN_MONTH)&&(_dayOfMonth==4))
    {
      _dayOfMonth=FIRST_GREGORIAN_DAY;
      return;
    }

    if(_dayOfMonth<GregorianDate.getDaysForMonthAndYear(_month, _year))
    {
      _dayOfMonth++;
    }
    else
    {
      if(_month<12)
      {
        _month++;
      }
      else
      {
        _month=1;
        if(_year<=Integer.MAX_VALUE-1)
        {
          _year++;
        }
        else
        {
          throw new IllegalStateException();
        }
      }
      _dayOfMonth=1;
    }
  }

  /**
   * Add days to this date.
   * @param days Number of days to add.
   */
  public void addDays(int days)
  {
    if (isJulian())
    {
      byte saveMonth=_month;
      byte saveDay=_dayOfMonth;
      int saveYear=_year;
      unsafeAddDays(days);
      if (!isJulian())
      {
        // We crossed the 10 missing days or we went right into them
        // Let's go brutal
        _month=saveMonth;
        _dayOfMonth=saveDay;
        _year=saveYear;
        for(int i=0;i<days;i++)
        {
          addADay();
        }
      }
    }
    else
    {
      unsafeAddDays(days);
    }
  }

  private void unsafeAddDays(int daysToAdd)
  {
    if(daysToAdd<=getDaysLeftInMonth())
    {
      _dayOfMonth=(byte)(_dayOfMonth+daysToAdd);
    }
    else
    {
      if(daysToAdd<=getDaysLeftInYear())
      {
        daysToAdd-=(getDaysLeftInMonth()+1);
        _dayOfMonth=1;
        _month++;
        while(daysToAdd>0)
        {
          if(daysToAdd>=getDaysForMonthAndYear(_month, _year))
          {
            daysToAdd-=getDaysForMonthAndYear(_month, _year);
            _month++;
          }
          else
          {
            _dayOfMonth=(byte)(1+daysToAdd);
            daysToAdd=0;
          }
        }
      }
      else
      {
        daysToAdd-=getDaysLeftInYear()+1;
        _dayOfMonth=1;
        _month=1;
        _year++;
        unsafeAddDays(daysToAdd);
      }
    }
  }

  /**
   * Get the number of days since 1/1/0000.
   * @return A count of days.
   */
  public long getDays()
  {
    long ret=0;
    int year=_year;
    for(int i=0;i<year;i++)
    {
      ret+=getDaysForYear(i);
    }
    ret+=getDayInYear();
    return ret;
  }

  /**
   * Computes the number of days between d1 and d2.
   * The returned value is positive if d1 is after d2, is zero if d1 is d2, or
   * is negative if d1 is before d2.
   * @param d1 First date.
   * @param d2 Second date.
   * @return The computed value.
   */
  public static long getDifferenceInDays(GregorianDate d1, GregorianDate d2)
  {
    return d1.getDays()-d2.getDays();
  }

  /**
   * Get the label for a month.
   * @param month Month (starting at 1).
   * @return the label of the targeted month.
   */
  public static String getLabelForMonth(byte month)
  {
    return MONTH_LABELS[month-1];
  }

  /**
   * Get the label for a day of the week.
   * @param day Day of week (starting at 1).
   * @return the label of the targeted day of week.
   */
  public static String getLabelForDayOfWeek(byte day)
  {
    return DAY_OF_THE_WEEK_LABELS[day-1];
  }

  @Override
  public String toString()
  {
    return _dayOfMonth+" "+getLabelForMonth(_month)+" "+_year;
  }

  @Override
  public boolean equals(Object o)
  {
    if (!(o instanceof GregorianDate)) return false;
    GregorianDate gd=(GregorianDate)o;
    if (gd._dayOfMonth!=_dayOfMonth) return false;
    if (gd._month!=_month) return false;
    return (gd._year==_year);
  }

  @Override
  public int hashCode()
  {
    return _year;
  }
}
