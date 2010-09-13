package delta.genea.data.dates;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Test partial dates.
 * @author DAM
 */
public class TestPartialDates extends TestCase
{
  private static String[] SAMPLES=
  { "01-1688","04-06-1688","05-??-1688","1710","17-08-??","??-??-??","??-12-??","??-??-1710","??-190" };
  private static String[] NORMALIZED=
  { "01-1688","04-06-1688","05-??-1688","1710","17-08-????",null,"12-????","1710","0190" };
  
  /**
   * Constructor.
   */
  public TestPartialDates()
  {
    super("Partial dates");
  }

  /**
   * Test build partial dates from string.
   */
  public void testPartialDatesParsing()
  {
    Assert.assertEquals(SAMPLES.length,NORMALIZED.length);
    for(int i=0;i<SAMPLES.length;i++)
    {
      PartialDate date=PartialDate.fromString(SAMPLES[i]);
      String dateStr=null;
      if (date!=null)
      {
        dateStr=date.toString();
      }
      System.out.println(SAMPLES[i]+" --> "+dateStr);
      Assert.assertEquals(NORMALIZED[i],dateStr);
    }
  }
}
