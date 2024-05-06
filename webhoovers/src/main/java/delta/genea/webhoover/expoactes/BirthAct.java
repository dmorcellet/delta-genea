package delta.genea.webhoover.expoactes;

import java.util.Date;

/**
 * Data for a single birth act.
 * @author DAM
 */
public class BirthAct
{
  /**
   * Last name.
   */
  public String _lastName;
  /**
   * First name.
   */
  public String _firstName;
  /**
   * Act date.
   */
  public Date _date;
  /**
   * Act place.
   */
  public String _place;
  /**
   * Father data.
   */
  public String _father;
  /**
   * Mother data.
   */
  public String _mother;

  /**
   * Constructor.
   */
  public BirthAct()
  {
    // Nothing to do !! 
  }

  @Override
  public String toString()
  {
    return _lastName+" "+_firstName+" "+_place+" "+_date+" "+_father+" "+_mother;
  }
}
