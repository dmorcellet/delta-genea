package delta.genea.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

import delta.genea.data.Act;

/**
 * A comparator for <tt>Act</tt>s using their date.
 * @author DAM
 */
public class ActDateComparator implements Comparator<Act>, Serializable
{
  @Override
  public int compare(Act a1, Act a2)
  {
    Long date1=a1.getDate();
    Long date2=a2.getDate();
    if (date2==null)
    {
      return (date1!=null)?1:0;
    }
    if (date1!=null)
    {
      return Long.compare(date1.longValue(),date2.longValue());
    }
    return -1;
  }
}
