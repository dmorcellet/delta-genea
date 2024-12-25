package delta.genea.data.sources;

import java.io.File;

import delta.common.framework.objects.data.ObjectsSource;

/**
 * Factory for genea XML data sources.
 * @author DAM
 */
public class GeneaXmlDataSourceFactory implements GeneaObjectsSourceFactory
{
  private static final String PREFIX="xml:";

  @Override
  public ObjectsSource build(String id)
  {
    if (handles(id))
    {
      String filename=id.substring(PREFIX.length());
      File xmlDir=new File(filename);
      ObjectsSource source=new GeneaXmlDataSource(xmlDir);
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
