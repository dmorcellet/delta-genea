package delta.genea.webhoover.utils;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Test class for parsing utils.
 * @author DAM
 */
public class ParsingUtilsTest extends TestCase
{
  /**
   * Test some samples.
   */
  public void testSamples()
  {
    doSample("<toto>titi</toto>","toto","titi");
    doSample("<toto","toto");
    doSample("<toto>","toto");
    doSample("<toto>titi</toto","toto");
    doSample("<toto a='here'>titi</toto>   <toto>tata</toto>","toto","titi","tata");
  }

  private void doSample(String input, String tag, String... expected)
  {
    List<String> result=ParsingUtils.splitAsTags(tag,input);
    Assert.assertNotNull(result);
    Assert.assertEquals(expected.length,result.size());
    for(int i=0;i<expected.length;i++)
    {
      Assert.assertEquals(expected[i],result.get(i));
    }
  }
}
