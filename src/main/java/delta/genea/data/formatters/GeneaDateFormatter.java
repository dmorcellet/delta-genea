package delta.genea.data.formatters;

import delta.common.utils.text.TextFormatter;
import delta.genea.data.GeneaDate;
import delta.genea.time.FrenchRevolutionCalendar;
import delta.genea.time.FrenchRevolutionDate;
import delta.genea.time.GregorianDate;

/**
 * A text formatter used for 'genea' dates.
 * @author DAM
 */
public class GeneaDateFormatter extends TextFormatter
{
  private boolean _useRevolutionaryCalendar;

  /**
   * Constructor.
   */
  public GeneaDateFormatter()
  {
    _useRevolutionaryCalendar=true;
  }

  public void setUseRevolutionaryCalendar(boolean value) { _useRevolutionaryCalendar=value; }

  /**
   * Format the specified object into the given <tt>StringBuilder</tt>.
   * @param o Object to format.
   * @param sb Output string builder.
   */
  @Override
  public void format(Object o, StringBuilder sb)
  {
    GeneaDate gdate=null;
    if (o instanceof GeneaDate) gdate=(GeneaDate)o;
    if (gdate==null) return;

    Long date=gdate.getDate();
    String infosDate=gdate.getInfosDate();

    if (date!=null)
    {
      GregorianDate gd=new GregorianDate(date);
      sb.append(gd.getDayOfWeekLabel());
      sb.append(' ');
      sb.append(gd.getDayOfMonth());
      sb.append(' ');
      sb.append(gd.getMonthLabel());
      sb.append(' ');
      sb.append(gd.getYear());
      if (_useRevolutionaryCalendar)
      {
        FrenchRevolutionDate fRd=FrenchRevolutionCalendar.convert(gd);
        if (fRd!=null)
        {
          sb.append(" (");
          sb.append(fRd);
          sb.append(')');
        }
      }
    }
    else if ((infosDate==null) || (infosDate.length()==0))
    {
      sb.append("???");
    }
    else
    {
      sb.append(infosDate);
    }
  }
}
