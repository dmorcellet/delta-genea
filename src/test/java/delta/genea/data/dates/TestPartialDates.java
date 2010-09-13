package delta.genea.data.dates;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestPartialDates extends TestCase
{
  private static String[] SAMPLES=
  { "01-1688","04-06-1688","05-??-1688","1710","17-08-??", "??-??-??", "??-12-??" };
  
  /**
   * Constructor.
   */
  public TestPartialDates()
  {
    super("Partial dates");
  }

  public void testConversions()
  {
    for(int i=0;i<SAMPLES.length;i++)
    {
      PartialDate date=PartialDate.fromString(SAMPLES[i]);
      System.out.println(SAMPLES[i]+" --> "+date);
    }
  }
}
