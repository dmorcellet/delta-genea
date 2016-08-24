package delta.genea.hmi;

import delta.genea.data.Person;
import delta.genea.data.Sex;

/**
 * Test class for the 'edit person' form.
 * @author DAM
 */
public class MainTestEditPersonForm
{
  /**
   * Main method of this test.
   * @param args Not used.
   */
  public static void main(String[] args)
  {
    EditPersonForm form=new EditPersonForm(null,true);
    Person p=new Person(null);
    p.setLastName("LASTNAME");
    p.setFirstname("Firstname");
    p.setSex(Sex.MALE);
    p.setComments("Comments\non\nseveral\nlines!");
    form.fillWithData(p);
    form.setVisible(true);
  }
}
