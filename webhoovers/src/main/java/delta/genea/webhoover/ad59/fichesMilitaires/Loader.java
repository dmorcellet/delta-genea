package delta.genea.webhoover.ad59.fichesMilitaires;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import delta.common.utils.xml.DOMParsingTools;
import delta.downloads.DownloadException;
import delta.downloads.Downloader;
import delta.genea.webhoover.ADSession;
import delta.genea.webhoover.ImageMontageMaker;

/**
 * Loader for military forms (AD59).
 * @author DAM
 */
public class Loader
{
  private static final Logger LOGGER=LoggerFactory.getLogger(Loader.class);

  private static final String BASE_URL="https://archivesdepartementales.lenord.fr/";
  private static final String BASE_VIEWER_URL="http://viewer-archivesdepartementales.lenord.fr/";
  private static final String FICHE_MATRICULES_PLAYLIST="accounts/mnesys_ad59/datas/playlists/fiches_matriculesXXX.xml?time=TIME";
  private static final String FIRST_PAGE="?id=viewer&doc=accounts%2Fmnesys_ad59%2Fdatas%2Fir%2FRegistres%20militaires%2FFiches%2Ffiches_matricules.xml&page_ref=XXX&lot_num=1&img_num=1";

  private int _packageId;
  private String _basePath;

  /**
   * Constructor.
   * @param packageId Package identifier.
   */
  public Loader(int packageId)
  {
    _packageId=packageId;
  }

  /**
   * Load a page.
   * @param session Download session.
   * @param pageNumber Page number (starting at 1).
   * @throws DownloadException
   */
  public void load(ADSession session, int pageNumber) throws DownloadException
  {
    // Loading first page is needed to load pages index
    // Otherwise playlist gives a "not found" error...
    loadFirstPage(session);
    List<String> items=loadPlayList(session);
    LOGGER.info("Got items: {}",items);
    loadPage(session,pageNumber,items.get(pageNumber-1));
    LOGGER.info(session.getDownloader().getStatistics());
  }

  private void loadFirstPage(ADSession session) throws DownloadException
  {
    String url=getFirstPageURL();
    File tmpDir=session.getTmpDir();
    Downloader downloader=session.getDownloader();
    downloader.setStoreCookies(true);
    File tmpFile=new File(tmpDir,"first"+_packageId+".html");
    downloader.downloadToFile(url, tmpFile);
    LOGGER.debug("Cookies: {}",downloader.getCookies());
  }

  private String getFirstPageURL()
  {
    String url=FIRST_PAGE.replace("XXX",String.valueOf(_packageId));
    return BASE_URL+url;
  }

  private String getPlaylistURL()
  {
    //http://www.archivesdepartementales.lenord.fr/accounts/mnesys_ad59/datas/playlists/fiches_matricules11247.xml?time=1417966997316
    String url=FICHE_MATRICULES_PLAYLIST.replace("XXX",String.valueOf(_packageId));
    long now=System.currentTimeMillis();
    url=url.replace("TIME",String.valueOf(now));
    return BASE_URL+url;
  }

  private List<String> loadPlayList(ADSession session) throws DownloadException
  {
    ArrayList<String> ret=new ArrayList<String>();
    String url=getPlaylistURL();
    File tmpDir=session.getTmpDir();
    Downloader downloader=session.getDownloader();
    File tmpFile=new File(tmpDir,"playlist"+_packageId+".xml");
    downloader.downloadToFile(url, tmpFile);
    Element rootElement=DOMParsingTools.parse(tmpFile);
    String basePath=DOMParsingTools.getStringAttribute(rootElement.getAttributes(),"basepath",null);
    if (basePath!=null)
    {
      Element g=DOMParsingTools.getChildTagByName(rootElement,"g");
      if (g!=null)
      {
        int nbItems=DOMParsingTools.getIntAttribute(g.getAttributes(),"nbi",0);
        List<Element> itemElements=DOMParsingTools.getChildTagsByName(g,"a");
        for(Element itemElement : itemElements)
        {
          String text=itemElement.getTextContent();
          ret.add(text);
        }
        if (nbItems!=ret.size())
        {
          LOGGER.error("Attention! Nb pages incorrect!");
        }
        _basePath=basePath;
      }
    }
    tmpFile.delete();
    return ret;
  }

  private void loadPage(ADSession session, int pageNumber, String item) throws DownloadException
  {
    File tmpDir=session.getTmpDir();
    Downloader downloader=session.getDownloader();
    File out=new File(tmpDir,"image_"+pageNumber+".jpg");
    item=item.replace(".jpg","_jpg");
    String urlTemplate=BASE_VIEWER_URL+_basePath+item+"_/3_COUNT.jpg";
    int nbH=4;
    int nbV=6;
    File[][] files=new File[nbH][nbV];
    for(int i=0;i<24;i++)
    {
      String url=urlTemplate.replace("COUNT",String.valueOf(i));
      int x=i%nbH;
      int y=i/nbH;
      File tileFile=new File(tmpDir,"tile"+x+"_"+y+".jpg");
      tileFile.getParentFile().mkdirs();
      downloader.downloadToFile(url,tileFile);
      files[x][y]=tileFile;
    }
    ImageMontageMaker maker=new ImageMontageMaker();
    try
    {
      // Image building may raise an exception if JAI is not installed correctly
      // One way to do it is to install it on the JRE
      maker.doIt(files, out);
    }
    catch(Exception e)
    {
      LOGGER.warn("Could not make image!",e);
    }
    /*
    for(int hIndex=0;hIndex<nbH;hIndex++)
    {
      for(int vIndex=0;vIndex<nbV;vIndex++)
      {
        boolean ok=files[hIndex][vIndex].delete();
        if (!ok)
        {
          System.err.println("Cannot delete : "+files[hIndex][vIndex]);
        }
      }
    }*/
  }

/*
Sample playlist XML document:
<?xml version="1.0" encoding="utf-8"?>
<playlist nbg="1" host="/" basepath="accounts/mnesys_ad59/datas/medias/" index="" indexzone="">
<t unitid="" unitdate=" classe 1897 "> Volume 6 (2501-3000)</t>
<g nbi="592">
<i><a>1R_REGISTRES/FRAD059_1R_02529/FRAD059_1R_02529_002998_a.jpg</a></i>
<i><a>1R_REGISTRES/FRAD059_1R_02529/FRAD059_1R_02529_002999_a.jpg</a></i>
<i><a>1R_REGISTRES/FRAD059_1R_02529/FRAD059_1R_02529_002999_b.jpg</a></i>
<i><a>1R_REGISTRES/FRAD059_1R_02529/FRAD059_1R_02529_003000_a.jpg</a></i>
(592 times)
</g>
</playlist>
*/
/*
 p466:
 http://www.archivesdepartementales.lenord.fr/accounts/mnesys_ad59/datas/medias/1R_REGISTRES/FRAD059_1R_02529/FRAD059_1R_02529_002889_a_jpg_/2_0.jpg
*/

  /**
   * Main method.
   * @param args Not used.
   * @throws DownloadException
   */
  public static void main(String[] args) throws DownloadException
  {
    ADSession session=new ADSession();
    // Béthune, 1897: packageId=11247, pageNumber=466
    // Lille, 1901:   packageId=11823, pageNumber=552
    // Lille, 1893:   packageId=10683, pageNumber=149
    // Lille, 1885:   packageId=9615,  pageNumber=189
    int packageId=9615;
    int pageNumber=189;
    Loader l=new Loader(packageId);
    session.start();
    l.load(session,pageNumber);
    session.stop();
  }
}
