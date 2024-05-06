package delta.genea.webhoover.ad49;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import delta.genea.webhoover.ActsPackage;

/**
 * Main class for the AD49 webhoover.
 * @author DAM
 */
public class MainDownloadActs
{
  private static Set<File> _scheduledDirs=new HashSet<File>();
  private static boolean _useThreads=true;
  private static void doIt(final ActsPackage actsPackage)
  {
    File dir=actsPackage.getDirFile(Constants.ROOT_DIR);
    if (!_scheduledDirs.contains(dir))
    {
      _scheduledDirs.add(dir);
      Runnable r=new Runnable()
      {
        public void run()
        {
          AD49Session localSession=new AD49Session();
          localSession.start();
          PackagePageParser parser=new PackagePageParser(localSession,actsPackage);
          try
          {
            parser.parse();
            parser.downloadAllPages();
          }
          catch(Exception e)
          {
            e.printStackTrace();
          }
          localSession.stop();
        }
      };
      if (_useThreads)
      {
        Thread t=new Thread(r);
        t.setDaemon(false);
        t.start();
      }
      else
      {
        r.run();
      }
    }
    else
    {
      System.err.println("Duplicate dir : "+dir);
    }
  }

  /**
   * Main method of this tool.
	 * @param args Not used.
	 */
	public static void main(String[] args)
	{
	  AD49Session session=new AD49Session();
		session.start();
		try
		{
      int id=490000359;
  		PlacePageParser placeParser=new PlacePageParser(session,id);
  		List<ActsPackage> actsPackages=placeParser.parse();
  		ActsPackage actsPackage;
  		for(Iterator<ActsPackage> it=actsPackages.iterator();it.hasNext();)
  		{
  		  actsPackage=it.next();
  	    //if ((actsPackage._period.startsWith("17")) || (actsPackage._period.startsWith("16")) || (actsPackage._period.startsWith("15")))
  		  //if ((actsPackage._period.startsWith("An")) || (actsPackage._period.startsWith("18")))
  	    //if (actsPackage._period.startsWith("18"))
  		  {
  	      doIt(actsPackage);
  		  }
  		}
		}
		catch(Exception e)
		{
		  e.printStackTrace();
		}
		finally
		{
		  session.stop();
		}
	}
}
