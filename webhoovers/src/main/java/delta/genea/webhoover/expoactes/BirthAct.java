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
  private String _lastName;
  /**
   * First name.
   */
  private String _firstName;
  /**
   * Act date.
   */
  private Date _date;
  /**
   * Act place.
   */
  private String _place;
  /**
   * Father data.
   */
  private String _father;
  /**
   * Mother data.
   */
  private String _mother;

  /**
   * Constructor.
   */
  public BirthAct()
  {
    // Nothing to do !! 
  }

  /**
   * @return the lastName
   */
  public String getLastName()
  {
    return _lastName;
  }

  /**
   * @param lastName the lastName to set
   */
  public void setLastName(String lastName)
  {
    _lastName=lastName;
  }

  /**
   * @return the firstName
   */
  public String getFirstName()
  {
    return _firstName;
  }

  /**
   * @param firstName the firstName to set
   */
  public void setFirstName(String firstName)
  {
    _firstName=firstName;
  }

  /**
   * @return the date
   */
  public Date getDate()
  {
    return _date;
  }

  /**
   * @param date the date to set
   */
  public void setDate(Date date)
  {
    _date=date;
  }

  /**
   * @return the place
   */
  public String getPlace()
  {
    return _place;
  }

  /**
   * @param place the place to set
   */
  public void setPlace(String place)
  {
    _place=place;
  }

  /**
   * @return the father
   */
  public String getFather()
  {
    return _father;
  }

  /**
   * @param father the father to set
   */
  public void setFather(String father)
  {
    _father=father;
  }

  /**
   * @return the mother
   */
  public String getMother()
  {
    return _mother;
  }

  /**
   * @param mother the mother to set
   */
  public void setMother(String mother)
  {
    _mother=mother;
  }

  @Override
  public String toString()
  {
    return _lastName+" "+_firstName+" "+_place+" "+_date+" "+_father+" "+_mother;
  }
}
