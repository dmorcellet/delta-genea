package delta.genea.webhoover.ad49.selective;

import java.io.File;
import java.util.List;

import delta.genea.webhoover.ad49.AD49Session;
import delta.genea.webhoover.ad49.ActsPackage;
import delta.genea.webhoover.ad49.PackagePageParser;
import delta.genea.webhoover.ad49.PlacePageParser;

public class MainDownloadActsSelective
{
  private static final File IN_FILE=new File("/home/dm/tmp/liste.csv");

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    AD49Session session=new AD49Session();
    session.start();
    SpecFileParser p=new SpecFileParser();
    List<PageDescription> pages=p.parse(IN_FILE);
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
          //packageParser.downloadPages(page._name,page._minPageIndex,page._maxPageIndex);
        }
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
    }
    session.stop();
  }
}
