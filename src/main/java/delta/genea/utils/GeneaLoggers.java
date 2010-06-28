package delta.genea.utils;

import org.apache.log4j.Logger;

import delta.common.utils.traces.LoggersRegistry;
import delta.common.utils.traces.LoggingConstants;

/**
 * Management class for all Genea loggers.
 * @author DAM
 */
public abstract class GeneaLoggers
{
  /**
   * Name of the "GENEA" logger.
   */
  public static final String GENEA="APPS.GENEA";

  /**
   * Name of the SQL related Genea logger.
   */
  public static final String GENEA_SQL=GENEA+LoggingConstants.SEPARATOR+"SQL";

  /**
   * Name of the data related Genea logger.
   */
  public static final String GENEA_DATA=GENEA+LoggingConstants.SEPARATOR+"DATA";

  private static final Logger _geneaLogger=LoggersRegistry.getLogger(GENEA);
  private static final Logger _geneaSqlLogger=LoggersRegistry.getLogger(GENEA_SQL);
  private static final Logger _geneaDataLogger=LoggersRegistry.getLogger(GENEA_DATA);

  /**
   * Get the logger used for Genea (GENEA).
   * @return the logger used for Genea.
   */
  public static Logger getGeneaLogger()
  {
    return _geneaLogger;
  }

  /**
   * Get the logger used for Genea/SQL.
   * @return the logger used for Genea/SQL.
   */
  public static Logger getGeneaSqlLogger()
  {
    return _geneaSqlLogger;
  }

  /**
   * Get the logger used for Genea/Data.
   * @return the logger used for Genea/Data.
   */
  public static Logger getGeneaDataLogger()
  {
    return _geneaDataLogger;
  }
}
