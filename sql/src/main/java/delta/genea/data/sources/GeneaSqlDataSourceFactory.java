package delta.genea.data.sources;

import delta.common.framework.objects.data.ObjectsSource;

/**
 * Factory for genea SQL data sources.
 * @author DAM
 */
public class GeneaSqlDataSourceFactory implements GeneaObjectsSourceFactory
{
  private static final String PREFIX="sql:";

  @Override
  public ObjectsSource build(String id)
  {
    if (handles(id))
    {
      String dbName=id.substring(PREFIX.length());
      ObjectsSource source=new GeneaSqlDataSource(dbName);
      return source;
    }
    return null;
  }

  @Override
  public boolean handles(String id)
  {
    return id.startsWith(PREFIX);
  }
}
