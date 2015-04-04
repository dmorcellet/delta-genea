package delta.genea;

import delta.genea.data.sources.GeneaDataSource;

/**
 * Genea application.
 * @author DAM
 */
public class GeneaApplication
{
  private static GeneaApplication _instance;

  /**
   * Get the sole instance of this class.
   * @return the sole instance of this class.
   */
  public static GeneaApplication getInstance()
  {
    synchronized(GeneaApplication.class)
    {
      if (_instance==null)
      {
        _instance=new GeneaApplication();
      }
      return _instance;
    }
  }

  private GeneaApplication()
  {
    init();
  }

  private void init()
  {
    // Nothing to do !!
  }

  /**
   * Stop this application.
   */
  public void stop()
  {
    GeneaDataSource.closeAll();
  }
}
