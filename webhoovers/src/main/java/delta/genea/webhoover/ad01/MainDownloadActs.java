package delta.genea.webhoover.ad01;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import delta.downloads.Downloader;
import delta.genea.webhoover.ActsPackage;

/**
 * Download a military form.
 * @author DAM
 */
public class MainDownloadActs
{
  /**
   * Main method for this tool.
   * @param args Not used.
   * @throws Exception if a problem occurs.
   */
  public static void main(String[] args) throws Exception
  {
    HashSet<String> dirNames=new HashSet<String>();
    Downloader downloader=new Downloader();
    File mainPage=Constants.MAIN_PAGE_FILE;
    File parent=mainPage.getParentFile();
    parent.mkdirs();
    System.out.println("Main page : "+Constants.MAIN_PAGE);
    downloader.downloadToFile(Constants.MAIN_PAGE,Constants.MAIN_PAGE_FILE);
    List<ActsPackage> actsPackages=new PlacePageParser().parseFile(Constants.MAIN_PAGE_FILE);
    ActsPackage p=new ActsPackage();
    p._link=Constants.ROOT_SITE+"/visu2/visu.html?NumLot=2&COD=MATMORCELLETAlexis,%20Fran%C3%A7ois5251905";
    p._actType="Registre matricule";
    p._period="1905";
    p._placeName="Belley";
    actsPackages.add(p);
    int nb=actsPackages.size();
    boolean doIt=false;
    for(int i=0;i<nb;i++)
    {
      ActsPackage a=actsPackages.get(i);
      // if (a._period.startsWith("1845"))
      {
        doIt=true;
      }
      if (doIt)
      {
        PackagePageParser parser=new PackagePageParser(downloader,a);
        String dirName=parser.getDirName();
        if (dirNames.contains(dirName))
        {
          System.err.println("Erreur : "+dirName+" en doublon !!");
        }
        dirNames.add(dirName);
        parser.parse();
      }
    }
    mainPage.delete();
  }
}
