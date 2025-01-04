package delta.genea.data.sources;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.framework.objects.data.ObjectsSource;

/**
 * Factory for genea XML data sources.
 * @author DAM
 */
public class GeneaXmlDataSourceFactory implements GeneaObjectsSourceFactory
{
  private static final Logger LOGGER=LoggerFactory.getLogger(GeneaXmlDataSourceFactory.class);

  private static final String PREFIX="xml:";

  @Override
  public ObjectsSource build(String id)
  {
    if (handles(id))
    {
      String filename=id.substring(PREFIX.length());
      File xmlDir=new File(filename);
      ObjectsSource source=new GeneaXmlDataSource(xmlDir);
      LOGGER.debug("Built XML data source: {} (absolute {})",xmlDir,xmlDir.getAbsolutePath());
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
