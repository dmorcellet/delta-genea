package delta.genea.web;

import delta.common.framework.web.WebApplication;
import delta.common.framework.web.WebServlet;

public class GeneaServlet extends WebServlet
{
  private static final long serialVersionUID=1L;

  @Override
  protected WebApplication buildApplication() throws Exception
  {
    return new GeneaWebApplication();
  }
}
