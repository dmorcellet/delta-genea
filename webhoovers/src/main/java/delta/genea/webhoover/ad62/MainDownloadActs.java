package delta.genea.webhoover.ad62;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.utils.NumericTools;
import delta.common.utils.misc.SleepManager;
import delta.downloads.DownloadException;
import delta.downloads.Downloader;
import delta.genea.webhoover.ADSession;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

/**
 * Main class for the AD62 webhoover.
 * @author DAM
 */
public class MainDownloadActs
{
  private static final Logger LOGGER=LoggerFactory.getLogger(MainDownloadActs.class);

  private File _toDir;
  private ADSession _session;

  private MainDownloadActs(File toDir)
  {
    _toDir=toDir;
  }

  private void doIt() throws DownloadException
  {
    _session=new ADSession();
    _session.start();
    // BB 1 : 381133539
    // BB 2 : 381133567
    // Douvrin 1 : 381146209
    // Béthencourt 1 : 210197507
    // Béthencourt 2 : 210197532
    String mainSetPage=downloadMainSetPage(381133567);
    List<String> pages=parseMainSetPage(mainSetPage);
    int nbPages=pages.size();
    for(int i=0;i<nbPages;i++)
    {
      try
      {
        handlePage(pages.get(i),i);
      }
      catch(Exception e)
      {
        String msg="Error with page "+i;
        LOGGER.error(msg,e);
      }
    }
    _session.stop();
  }

  private String downloadMainSetPage(int id) throws DownloadException
  {
    String url=Constants.buildMainSetPageURL(id);
    Downloader downloader=_session.getDownloader();
    String ret=downloader.downloadString(url);
    return ret;
  }

  private List<String> parseMainSetPage(String input)
  {
    List<String> ret=new ArrayList<String>();
    Source source=new Source(input);
    List<Element> pages=JerichoHtmlUtils.findElementsByTagNameAndAttributeValue(source,"img","data-type","IMG");
    for(Element page : pages)
    {
      String imageFile=page.getAttributeValue("data-original");
      if ((imageFile!=null) && (!imageFile.isEmpty()))
      {
        ret.add(imageFile);
      }
    }
    return ret;
  }

  private void handlePage(String page, int index) throws DownloadException
  {
    File toFile=Constants.getImageFile(_toDir,index+1);
    if (toFile.exists())
    {
      return;
    }
    Downloader downloader=_session.getDownloader();
    String url=Constants.buildGenereImageURL(1000,1000,page);
    LOGGER.debug("Image generation URL: {}",url);
    String ret=downloader.downloadString(url);
    LOGGER.debug("Image generation result: {}",ret);
    // Returns :
    // 4 /cache/mnt_lustre_ad62_etat_civil_registres_1_132_frad062_5mir_132_01_frad062_5mir_132_01_0547_1800_1800_0_0_0_0_img.jpg  1800  1345  2944  2200  image_single
    String[] elements=ret.split("\t");
    int largeur=NumericTools.parseInt(elements[4],0);
    int hauteur=NumericTools.parseInt(elements[5],0);
    url=Constants.buildGenereImageURL(largeur,hauteur,page);
    LOGGER.debug("Image generation URL (right sizes): {}",url);
    ret=downloader.downloadString(url);
    elements=ret.split("\t");
    String cachedImage=elements[1];
    String imageURL=Constants.buildCachedImageURL(cachedImage);
    LOGGER.debug("Cached image URL: {}",imageURL);
    LOGGER.info("File to write: {}",toFile);
    downloader.downloadToFile(imageURL,toFile);
    LOGGER.info("Wrote file: {}",toFile);
    SleepManager.sleep(2000);
  }

  /**
   * Main method of this tool.
   * @param args Output directory.
   * @throws Exception If a problem occurs.
   */
  public static void main(String[] args) throws Exception
  {
    File toDir=new File(args[0]);
    new MainDownloadActs(toDir).doIt();
  }
}
