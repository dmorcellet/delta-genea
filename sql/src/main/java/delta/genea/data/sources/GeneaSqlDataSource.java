package delta.genea.data.sources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.framework.objects.sql.DatabaseConfiguration;
import delta.common.framework.objects.sql.SqlObjectsSource;
import delta.genea.data.Act;
import delta.genea.data.ActText;
import delta.genea.data.ActType;
import delta.genea.data.Cousinage;
import delta.genea.data.Person;
import delta.genea.data.Picture;
import delta.genea.data.Place;
import delta.genea.data.Union;
import delta.genea.sql.ActSqlDriver;
import delta.genea.sql.ActTypeSqlDriver;
import delta.genea.sql.CousinageSqlDriver;
import delta.genea.sql.PersonSqlDriver;
import delta.genea.sql.PictureSqlDriver;
import delta.genea.sql.PlaceSqlDriver;
import delta.genea.sql.TextSqlDriver;
import delta.genea.sql.UnionSqlDriver;

/**
 * A SQL-based objects source for the Genea application.
 * @author DAM
 */
public class GeneaSqlDataSource extends SqlObjectsSource
{
  private static final Logger LOGGER=LoggerFactory.getLogger(GeneaSqlDataSource.class);

  /**
   * Constructor.
   * @param dbName Name of the database to manage.
   */
  public GeneaSqlDataSource(String dbName)
  {
    super(dbName);
    buildDrivers();
    try
    {
      start();
    }
    catch(Exception e)
    {
      LOGGER.error("Cannot start genea SQL data source!",e);
    }
  }

  @Override
  protected DatabaseConfiguration buildDatabaseConfiguration(String dbName)
  {
    return new DatabaseConfiguration("delta/genea/misc/database.properties");
  }

  /**
   * Build the drivers for all the object classes.
   */
  private void buildDrivers()
  {
    try
    {
      addClass(Person.class,new PersonSqlDriver(this));
      addClass(Union.class,new UnionSqlDriver(this));
      addClass(Place.class,new PlaceSqlDriver(this));
      addClass(Act.class,new ActSqlDriver(this));
      addClass(Picture.class,new PictureSqlDriver(this));
      addClass(ActText.class,new TextSqlDriver());
      addClass(ActType.class,new ActTypeSqlDriver());
      addClass(Cousinage.class,new CousinageSqlDriver(this));
    }
    catch(Exception e)
    {
      LOGGER.error("",e);
    }
  }
}
