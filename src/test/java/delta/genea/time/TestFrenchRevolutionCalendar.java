package delta.genea.time;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestFrenchRevolutionCalendar extends TestCase
{
  /**
   * Constructor.
   */
  public TestFrenchRevolutionCalendar()
  {
    super("French revolution calendar test");
  }

  private void checkToGregorian(FrenchRevolutionDate dR, GregorianDate dG)
  {
    GregorianDate d=FrenchRevolutionCalendar.convert(dR);
    Assert.assertEquals(dG,d);
  }

  private void checkToFrenchRevolution(GregorianDate dG, FrenchRevolutionDate dR)
  {
    FrenchRevolutionDate d=FrenchRevolutionCalendar.convert(dG);
    Assert.assertEquals(dR,d);
  }

  public void testConversions()
  {
    FrenchRevolutionDate[] rDates={
        new FrenchRevolutionDate((byte)1, FrenchRevolutionMonth.VENDEMIAIRE, 1),
        new FrenchRevolutionDate((byte)14, FrenchRevolutionMonth.NIVOSE, 4),
        new FrenchRevolutionDate((byte)18, FrenchRevolutionMonth.FRIMAIRE, 4),
        new FrenchRevolutionDate((byte)8, FrenchRevolutionMonth.FRUCTIDOR, 2),
        new FrenchRevolutionDate((byte)11, FrenchRevolutionMonth.NIVOSE, 4),
        new FrenchRevolutionDate((byte)10, FrenchRevolutionMonth.BRUMAIRE, 12),
        new FrenchRevolutionDate((byte)10, FrenchRevolutionMonth.FRUCTIDOR, 4),
        new FrenchRevolutionDate((byte)13, FrenchRevolutionMonth.VENTOSE, 6),
        new FrenchRevolutionDate((byte)16, FrenchRevolutionMonth.GERMINAL, 8),
        new FrenchRevolutionDate((byte)28, FrenchRevolutionMonth.FLOREAL, 5),
        new FrenchRevolutionDate((byte)12, FrenchRevolutionMonth.PLUVIOSE, 7),
        new FrenchRevolutionDate((byte)10, FrenchRevolutionMonth.VENTOSE, 8),
        new FrenchRevolutionDate((byte)21, FrenchRevolutionMonth.GERMINAL, 8),
        new FrenchRevolutionDate((byte)26, FrenchRevolutionMonth.PLUVIOSE, 7),
        new FrenchRevolutionDate((byte)13, FrenchRevolutionMonth.BRUMAIRE, 12)
    };
    GregorianDate[] gDates={
        new GregorianDate((byte)22,(byte)9,1792),
        new GregorianDate((byte)4,(byte)1,1796),
        new GregorianDate((byte)9,(byte)12,1795),
        new GregorianDate((byte)25,(byte)8,1794),
        new GregorianDate((byte)1,(byte)1,1796),
        new GregorianDate((byte)2,(byte)11,1803),
        new GregorianDate((byte)27,(byte)8,1796),
        new GregorianDate((byte)3,(byte)3,1798),
        new GregorianDate((byte)6,(byte)4,1800),
        new GregorianDate((byte)17,(byte)5,1797),
        new GregorianDate((byte)31,(byte)1,1799),
        new GregorianDate((byte)1,(byte)3,1800),
        new GregorianDate((byte)11,(byte)4,1800),
        new GregorianDate((byte)14,(byte)2,1799),
        new GregorianDate((byte)5,(byte)11,1803)
    };
    Assert.assertEquals(rDates.length,gDates.length);
    for(int i=0;i<rDates.length;i++)
    {
      checkToGregorian(rDates[i],gDates[i]);
    }

  	checkToFrenchRevolution(new GregorianDate((byte)13, (byte)5, 1976),null);
  }
}
