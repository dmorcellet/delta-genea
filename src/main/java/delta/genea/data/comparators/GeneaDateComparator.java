package delta.genea.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

import delta.genea.data.GeneaDate;

/**
 * A comparator for <tt>GeneaDate</tt>s.
 * @author DAM
 */
public class GeneaDateComparator implements Comparator<GeneaDate>, Serializable
{
  private static final long serialVersionUID=1L;

  public int compare(GeneaDate gd1, GeneaDate gd2)
  {
    if (gd1!=null)
    {
      if (gd2!=null)
      {
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
        if (infos1!=null)
        {
          if (infos2!=null)
          {
            int ret=infos1.compareTo(infos2);
            return ret;
          }
          return 1;
        }
        if (infos2!=null) return -1;
        return 0;
      }
      return 1;
    }
    if (gd2!=null) return -1;
    return 0;
  }
}
