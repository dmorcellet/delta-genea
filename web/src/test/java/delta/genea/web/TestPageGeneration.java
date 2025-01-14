package delta.genea.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.framework.web.SimpleRequest;
import delta.common.framework.web.SimpleRequestResponse;
import delta.common.framework.web.WebRequest;
import delta.common.framework.web.WebUserContext;
import delta.genea.web.pages.ActPageParameters;
import delta.genea.web.pages.ActsFromPlaceParameters;
import delta.genea.web.pages.PersonPageParameters;
import junit.framework.TestCase;

/**
 * Test page generations.
 * @author DAM
 */
public class TestPageGeneration extends TestCase
{
  private static final Logger LOGGER=LoggerFactory.getLogger(TestPageGeneration.class);

  private GeneaWebApplication _app;
  private WebUserContext _userContext;

  /**
   * Constructor.
   */
  public TestPageGeneration()
  {
    super("Page generation test");
  }

  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    _app=new GeneaWebApplication();
    _userContext=_app.buildUserContext();
  }

  /**
   * Test pages generation.
   */
  public void testPageGeneration()
  {
    generatePersonPage();
    generateActPage();
    generateActsFromPlacePage();
  }

  private void generatePersonPage()
  {
    SimpleRequest request=new SimpleRequest(_userContext);
    request.putStringParameter(WebRequest.ACTION_PARAM,PersonPageParameters.ACTION_VALUE);
    request.putLongParameter(PersonPageParameters.PERSON_KEY,1676);
    generatePage(request);
  }
  
  private void generateActPage()
  {
    SimpleRequest request=new SimpleRequest(_userContext);
    request.putStringParameter(WebRequest.ACTION_PARAM,ActPageParameters.ACTION_VALUE);
    request.putLongParameter(ActPageParameters.KEY,176);
    generatePage(request);
  }

  private void generateActsFromPlacePage()
  {
    SimpleRequest request=new SimpleRequest(_userContext);
    request.putStringParameter(WebRequest.ACTION_PARAM,ActsFromPlaceParameters.ACTION_VALUE);
    request.putLongParameter(ActsFromPlaceParameters.KEY,21);
    generatePage(request);
  }
  
  private String generatePage(WebRequest request)
  {
    SimpleRequestResponse response=new SimpleRequestResponse(null);
    try
    {
      _app.handleRequest(_userContext,request,response);
    }
    catch(Exception e)
    {
      LOGGER.error("Page generation error",e);
    }
    String text=response.getTextResponse();
    System.out.println(text);
    return text;
  }

  @Override
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
}
