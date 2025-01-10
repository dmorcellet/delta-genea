package delta.genea.webhoover.ad01;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.downloads.Downloader;
import delta.genea.webhoover.ActsPackage;
import delta.genea.webhoover.utils.FileUtils;

/**
 * Download a military form.
 * @author DAM
 */
public class MainDownloadActs
{
  private static final Logger LOGGER=LoggerFactory.getLogger(MainDownloadActs.class);

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
    LOGGER.info("Main page: {}",Constants.MAIN_PAGE);
    downloader.downloadToFile(Constants.MAIN_PAGE,Constants.MAIN_PAGE_FILE);
    List<ActsPackage> actsPackages=new PlacePageParser().parseFile(Constants.MAIN_PAGE_FILE);
    ActsPackage p=new ActsPackage();
    p.setLink(Constants.ROOT_SITE+"/visu2/visu.html?NumLot=2&COD=MATMORCELLETAlexis,%20Fran%C3%A7ois5251905");
    p.setActType("Registre matricule");
    p.setPeriod("1905");
    p.setPlaceName("Belley");
    actsPackages.add(p);
    int nb=actsPackages.size();
    for(int i=0;i<nb;i++)
    {
      ActsPackage a=actsPackages.get(i);
      boolean doIt=doIt(a);
      if (doIt)
      {
        PackagePageParser parser=new PackagePageParser(downloader,a);
        String dirName=parser.getDirName();
        if (dirNames.contains(dirName))
        {
          LOGGER.error("{} en doublon !!",dirName);
        }
        dirNames.add(dirName);
        parser.parse();
      }
    }
    FileUtils.deleteFile(mainPage);
  }

  private static boolean doIt(ActsPackage a)
  {
    LOGGER.debug("Testing package: {}",a);
    return (a!=null);
  }
}
