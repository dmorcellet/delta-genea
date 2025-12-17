package delta.genea.gedcom;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.utils.NumericTools;
import delta.common.utils.misc.LatineNumbers;
import delta.common.utils.text.StringSplitter;
import delta.common.utils.time.Month;
import delta.genea.data.GeneaDate;
import delta.genea.time.FrenchRevolutionCalendar;
import delta.genea.time.FrenchRevolutionDate;
import delta.genea.time.FrenchRevolutionMonth;
import delta.genea.time.GregorianDate;

/**
 * Utility methods related to GEDCOM.
 * @author DAM
 */
public class DateUtils
{
  private static final Logger LOGGER=LoggerFactory.getLogger(DateUtils.class);

  private static final String FRENCH_REVOLUTION_DATE_SEED="@#DFRENCH R@";
  // For Julian dates: "@#DJULIAN@"

  /**
   * Decode a GEDCOM date.
   * @param dateString Input string.
   * @return A genea date (never <code>null</code>).
   */
  public static GeneaDate decodeDate(String dateString)
  {
    GeneaDate ret=null;
    try
    {
      if (dateString.startsWith(FRENCH_REVOLUTION_DATE_SEED))
      {
        String dateStr=dateString.substring(FRENCH_REVOLUTION_DATE_SEED.length()).trim();
        return decodeFrenchRevolutionDate(dateStr);
      }
      String dateStr;
      String suffix="";
      if (dateString.startsWith("ABT"))
      {
        dateStr=dateString.substring(3);
        suffix="ca";
      }
      else if (dateString.startsWith("AFT"))
      {
        dateStr=dateString.substring(3);
        suffix="ap";
      }
      else if (dateString.startsWith("BEF"))
      {
        dateStr=dateString.substring(3);
        suffix="av";
      }
      else
      {
        dateStr=dateString;
      }
      dateStr=dateStr.trim();
      String[] strings=StringSplitter.split(dateStr,' ');

      if (strings.length==4)
      {
        ret=decode4Parts(dateStr,strings);
      }
      else if (strings.length==3)
      {
        ret=decode3Parts(dateStr,suffix,strings);
      }
      else if (strings.length==2)
      {
        ret=decode2Parts(suffix,strings);
      }
      else if (strings.length==1)
      {
        suffix=strings[0]+suffix;
        ret=new GeneaDate((Long)null,suffix);
      }
      else
      {
        LOGGER.error("Pb: number of spaces={} [{}]",Integer.valueOf(strings.length),dateString);
        ret=new GeneaDate((Long)null,dateString);
      }
    }
    catch (Exception e)
    {
      LOGGER.error("Cannot parse date: "+dateString,e);
      ret=new GeneaDate((Long)null,dateString);
    }
    return ret;
  }

  private static GeneaDate decodeFrenchRevolutionDate(String dateStr)
  {
    Date date=null;
    String dateInfos="";
    String[] strings=StringSplitter.split(dateStr,' ');
    int day=NumericTools.parseInt(strings[0],-1);
    FrenchRevolutionMonth month=getMonth(strings[1]);
    int year=LatineNumbers.convert(strings[3]);
    if ((day!=-1) && (month!=null) && (year>0))
    {
      FrenchRevolutionDate frDate=new FrenchRevolutionDate((byte)day,month,year);
      GregorianDate gDate=FrenchRevolutionCalendar.convert(frDate);
      Calendar c=Calendar.getInstance();
      c.set(gDate.getYear(),gDate.getMonth()-1,gDate.getDayOfMonth());
      date=c.getTime();
    }
    else
    {
      dateInfos=dateStr;
    }
    return new GeneaDate(date,dateInfos);
  }

  private static GeneaDate decode4Parts(String stringToUse, String[] strings)
  {
    String dateInfos=stringToUse;
    if (("BET".equals(strings[0])) && ("AND".equals(strings[2])))
    {
      int year1=NumericTools.parseInt(strings[1],0);
      int year2=NumericTools.parseInt(strings[3],0);

      if ((year1>0) && (year2>0))
      {
        dateInfos=year1+"/"+year2;
      }
      else
      {
        LOGGER.error("Invalid years in a between [{}]",stringToUse);
      }
    }
    else
    {
      LOGGER.error("Bad 'between' structure [{}]",stringToUse);
    }
    return new GeneaDate((Long)null,dateInfos);
  }

  private static GeneaDate decode3Parts(String dateStr, final String suffix, String[] strings)
  {
    Date date=null;
    String dateInfos=null;
    // Full date ?
    int day=NumericTools.parseInt(strings[0],1);
    int month=Month.decodeEnglishMonth(strings[1]);
    int year=NumericTools.parseInt(strings[2],0);
    if ((day>0)&&(month>0)&&(year>0))
    {
      if (suffix.isEmpty())
      {
        Calendar c=Calendar.getInstance();
        c.set(year,month-1,day);
        date=c.getTime();
      }
      else
      {
        StringBuilder sb=new StringBuilder();
        sb.append(day);
        sb.append('-');
        sb.append(month);
        sb.append('-');
        sb.append(year);
        sb.append(suffix);
        dateInfos=sb.toString();
      }
    }
    else
    {
      dateInfos=dateStr+suffix;
    }
    return new GeneaDate(date,dateInfos);
  }

  private static GeneaDate decode2Parts(final String suffix, String[] strings)
  {
    int month=Month.decodeEnglishMonth(strings[0]);
    int year=Integer.parseInt(strings[1]);
    StringBuilder sb=new StringBuilder();
    sb.append(month);
    sb.append('-');
    sb.append(year);
    sb.append(suffix);
    String dateInfos=sb.toString();
    return new GeneaDate((Long)null,dateInfos);
  }

  private static FrenchRevolutionMonth getMonth(String month)
  {
    FrenchRevolutionMonth ret=null;
    if ("BRUM".equals(month)) ret=FrenchRevolutionMonth.BRUMAIRE;
    if ("FLOR".equals(month)) ret=FrenchRevolutionMonth.FLOREAL;
    if ("FRIM".equals(month)) ret=FrenchRevolutionMonth.FRIMAIRE;
    if ("FRUC".equals(month)) ret=FrenchRevolutionMonth.FRUCTIDOR;
    if ("GERM".equals(month)) ret=FrenchRevolutionMonth.GERMINAL;
    if ("MESS".equals(month)) ret=FrenchRevolutionMonth.MESSIDOR;
    if ("NIVO".equals(month)) ret=FrenchRevolutionMonth.NIVOSE;
    if ("PLUV".equals(month)) ret=FrenchRevolutionMonth.PLUVIOSE;
    if ("PRAI".equals(month)) ret=FrenchRevolutionMonth.PRAIRIAL;
    if ("THER".equals(month)) ret=FrenchRevolutionMonth.THERMIDOR;
    if ("VEND".equals(month)) ret=FrenchRevolutionMonth.VENDEMIAIRE;
    if ("VENT".equals(month)) ret=FrenchRevolutionMonth.VENTOSE;
    return ret;
  }
}
