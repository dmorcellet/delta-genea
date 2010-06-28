package delta.genea.webhoover.gennpdc;

import java.util.Date;

/**
 * @author DAM
 */
public class BirthAct
{
  public String _lastName;
  public String _firstName;
  public Date _date;
  public String _place;
  public String _father;
  public String _mother;
  
  public BirthAct()
  {
  }

  public String toString()
  {
    return _lastName+" "+_firstName+" "+_place+" "+_date+" "+_father+" "+_mother;
  }
}
