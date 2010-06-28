package delta.genea.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

import delta.genea.data.GeneaDate;

/**
 * A comparator for <tt>GeneaDate</tt>s.
 * @author DAM
 */
public class GeneaDateComparator implements Comparator<Object>, Serializable
{
  private static final long serialVersionUID=1L;

  public int compare(Object o1, Object o2)
  {
    GeneaDate gd1=(GeneaDate)o1;
    GeneaDate gd2=(GeneaDate)o2;
    Long d1=gd1.getDate();
    Long d2=gd2.getDate();
    if (d1!=null)
    {
      if (d2!=null)
      {
        int ret=d1.compareTo(d2);
        return ret;
      }
      return 1;
    }
    if (d2!=null) return -1;
    String infos1=gd1.getInfosDate();
    String infos2=gd2.getInfosDate();
    int ret=infos1.compareTo(infos2);
    return ret;
  }
}
