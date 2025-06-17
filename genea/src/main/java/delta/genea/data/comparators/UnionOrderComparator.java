package delta.genea.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

import delta.genea.data.Union;

/**
 * A comparator for <tt>Union</tt>s using the man/woman.
 * @author DAM
 */
public class UnionOrderComparator implements Comparator<Union>, Serializable
{
  private boolean _isMan;

  /**
   * Constructor.
   * @param isMan Use man or woman.
   */
  public UnionOrderComparator(boolean isMan)
  {
    _isMan=isMan;
  }

  @Override
  public int compare(Union u1, Union u2)
  {
    Integer order1=(_isMan)?u1.getManOrder():u1.getWomanOrder();
    Integer order2=(_isMan)?u2.getManOrder():u2.getWomanOrder();
    if (order2==null)
    {
      return (order1!=null)?1:0;
    }
    if (order1!=null)
    {
      return Integer.compare(order1.intValue(),order2.intValue());
    }
    return -1;
  }
}
