package delta.genea.web;

import delta.common.framework.web.WebApplication;
import delta.common.framework.web.WebRequestDispatcher;
import delta.common.framework.web.WebUserContext;
import delta.genea.GeneaApplication;

/**
 * 'genea' web application.
 * @author DAM
 */
public class GeneaWebApplication extends WebApplication
{
  private GeneaWebRequestDispatcher _dispatcher;

  /**
   * Constructor.
   */
  public GeneaWebApplication()
  {
    super("genea");
    _dispatcher=new GeneaWebRequestDispatcher();
    setAppContext(new GeneaApplicationContext());
  }

  @Override
  public void initApplication()
  {
    GeneaApplication.getInstance();
  }

  @Override
  public void closeApplication()
  {
    GeneaApplication.getInstance().stop();
  }

  @Override
  public WebUserContext buildUserContext()
  {
    return new GeneaUserContext(this);
  }

  @Override
  public WebRequestDispatcher getDispatcher()
  {
    return _dispatcher;
  }
}
