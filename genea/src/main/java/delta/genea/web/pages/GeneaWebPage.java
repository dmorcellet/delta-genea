package delta.genea.web.pages;

import java.io.File;

import delta.common.framework.web.WebPage;
import delta.common.utils.ParameterFinder;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.web.GeneaUserContext;

/**
 * Base class for the web pages of the 'genea' web application.
 * @author DAM
 */
public class GeneaWebPage extends WebPage
{
  protected GeneaWebPage()
  {
    // Nothing to do !!
  }

  /**
   * Get the data source for this page.
   * @return A data source or <code>null</code>.
   */
  public GeneaDataSource getDataSource()
  {
    String dbName=ParameterFinder.getStringParameter(_request,GeneaUserContext.DB_NAME,null);
    if (dbName==null)
    {
      return null;
    }
    GeneaDataSource dataSource;
    File xmlDir=new File(dbName);
    if (xmlDir.exists())
    {
      dataSource=GeneaDataSource.getInstance(xmlDir);
    }
    else
    {
      dataSource=GeneaDataSource.getInstance(dbName);
    }
    getUserContext().putParameter(GeneaUserContext.DATA_SOURCE,dataSource);
    return dataSource;
  }
}
