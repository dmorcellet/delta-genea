package delta.genea.webhoover.ad59;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.utils.NumericTools;
import delta.common.utils.html.HtmlConstants;
import delta.common.utils.text.TextTools;
import delta.common.utils.text.TextUtils;
import delta.downloads.DownloadException;
import delta.downloads.Downloader;
import delta.genea.webhoover.ADSession;
import delta.genea.webhoover.ActsPackage;
import delta.genea.webhoover.utils.FileUtils;
import delta.genea.webhoover.utils.ImageUtils;

/**
 * Main class for the AD59 webhoover.
 * @author DAM
 */
public class MainDownloadActs
{
  private static final Logger LOGGER=LoggerFactory.getLogger(MainDownloadActs.class);

  private static final boolean USE_THREADS=true;

  private File _rootDir;

  private MainDownloadActs(File rootDir)
  {
    _rootDir=rootDir;
  }

  private void getPages(final ActsPackage actsPackage,final boolean td)
  {
    Runnable r=new Runnable()
    {
      public void run()
      {
        ADSession localSession=new ADSession();
        localSession.start();
        try
        {
          getPages(localSession,actsPackage,td);
        }
        catch(Exception e)
        {
          LOGGER.error("Exception with package: "+actsPackage,e);
        }
        localSession.stop();
      }
    };
    if (USE_THREADS)
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

  private void doIt() throws DownloadException
  {
    ADSession session=new ADSession();
    session.start();
    List<String> places=getPlaces(session);
    LOGGER.info("Got places: {}",places);
    handlePlace(session,Constants.PLACE_NAME,true);
    session.stop();
  }

  private void handlePlace(ADSession session, String placeName, boolean td) throws DownloadException
  {
    List<String> periods=getPeriods(session,placeName);
    LOGGER.info("Got periods: {}",periods);
    for(String period : periods)
    {
      int index=period.indexOf('-');
      int from=NumericTools.parseInt(period.substring(0,index).trim(),-1);
      int to=NumericTools.parseInt(period.substring(index+1).trim(),-1);
      handlePeriod(session,placeName,from,to,td);
    }
  }

  private void handlePeriod(ADSession session, String placeName, int from, int to, boolean td) throws DownloadException
  {
    List<ActsPackage> actsPackages=getPackages(session,placeName,from,to);
    for(ActsPackage actsPackage : actsPackages)
    {
      getPages(actsPackage,td);
    }
  }

  private List<String> getPlaces(ADSession session) throws DownloadException
  {
    List<String> placeNames=new ArrayList<String>();
    Downloader downloader=session.getDownloader();
    File tmpDir=session.getTmpDir();
    File tmpFile=new File(tmpDir,"TD_placesIndex.html");
    String url=Constants.getIndexURL(null,Constants.TD);
    downloader.downloadToFile(url, tmpFile);
    List<String> lines=TextUtils.readAsLines(tmpFile);
    String line=lines.get(0);
    List<String> places=TextTools.findAllBetween(line,"<option value='","</option>");
    String separator="'>";
    for(String place : places)
    {
      int index=place.indexOf(separator);
      String placeName1=place.substring(0,index);
      String placeName2=place.substring(index+separator.length());
      if (!(placeName1.equals(placeName2)))
      {
        LOGGER.warn("Bad place value: [{}]",place);
      }
      if (placeName1.length()>0)
      {
        placeNames.add(placeName1);
      }
    }
    FileUtils.deleteFile(tmpFile);
    return placeNames;
  }

  private List<String> getPeriods(ADSession session, String placeName) throws DownloadException
  {
    List<String> periods=new ArrayList<String>();
    Downloader downloader=session.getDownloader();
    File tmpDir=session.getTmpDir();
    File tmpFile=new File(tmpDir,"TD_periods.html");
    
    String url=Constants.getIndexURL(placeName,Constants.TD);
    downloader.downloadToFile(url, tmpFile);
    List<String> lines=TextUtils.readAsLines(tmpFile);
    String line=lines.get(0);
    List<String> periodOptions=TextTools.findAllBetween(line,"<option value='","</option>");
    String separator="'>";
    for(String period : periodOptions)
    {
      int index=period.indexOf(separator);
      String period1=period.substring(0,index);
      String period2=period.substring(index+separator.length());
      if (!(period1.equals(period2)))
      {
        LOGGER.warn("Bad period value: [{}]",period);
      }
      if (period1.length()>0)
      {
        periods.add(period1);
      }
    }
    FileUtils.deleteFile(tmpFile);
    return periods;
  }

  private List<ActsPackage> getPackages(ADSession session, String placeName, int from, int to) throws DownloadException
  {
    List<ActsPackage> packages=new ArrayList<ActsPackage>();
    Downloader downloader=session.getDownloader();
    File tmpDir=session.getTmpDir();
    File tmpFile=new File(tmpDir,"TD_packages.html");
    String url=Constants.getURL(placeName,from,to,Constants.TD);
    downloader.downloadToFile(url, tmpFile);
    List<String> lines=TextUtils.readAsLines(tmpFile);
    int index=0;
    int nbLines=lines.size();
    for(int i=0;i<nbLines;i++)
    {
      String line=lines.get(i);
      if (line.contains("&page_ref"))
      {
        index++;
        if ((index>1) && ((index-1)%2==0))
        {
          String packageId=TextTools.findBetween(line,"&page_ref=","&");
          String actTypeLine=lines.get(i+1);
          String actType=TextTools.findBetween(actTypeLine,"&gt;","</p>").trim();
          ActsPackage newPackage=new ActsPackage();
          newPackage.setActType(actType);
          newPackage.setId(packageId);
          newPackage.setPeriod(from+"-"+to);
          newPackage.setPlaceName(placeName);
          packages.add(newPackage);
        }
      }
    }
    return packages;
  }

  void doItSalome() throws DownloadException
  {
    ADSession session=new ADSession();
    session.start();
    List<String> places=getPlaces(session);
    LOGGER.info("Got places: {}",places);
    ActsPackage p=new ActsPackage();
    p.setId("292115");
    p.setPlaceName("Salomé");
    p.setPeriod("1737-1760");
    p.setActType("BMS");
    getPages(session,p,false);
    session.stop();
  }

  private void getPages(ADSession session, ActsPackage actsPackage, boolean td) throws DownloadException
  {
    LOGGER.info("Handling {} with session {}",actsPackage,session.getTmpDir());
    int nbPages=getPage(session,actsPackage,1,td);
    for(int i=2;i<=nbPages;i++)
    {
      getPage(session,actsPackage,i,td);
    }
  }

  private int getPage(ADSession session, ActsPackage actsPackage, int pageNumber, boolean td) throws DownloadException
  {
    Downloader downloader=session.getDownloader();
    File tmpDir=session.getTmpDir();
    File toDir=actsPackage.getDirFile(_rootDir);
    File imageFile=Constants.getImageFile(toDir,pageNumber);
    File parent=imageFile.getParentFile();
    parent.mkdirs();

    if ((pageNumber>1) && (imageFile.exists()))
    {
      return 0;
    }
    String packageId=actsPackage.getId();
    String seed="TD_package"+packageId+"_page"+pageNumber;
    File tmpFile=new File(tmpDir,seed+".html");
    String url=Constants.getPageURL(packageId,pageNumber,td);
    downloader.downloadToFile(url, tmpFile);
    List<String> lines=TextUtils.readAsLines(tmpFile);
    FileUtils.deleteFile(tmpFile);
    String naonedViewerLine=TextTools.findLine(lines,"v1 = new NAONED_VIEWER(");
    String fileName=TextTools.findBetween(naonedViewerLine,"new NAONED_VIEWER('","',main_w,main_h");
    String nbPagesLines=TextTools.findLine(lines,"<div class=\"d32_a\" id=\"d32_a\">");
    String nbPagesStr=TextTools.findBetween(nbPagesLines,"<div class=\"d32_a\" id=\"d32_a\">",HtmlConstants.END_DIV);
    int nbPages=NumericTools.parseInt(nbPagesStr,0);
    File sizeFile=new File(tmpFile.getParentFile(),seed+"_size.txt");
    String sizeUrl=Constants.getSizeURL(fileName);
    downloader.downloadToFile(sizeUrl, sizeFile);
    String sizeLine=TextUtils.readAsLines(sizeFile).get(0);
    String widthStr=TextTools.findBetween(sizeLine,"main_w=",";");
    int width=NumericTools.parseInt(widthStr,0);
    String heightStr=TextTools.findBetween(sizeLine,"main_h=",";");
    int height=NumericTools.parseInt(heightStr,0);
    FileUtils.deleteFile(sizeFile);

    final int CHUNK=100;
    int nbV=(height/CHUNK)+((height%CHUNK==0)?0:1);
    File[][] files=new File[1][nbV];
    for(int vIndex=0;vIndex<nbV;vIndex++)
    {
      String vStr=String.valueOf(vIndex);
      if (vIndex<10) vStr="0"+vStr;
      String name="00_"+vStr+"_"+imageFile.getName();
      File image=new File(parent,name);
      String imageURLStr=Constants.getImageURL(fileName,0,vIndex*CHUNK,width,CHUNK);
      downloader.downloadToFile(imageURLStr, image);
      files[0][vIndex]=image;
    }
    ImageUtils.makeImage(files,imageFile);
    return nbPages;
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
