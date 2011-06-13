package delta.genea.web;

import delta.common.framework.web.WebApplication;
import delta.common.framework.web.WebUserContext;
import delta.common.utils.ParameterFinder;
import delta.common.utils.ParametersNode;
import delta.genea.data.Person;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.misc.DatabaseConfiguration;
import delta.genea.web.pages.PersonPageParameters;

/**
 * Context for a user of the Genea web application.
 * @author DAM
 */
public class GeneaUserContext extends WebUserContext
{
  /**
   * Default de-cujus key.
   */
  public static final long DEFAULT_KEY=76;
  /**
   * De-cujus parameter name.
   */
  public static final String DE_CUJUS="DE_CUJUS";
  /**
   * Database parameter name.
   */
  public static final String DB_NAME="DB_NAME";

  /**
   * Constructor.
   * @param app Associated web application.
   */
  public GeneaUserContext(WebApplication app)
  {
    super(app);
    putLongParameter(PersonPageParameters.PERSON_KEY,DEFAULT_KEY);
    String dbName=DatabaseConfiguration.getInstance().getDefaultDbName();
    setDbName(dbName);
    setDeCujus(DEFAULT_KEY);
  }

  /**
   * Set the value of the de-cujus parameter.
   * @param key Person key.
   */
  public final void setDeCujus(long key)
  {
    long previous=getDeCujus();
    if (previous!=key)
    {
      String dbName=getDbName();
      Person p=GeneaDataSource.getInstance(dbName).getPersonDataSource().load(key);
      if (p!=null)
      {
        putLongParameter(DE_CUJUS,key);
      }
    }
  }

  /**
   * Get the key of the current de-cujus.
   * @return the key of the current de-cujus.
   */
  public long getDeCujus()
  {
    return ParameterFinder.getLongParameter(this,DE_CUJUS,-1);
  }

  /**
   * Set the value of the database name parameter.
   * @param dbName New value.
   */
  public final void setDbName(String dbName)
  {
    putStringParameter(DB_NAME,dbName);
  }

  /**
   * Get the name of the current database.
   * @return the name of the current database.
   */
  public String getDbName()
  {
    return ParameterFinder.getStringParameter(this,DB_NAME,null);
  }

  @Override
  public void useParameters(ParametersNode requestParameters)
  {
    // De cujus update
    long previousDeCujus=getDeCujus();
    long newDeCujus=ParameterFinder.getLongParameter(requestParameters,DE_CUJUS,previousDeCujus,false);
    if (newDeCujus!=previousDeCujus)
    {
      setDeCujus(newDeCujus);
    }
    // Database name update
    String dbName=ParameterFinder.getStringParameter(requestParameters,DB_NAME,null);
    if (dbName!=null)
    {
      setDbName(dbName);
    }
  }
}
