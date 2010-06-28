package delta.genea;

import delta.genea.data.sources.GeneaDataSource;

public class GeneaApplication
{
  private static GeneaApplication _instance;

  public static GeneaApplication getInstance()
  {
    synchronized(GeneaApplication.class)
    {
      if (_instance==null) _instance=new GeneaApplication();
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

  public void stop()
  {
    GeneaDataSource.closeAll();
  }
}
