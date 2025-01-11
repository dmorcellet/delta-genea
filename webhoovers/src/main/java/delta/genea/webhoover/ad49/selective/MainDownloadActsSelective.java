package delta.genea.webhoover.ad49.selective;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.genea.webhoover.ActsPackage;
import delta.genea.webhoover.ad49.AD49Session;
import delta.genea.webhoover.ad49.PackagePageParser;
import delta.genea.webhoover.ad49.PlacePageParser;

/**
 * Selective download of act pages.
 * @author DAM
 */
public class MainDownloadActsSelective
{
  private static final Logger LOGGER=LoggerFactory.getLogger(MainDownloadActsSelective.class);

  /**
   * Main method for this tool.
   * @param args Not used.
   */
  public static void main(String[] args)
  {
    AD49Session session=new AD49Session();
    session.start();
    SpecFileParser p=new SpecFileParser();
    for(String arg : args)
    {
      File f=new File(arg);
      List<PageDescription> pages=p.parse(f);
      parsePages(session,pages);
    }
    session.stop();
  }

  private static void parsePages(AD49Session session, List<PageDescription> pages)
  {
    for(PageDescription page : pages)
    {
      try
      {
        int id=page.getPlaceId();
        int packageIndex=page.getPackageIndex();
        PlacePageParser placeParser=new PlacePageParser(session,id);
        List<ActsPackage> actsPackages=placeParser.parse();
        ActsPackage actsPackage=null;
        if (actsPackages.size()>=packageIndex)
        {
          actsPackage=actsPackages.get(packageIndex-1);
        }
        else
        {
          LOGGER.warn("Cannot find package index {} for place {}",Integer.valueOf(packageIndex),Integer.valueOf(id));
        }
        if (actsPackage!=null)
        {
          PackagePageParser packageParser=new PackagePageParser(session,actsPackage);
          packageParser.parse();
          packageParser.downloadPages(page.getName(),page.getMinPageIndex(),page.getMaxPageIndex());
        }
      }
      catch(Exception e)
      {
        LOGGER.warn("Could not handle page: "+page,e);
      }
    }
  }
}
