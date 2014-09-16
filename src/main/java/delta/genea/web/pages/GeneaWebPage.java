package delta.genea.web.pages;

import delta.common.framework.web.WebPage;
import delta.common.utils.ParameterFinder;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.web.GeneaUserContext;

/**
 * @author DAM
 */
public class GeneaWebPage extends WebPage
{
  protected GeneaWebPage()
  {
    // Nothing to do !!
  }

  public Long getPersonKey(String variableName)
  {
    Long key=ParameterFinder.getLongParameter(_request,variableName,null);
    if (key==null)
    {
      key=ParameterFinder.getLongParameter(_request,GeneaUserContext.DE_CUJUS,Long.valueOf(0));
    }
    return key;
  }

  public GeneaDataSource getDataSource()
  {
    String dbName=ParameterFinder.getStringParameter(_request,GeneaUserContext.DB_NAME,null);
    if (dbName!=null)
    {
      return GeneaDataSource.getInstance(dbName);
    }
    return null;
  }
}
