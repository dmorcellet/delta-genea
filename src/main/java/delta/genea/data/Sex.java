package delta.genea.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sex of a person.
 * @author DAM
 */
public final class Sex
{
  private static final List<Sex> _instances=new ArrayList<Sex>();
  /**
   * Male.
   */
  public static final Sex MALE=new Sex('M',"Male");
  /**
   * Female.
   */
  public static final Sex FEMALE=new Sex('F',"Female");
  /**
   * Unknown.
   */
  public static final Sex UNKNOWN=new Sex('?',"Unknown");

  private char _value;
  private String _label;

  /**
   * Get a list of all possible sexes.
   * @return a list of sex instances.
   */
  public static List<Sex> getListOfSex()
  {
    return Collections.unmodifiableList(_instances);
  }

  private Sex(char value, String label)
  {
    _value=value;
    _label=label;
    _instances.add(this);
  }

  /**
   * Get a sex instance from its char code.
   * @param value Code to use.
   * @return A sex instance or <code>UNKNOWN</code> if not found.
   */
  public static Sex getFromValue(char value)
  {
    Sex ret=UNKNOWN;
    int nb=_instances.size();
    Sex current;
    for(int i=0;i<nb;i++)
    {
      current=_instances.get(i);
      if (current._value==value)
      {
        ret=current;
        break;
      }
    }
    return ret;
  }

  /**
   * Get the associated char value.
   * @return A char value that identifies this sex.
   */
  public char getValue()
  {
  	return _value;
  }

  /**
   * Get a hash code for this instance.
   * @see java.lang.Object#hashCode()
   * @return A hash code for this instance.
   */
  @Override
  public int hashCode()
  {
    return _value;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    return super.equals(obj);
  }

  @Override
  public String toString()
  {
    return _label;
  }
}
