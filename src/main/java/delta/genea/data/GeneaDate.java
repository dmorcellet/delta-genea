package delta.genea.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import delta.genea.time.GregorianDate;

public class GeneaDate
{
  private static final String EMPTY="";
  private Long _date;
  private String _infosDate;

  public GeneaDate()
  {
    _date=null;
    _infosDate=EMPTY;
  }

  public GeneaDate(Long date, String infos)
  {
    _date=date;
    if (infos==null)
    {
      infos=EMPTY;
    }
    _infosDate=infos;
  }

  public GeneaDate(Date date, String infos)
  {
    setDate(date);
    if (infos==null)
    {
      infos=EMPTY;
    }
    _infosDate=infos;
  }

  public Long getDate() { return _date; }
  public String getInfosDate() { return _infosDate; }

  public void setDate(Date d)
  {
    if (d!=null)
    {
      _date=Long.valueOf(d.getTime());
    }
    else
    {
      _date=null;
    }
  }
  public void setDate(Long date)
  {
    _date=date;
  }

  public void setInfosDate(String infos)
  {
    if (infos==null)
    {
      infos=EMPTY;
    }
    _infosDate=infos;
  }

  public String format(SimpleDateFormat sdf)
  {
    if (_date==null)
    {
      return _infosDate;
    }
    return sdf.format(new Date(_date.longValue()));
  }

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

  public static Integer computeAge(GeneaDate gd1, GeneaDate gd2)
  {
    // todo
    //Long d1=gd1.getDate();
    //Long d2=gd2.getDate();
    return null;
  }
}
