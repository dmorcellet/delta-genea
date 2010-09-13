package delta.genea.data.dates;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Test date information.
 * @author DAM
 */
public class TestDateInformation extends TestCase
{
  private static String[] SAMPLES=
  { "01-1688ca","04-06-1688/10-11-1688","05-02-1688av","05-1710av","17-08-1613ap"};
  
  /**
   * Constructor.
   */
  public TestDateInformation()
  {
    super("Date information");
  }

  /**
   * Test date information building from string.
   */
  public void testDateInformationParsing()
  {
    for(int i=0;i<SAMPLES.length;i++)
    {
      DateInformation date=DateInformation.fromString(SAMPLES[i]);
      System.out.println(SAMPLES[i]+" --> "+date);
      Assert.assertNotNull(date);
      String dateStr=date.toString();
      Assert.assertEquals(SAMPLES[i],dateStr);
    }
  }
}
