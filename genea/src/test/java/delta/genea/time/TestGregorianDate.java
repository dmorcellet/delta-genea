package delta.genea.time;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Test for gregorian dates.
 * @author DAM
 */
public class TestGregorianDate extends TestCase
{
  private static final Logger LOGGER=LoggerFactory.getLogger(TestGregorianDate.class);

  /**
   * Constructor.
   */
  public TestGregorianDate()
  {
    super("Gregorian dates test");
  }

  /**
   * Add days test.
   * @throws Exception
   */
  public void testAddDays() throws Exception
  {
    Random rnd=new Random(System.currentTimeMillis());
    long start=System.currentTimeMillis();
    int nbOccurrences=10000;
    for(int i=0;i<nbOccurrences;i++)
    {
      // Random start date
      byte month=(byte)(rnd.nextInt(12)+1);
      int year=rnd.nextInt(65000);
      byte day=(byte)(1+rnd.nextInt(GregorianDate.getDaysForMonthAndYear(month, year)));

      boolean validDate=false;
      GregorianDate date;
      int daysToAdd=0;
      try
      {
        date=new GregorianDate(day, month, year);
        validDate=true;

        daysToAdd=rnd.nextInt(5000);

        // use addDays
        boolean computation1Exploded=false;
        GregorianDate computedDate1=null;
        try
        {
          computedDate1=new GregorianDate(date);
          computedDate1.addDays(daysToAdd);
        }
        catch(Exception ex1)
        {
          computation1Exploded=true;
        }

        // use addDay daysToAdd times
        boolean computation2Exploded=false;
        GregorianDate computedDate2=null;
        try
        {
          computedDate2=new GregorianDate(date);
          for(int k=0;k<daysToAdd;k++)
          {
            computedDate2.addADay();
          }
        }
        catch(Exception ex2)
        {
          computation2Exploded=true;
        }

        // Aftermath
        // - Both computation exploded : OK
        // - Only one exploded : KO
        // - If both computations succeeded, computed dates must be equal.
        if (computation1Exploded==computation2Exploded)
        {
          if (!computation1Exploded)
          {
            String msg=date+".addDays("+daysToAdd+")="+computedDate1+ " / "+date+".addADay() "+daysToAdd+" times="+computedDate2;
            Assert.assertEquals(msg,computedDate1,computedDate2);
          }
        }
        else
        {
          if (computation1Exploded)
          {
            Assert.assertTrue(date+".addDays("+daysToAdd+") exploded !",false);
          }
          if (computation2Exploded)
          {
            Assert.assertTrue(date+".addADay() "+daysToAdd+" times exploded !",false);
          }
        }
      }
      catch(Exception e)
      {
        if (validDate)
        {
          throw e;
        }
      }
    }

    long end=System.currentTimeMillis();
    long duration=end-start;
    LOGGER.info("{} occurrences in {}ms",Integer.valueOf(nbOccurrences),Long.valueOf(duration));
  }

  /**
   * Day of the week test.
   */
  public void testDayOfTheWeek()
  {
    // Checks if date incrementation is coherent with day of the week incrementation
    // In the beginning was void...
    GregorianDate d=new GregorianDate((byte)1, (byte)1, 0);
    byte day=d.getDayInWeek();

    // Test until 31/12/2499 only !
    for(int i=0;i<913108;i++)
    {
      d.addADay();
      day++;
      if(day==8) day=1;
      if (d.getDayInWeek()!=day)
      {
        String label1=GregorianDate.getLabelForDayOfWeek(day);
        String label2=GregorianDate.getLabelForDayOfWeek(d.getDayInWeek());
        LOGGER.error("Day ({})={} and not {}",d,label1,label2);
      }
      Assert.assertEquals(d.getDayInWeek(),day);
    }
  }

  /**
   * Test for the gap between julian and gregorian calendar.
   */
  public void testJulianToGregorianStep()
  {
    GregorianDate lastJulianDate=new GregorianDate((byte)4, (byte)10, 1582);
    Assert.assertTrue(lastJulianDate.isJulian());
    Assert.assertFalse(lastJulianDate.isGregorian());

    GregorianDate firstGregorianDate=new GregorianDate((byte)15, (byte)10, 1582);
    Assert.assertTrue(firstGregorianDate.isGregorian());
    Assert.assertFalse(firstGregorianDate.isJulian());

    GregorianDate d=new GregorianDate(lastJulianDate);
    d.addADay();
    Assert.assertEquals(d,firstGregorianDate);
  }
}
