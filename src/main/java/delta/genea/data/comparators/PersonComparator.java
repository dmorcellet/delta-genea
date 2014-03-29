package delta.genea.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

import delta.genea.data.GeneaDate;
import delta.genea.data.Person;

/**
 * A comparator for <tt>Person</tt>s.
 * @author DAM
 */
public class PersonComparator implements Comparator<Person>, Serializable
{
  private static final long serialVersionUID=1L;

  /**
   * Comparison criteria.
   * @author DAM
   */
  public enum COMPARISON_CRITERIA
  {
    /**
     * Birth date.
     */
    BIRTH_DATE
  }

  private COMPARISON_CRITERIA _criteria;
  private GeneaDateComparator _dateComparator;

  /**
   * Constructor.
   * @param criteria Comparison criteria.
   */
  public PersonComparator(COMPARISON_CRITERIA criteria)
  {
    _criteria=criteria;
    _dateComparator=new GeneaDateComparator();
  }

  public int compare(Person p1, Person p2)
  {
    if (_criteria==COMPARISON_CRITERIA.BIRTH_DATE)
    {
      GeneaDate birth1=p1.getBirthGeneaDate();
      GeneaDate birth2=p2.getBirthGeneaDate();
      int ret=_dateComparator.compare(birth1,birth2);
      return ret;
    }
    return 0;
  }
}
