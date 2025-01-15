package delta.genea.webhoover.ad62;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.utils.NumericTools;
import delta.common.utils.text.StringSplitter;
import delta.common.utils.text.TextUtils;
import delta.downloads.DownloadException;
import delta.downloads.Downloader;
import delta.genea.webhoover.ADSession;
import delta.genea.webhoover.utils.FileUtils;
import delta.genea.webhoover.utils.ImageUtils;

/**
 * Main class for the AD62 webhoover.
 * @author DAM
 */
public class MainDownloadActs
{
  private static final Logger LOGGER=LoggerFactory.getLogger(MainDownloadActs.class);

  private File _toDir;

  private MainDownloadActs(File toDir)
  {
    _toDir=toDir;
  }

  private void doIt() throws DownloadException
  {
    ADSession session=new ADSession();
    session.start();
    List<String> places=getPlaces(session);
    LOGGER.info("Got places: {}",places);
    session.stop();
  }

  private String getTileURL(int pageNumber, int x, int y, int width, int height)
  {
    String urlTile=Constants.ROOT_SITE+"/cg62/visualiseur/visu_affiche_util.php?o=TILE&param=visu_0&p="+pageNumber+"&x="+x+"&y="+y+"&l="+width+"&h="+height+"&ol="+width+"&oh="+height+"&r=0&n=0&b=0&c=0";
    return urlTile;
  }

  private File downloadTile(ADSession session, String urlTile, int hIndex, int vIndex) throws DownloadException
  {
    Downloader downloader=session.getDownloader();
    File tmpDir=session.getTmpDir();
    File tileFileCacheFile=new File(tmpDir,"tileFileName.txt");
    File tileFile=new File(tmpDir,"tile"+hIndex+"_"+vIndex+".jpg");
    downloader.downloadToFile(urlTile,tileFileCacheFile);
    List<String> lines=TextUtils.readAsLines(tileFileCacheFile);
    String cacheFileUrl=lines.get(0);
    String tileUrl=Constants.ROOT_SITE+cacheFileUrl;
    downloader.downloadToFile(tileUrl,tileFile);
    FileUtils.deleteFile(tileFileCacheFile);
    return tileFile;
  }

  private void downloadPage(ADSession session, File out, int pageNumber, int width, int height, int tileSize) throws DownloadException
  {
    LOGGER.info("Handling page {}",Integer.valueOf(pageNumber));
    int nbH=(width/tileSize)+(((width%tileSize)!=0)?1:0);
    int nbV=(height/tileSize)+(((height%tileSize)!=0)?1:0);
    int x=0;
    File[][] files=new File[nbH][nbV];
    for(int hIndex=0;hIndex<nbH;hIndex++)
    {
      int tileWidth=Math.min(tileSize,width-x);
      int y=0;
      for(int vIndex=0;vIndex<nbV;vIndex++)
      {
        int tileHeight=Math.min(tileSize,height-y);
        String urlTile=getTileURL(pageNumber,x,y,tileWidth,tileHeight);
        files[hIndex][vIndex]=downloadTile(session,urlTile,hIndex,vIndex);
        y+=tileHeight;
      }
      x+=tileWidth;
    }
    ImageUtils.makeImage(files,out);
  }

  private List<String> getPlaces(ADSession session) throws DownloadException
  {
    List<String> placeNames=new ArrayList<String>();
    Downloader downloader=session.getDownloader();
    File tmpDir=session.getTmpDir();
    File tmpFile=new File(tmpDir,"TD_placesIndex.html");
    String url=Constants.getMainURL();
    downloader.downloadToFile(url, tmpFile);
    File tmpFile2=new File(tmpDir,"visuInit.html");
    String id=Constants.ID;
    url="http://www.archinoe.net/cg62/visualiseur/visu_init.php?fonds=ec&id="+id;
    downloader.downloadToFile(url, tmpFile2);
    String sessionId=downloader.getCookieValue("PHPSESSID");
    LOGGER.info("Session ID: {}",sessionId);
    File tmpFile3=new File(tmpDir,"page1.html");

    url="http://www.archinoe.net/cg62/visualiseur/visu_registre.php?id="+id+"&w=1280&h=1024";
    downloader.downloadToFile(url, tmpFile3);

    int tile=2280;
    int nbPages=200;
    for(int page=1;page<=nbPages;page++)
    {
      url="http://www.archinoe.net/cg62/visualiseur/visu_affiche_util.php?PHPSID="+sessionId+"&param=visu_0&o=IMG&p="+page;

      File tmpFile4=new File(tmpDir,"cache.txt");
      downloader.downloadToFile(url, tmpFile4);
      List<String> lines=TextUtils.readAsLines(tmpFile4);
      String line=lines.get(0);
      String[] items=StringSplitter.split(line,'\t');
      int width=NumericTools.parseInt(items[3],0);
      int height=NumericTools.parseInt(items[4],0);
      File out=new File(_toDir,Constants.getImageName(page));
      out.getParentFile().mkdirs();
      downloadPage(session,out,page,width,height,tile);
    }
    return placeNames;
  }

  /**
   * Main method of this tool.
   * @param args Output directory.
   * @throws DownloadException If a problem occurs.
   */
  public static void main(String[] args) throws DownloadException
  {
    File toDir=new File(args[0]);
    new MainDownloadActs(toDir).doIt();
  }
}
