package delta.common.utils.misc;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Unit test class for the latine numbers tools.
 * @author DAM
 */
public class TestLatineNumbers extends TestCase
{
  /**
   * Constructor.
   */
  public TestLatineNumbers()
  {
    super("Latine numbers test");
  }

  /**
   * Test the classic to latine and back to classic conversion loop.
   */
  public void testConversionLoop()
  {
    long start=System.currentTimeMillis();
    int val=LatineNumbers.convert("");
    Assert.assertEquals(0,val);
    for (int k=0; k<100; k++)
    {
      String v;
      for (int i=0;i<=10000;i++)
      {
        v=LatineNumbers.convert(i);
        int j=LatineNumbers.convert(v);
        Assert.assertEquals("Latine value ["+v+"]",i,j);
      }
    }
    long end=System.currentTimeMillis();
    System.out.println("Time="+(end-start)+"ms.");
  }
}
