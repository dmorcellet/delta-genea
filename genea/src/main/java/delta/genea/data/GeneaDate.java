package delta.genea.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import delta.genea.time.GregorianDate;

/**
 * Genealogic date (precise or approximative). 
 * @author DAM
 */
public class GeneaDate
{
  private static final String EMPTY="";
  private Long _date;
  private String _infosDate;

  /**
   * Constructor.
   */
  public GeneaDate()
  {
    _date=null;
    _infosDate=EMPTY;
  }

  /**
   * Full contructor.
   * @param date Exact date.
   * @param infos Approximative date data.
   */
  public GeneaDate(Long date, String infos)
  {
    _date=date;
    if (infos==null)
    {
      infos=EMPTY;
    }
    _infosDate=infos;
  }

  /**
   * Full contructor.
   * @param date Exact date.
   * @param infos Approximative date data.
   */
  public GeneaDate(Date date, String infos)
  {
    setDate(date);
    if (infos==null)
    {
      infos=EMPTY;
    }
    _infosDate=infos;
  }

  /**
   * Get the exact date, if any.
   * @return A date as a <code>Long</code>, or <code>null</code>.
   */
  public Long getDate()
  {
    return _date;
  }

  /**
   * Get the approximative date data, if any.
   * @return A date data string, or <code>null/code>.
   */
  public String getInfosDate()
  {
    return _infosDate;
  }

  /**
   * Set the exact date.
   * @param date Date to set.
   */
  public void setDate(Date date)
  {
    if (date!=null)
    {
      _date=Long.valueOf(date.getTime());
    }
    else
    {
      _date=null;
    }
  }

  /**
   * Set the exact date.
   * @param date Date to set.
   */
  public void setDate(Long date)
  {
    _date=date;
  }

  /**
   * Set the approximative date data.
   * @param infos Data to set.
   */
  public void setInfosDate(String infos)
  {
    if (infos==null)
    {
      infos=EMPTY;
    }
    _infosDate=infos;
  }

  /**
   * Get a readable string from this data.
   * @param sdf Date formatter.
   * @return A string.
   */
  public String format(SimpleDateFormat sdf)
  {
    if (_date==null)
    {
      return _infosDate;
    }
    return sdf.format(new Date(_date.longValue()));
  }

  /**
   * Get a simplified readable string from this data.
   * @return A string (year only for an exact date, approximative date otherwise.
   */
  public String getYearString()
  {
    if (_date!=null)
    {
      GregorianDate gd=new GregorianDate(_date);
      return String.valueOf(gd.getYear());
    }
    if (_infosDate.length()==0)
    {
      return "???";
    }
    return _infosDate;
  }

  /**
   * Compute an age (in years), between two dates.
   * @param gd1 First date.
   * @param gd2 Second date.
   * @return A years count.
   */
  public static Integer computeAge(GeneaDate gd1, GeneaDate gd2)
  {
    // TODO
    return null;
  }
}
