package delta.genea.web;

import delta.common.framework.objects.data.DataObject;
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
   * Data source parameter name.
   */
  public static final String DATA_SOURCE="DATA_SOURCE";

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
    setDeCujus(Long.valueOf(DEFAULT_KEY));
  }

  /**
   * Set the value of the de-cujus parameter.
   * @param key Person key.
   */
  public final void setDeCujus(Long key)
  {
    Long previous=getDeCujus();
    if (!DataObject.keysAreEqual(previous,key))
    {
      String dbName=getDbName();
      Person p=GeneaDataSource.getByName(dbName).load(Person.class,key);
      if (p!=null)
      {
        putLongParameter(DE_CUJUS,key.longValue());
      }
    }
  }

  /**
   * Get the key of the current de-cujus.
   * @return the key of the current de-cujus.
   */
  public Long getDeCujus()
  {
    return ParameterFinder.getLongParameter(this,DE_CUJUS,null);
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
    Long previousDeCujus=getDeCujus();
    Long newDeCujus=ParameterFinder.getLongParameter(requestParameters,DE_CUJUS,previousDeCujus,false);
    if (!DataObject.keysAreEqual(newDeCujus,previousDeCujus))
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

  /**
   * Get the genea data source.
   * @return the genea data source.
   */
  public GeneaDataSource getDataSource()
  {
    return (GeneaDataSource)ParameterFinder.getParameter(this,DATA_SOURCE,false);
  }
}
