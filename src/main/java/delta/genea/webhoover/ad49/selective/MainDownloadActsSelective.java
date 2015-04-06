package delta.genea.webhoover.ad49.selective;

import java.io.File;
import java.util.List;

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
  private static final File IN_FILE=new File("/home/dm/tmp/liste.csv");
  private static final File IN_FILE2=new File("/home/dm/tmp/liste2.csv");

  /**
   * Main method for this tool.
   * @param args Not used.
   */
  public static void main(String[] args)
  {
    AD49Session session=new AD49Session();
    session.start();
    SpecFileParser p=new SpecFileParser();
    List<PageDescription> pages=p.parse(IN_FILE);
    parsePages(session,pages);
    List<PageDescription> pages2=p.parse(IN_FILE2);
    parsePages(session,pages2);
    session.stop();
  }

  private static void parsePages(AD49Session session, List<PageDescription> pages)
  {
    for(PageDescription page : pages)
    {
      try
      {
        int id=page._placeId;
        PlacePageParser placeParser=new PlacePageParser(session,id);
        List<ActsPackage> actsPackages=placeParser.parse();
        ActsPackage actsPackage=null;
        if (actsPackages.size()>=page._packageIndex)
        {
          actsPackage=actsPackages.get(page._packageIndex-1);
        }
        else
        {
          System.out.println("Cannot find package index ["+page._packageIndex+"] for place ["+page._placeId+"]");
        }
        if (actsPackage!=null)
        {
          PackagePageParser packageParser=new PackagePageParser(session,actsPackage);
          packageParser.parse();
          packageParser.downloadPages(page._name,page._minPageIndex,page._maxPageIndex);
        }
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
    }
  }
}
