package delta.genea.webhoover.ad62;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import delta.common.utils.NumericTools;
import delta.common.utils.text.StringSplitter;
import delta.common.utils.text.TextUtils;
import delta.downloads.Downloader;
import delta.genea.utils.GeneaLoggers;
import delta.genea.webhoover.ADSession;
import delta.genea.webhoover.ActsPackage;
import delta.genea.webhoover.ImageMontageMaker;

/**
 * @author DAM
 */
public class MainDownloadActs
{
  private static final Logger _logger=GeneaLoggers.getGeneaLogger();

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    new MainDownloadActs().doIt();
  }

  private static boolean _useThreads=true;

  private void getPages(final ActsPackage actsPackage)
  {
    Runnable r=new Runnable()
    {
      public void run()
      {
        ADSession localSession=new ADSession();
        localSession.start();
        //getPages(localSession,actsPackage);
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

  private void doIt()
  {
    ADSession session=new ADSession();
    session.start();
    List<String> places=getPlaces(session);
    /*
    System.out.println(places);
    handlePlace(session,Constants.PLACE_NAME);
    */
    session.stop();
  }

  private File downloadTile(ADSession session, int pageNumber, int hIndex, int vIndex, int x, int y, int width, int height)
  {
    String urlTile=Constants.ROOT_SITE+"/cg62/visualiseur/visu_affiche_util.php?o=TILE&param=visu_0&p="+pageNumber+"&x="+x+"&y="+y+"&l="+width+"&h="+height+"&ol="+width+"&oh="+height+"&r=0&n=0&b=0&c=0";

    Downloader downloader=session.getDownloader();
    File tmpDir=session.getTmpDir();
    File tileFileCacheFile=new File(tmpDir,"tileFileName.txt");
    File tileFile=new File(tmpDir,"tile"+hIndex+"_"+vIndex+".jpg");
    downloader.downloadPage(urlTile,tileFileCacheFile);
    List<String> lines=TextUtils.readAsLines(tileFileCacheFile);
    String cacheFileUrl=lines.get(0);
    String tileUrl=Constants.ROOT_SITE+cacheFileUrl;
    downloader.downloadPage(tileUrl, tileFile);
    tileFileCacheFile.delete();
    return tileFile;
  }

  private void downloadPage(ADSession session, File out, int pageNumber, int width, int height, int tileSize)
  {
    //System.out.println("Handling "+_actsPackage._placeName+" / "+_actsPackage._period+" - page "+pageNumber);
    System.out.println("Handling page "+pageNumber);
    int nbH=(width/tileSize)+(((width%tileSize)!=0)?1:0);
    int nbV=(height/tileSize)+(((height%tileSize)!=0)?1:0);
    int x=0,y=0;
    int tileWidth, tileHeight;
    File[][] files=new File[nbH][nbV];
    for(int hIndex=0;hIndex<nbH;hIndex++)
    {
      tileWidth=Math.min(tileSize,width-x);
      y=0;
      for(int vIndex=0;vIndex<nbV;vIndex++)
      {
        tileHeight=Math.min(tileSize,height-y);
        files[hIndex][vIndex]=downloadTile(session,pageNumber,hIndex,vIndex,x,y,tileWidth,tileHeight);
        y+=tileHeight;
      }
      x+=tileWidth;
    }
    ImageMontageMaker maker=new ImageMontageMaker();
    try
    {
      maker.doIt(files, out);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
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
    }
  }

  private List<String> getPlaces(ADSession session)
  {
    List<String> placeNames=new ArrayList<String>();
    Downloader downloader=session.getDownloader();
    File tmpDir=session.getTmpDir();
    File tmpFile=new File(tmpDir,"TD_placesIndex.html");
    String url=Constants.getMainURL();
    downloader.downloadPage(url, tmpFile);
    File tmpFile2=new File(tmpDir,"visuInit.html");
    String id=Constants.ID;
    url="http://www.archinoe.net/cg62/visualiseur/visu_init.php?fonds=ec&id="+id;
    downloader.downloadPage(url, tmpFile2);
    String sessionId=downloader.getCookieValue("PHPSESSID");
    System.out.println(sessionId);
    File tmpFile3=new File(tmpDir,"page1.html");

    url="http://www.archinoe.net/cg62/visualiseur/visu_registre.php?id="+id+"&w=1280&h=1024";
    //url="http://www.archinoe.net/cg62/visualiseur/visu_affiche.php?PHPSID="+sessionId+"&param=visu_0&page=1";
    downloader.downloadPage(url, tmpFile3);

    int tile=2280;
    int nbPages=200;//Constants.NB_PAGES;
    //for(int page=1;page<=nbPages;page++)
    for(int page=nbPages;page<=nbPages;page++)
    {
      url="http://www.archinoe.net/cg62/visualiseur/visu_affiche_util.php?PHPSID="+sessionId+"&param=visu_0&o=IMG&p="+page;

      File tmpFile4=new File(tmpDir,"cache.txt");
      downloader.downloadPage(url, tmpFile4);
      List<String> lines=TextUtils.readAsLines(tmpFile4);
      String line=lines.get(0);
      String[] items=StringSplitter.split(line,'\t');
      int width=NumericTools.parseInt(items[3],0);
      int height=NumericTools.parseInt(items[4],0);
      File out=new File(Constants.ROOT_DIR,Constants.getImageName(page));
      out.getParentFile().mkdirs();
      downloadPage(session,out,page,width,height,tile);
    }

    /*
    http://www.archinoe.net/cg62/visualiseur/visu_affiche_util.php?PHPSID=1ec337e451228a3c3a5ad12f36cbacba&param=visu_0&uid=1254851219878&o=IMG&p=3
      retourne:
        /cache/ad62tablesdecennales3e2013e3003e249billyberclau1792190219131922tdnmd3e249frad0623e2490140a0003114088400000.jpg 1140  737 3464  2240
Les deux derniers chiffres indiquent la taille. On découpe en carrés de max 2280 de côté et on demande chaque bout

    // Pages :
    http://www.archinoe.net/cg62/visualiseur/visu_affiche_util.php?o=TILE&param=visu_0&p=1&x=0&y=0&l=2280&h=2240&ol=2280&oh=2240&r=0&n=0&b=0&c=0
      retourne: /cache/_ad62_tablesdecennales_3e2013e300_3e249_billyberclau1792190219131922tdnmd3e249_frad0623e2490140a0001jpg_2280_2240_0_0_2280_2240_0_0_0_0.jpg
    http://www.archinoe.net/cg62/visualiseur/visu_affiche_util.php?o=TILE&param=visu_0&p=1&x=2280&y=0&l=1160&h=2240&ol=1160&oh=2240&r=0&n=0&b=0&c=0
      retourne: /cache/_ad62_tablesdecennales_3e2013e300_3e249_billyberclau1792190219131922tdnmd3e249_frad0623e2490140a0001jpg_1160_2240_2280_0_1160_2240_0_0_0_0.jpg
*/
      /*
    List<String> lines=TextTools.splitAsLines(tmpFile);
    String line=lines.get(0);
    List<String> places=TextTools.findAllBetween(line,"<option value='","</option>");
    String separator="'>";
    String placeName1,placeName2;
    int index;
    for(String place :places)
    {
      index=place.indexOf(separator);
      placeName1=place.substring(0,index);
      placeName2=place.substring(index+separator.length());
      if (!(placeName1.equals(placeName2)))
      {
        System.err.println(place);
      }
      if (placeName1.length()>0)
      {
        placeNames.add(placeName1);
      }
    }
    tmpFile.delete();
    */
    return placeNames;
  }
}
