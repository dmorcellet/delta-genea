package delta.genea.webhoover.ad59;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import delta.common.utils.NumericTools;
import delta.common.utils.html.HtmlConstants;
import delta.common.utils.text.TextTools;
import delta.common.utils.text.TextUtils;
import delta.downloads.Downloader;
import delta.genea.webhoover.ADSession;
import delta.genea.webhoover.ActsPackage;
import delta.genea.webhoover.ImageMontageMaker;

/**
 * @author DAM
 */
public class MainDownloadActs
{
  private static final Logger LOGGER=Logger.getLogger(MainDownloadActs.class);

  /**
   * Main method of this tool.
   * @param args Not used.
   * @throws Exception If a problem occurs.
   */
  public static void main(String[] args) throws Exception
  {
    new MainDownloadActs().doIt();
  }

  private static boolean _useThreads=true;

  private void getPages(final ActsPackage actsPackage,final boolean td) throws Exception
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
          e.printStackTrace();
        }
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

  private void doIt() throws Exception
  {
    ADSession session=new ADSession();
    session.start();
    List<String> places=getPlaces(session);
    System.out.println(places);
    handlePlace(session,Constants.PLACE_NAME,true);
    session.stop();
  }

  private void handlePlace(ADSession session, String placeName, boolean td) throws Exception
  {
    List<String> periods=getPeriods(session,placeName);
    System.out.println(periods);
    for(String period : periods)
    {
      int index=period.indexOf('-');
      int from=NumericTools.parseInt(period.substring(0,index).trim(),-1);
      int to=NumericTools.parseInt(period.substring(index+1).trim(),-1);
      handlePeriod(session,placeName,from,to,td);
    }
  }

  private void handlePeriod(ADSession session, String placeName, int from, int to, boolean td) throws Exception
  {
    List<ActsPackage> actsPackages=getPackages(session,placeName,from,to);
    for(ActsPackage actsPackage : actsPackages)
    {
      getPages(actsPackage,td);
    }
  }

  private List<String> getPlaces(ADSession session) throws Exception
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
        System.err.println(place);
      }
      if (placeName1.length()>0)
      {
        placeNames.add(placeName1);
      }
    }
    tmpFile.delete();
    return placeNames;
  }

  private List<String> getPeriods(ADSession session, String placeName) throws Exception
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
        System.err.println(period);
      }
      if (period1.length()>0)
      {
        periods.add(period1);
      }
    }
    tmpFile.delete();
    return periods;
  }

  private List<ActsPackage> getPackages(ADSession session, String placeName, int from, int to) throws Exception
  {
    List<ActsPackage> packages=new ArrayList<ActsPackage>();
    Downloader downloader=session.getDownloader();
    File tmpDir=session.getTmpDir();
    File tmpFile=new File(tmpDir,"TD_packages.html");
    String url=Constants.getURL(placeName,from,to,Constants.TD);
    downloader.downloadToFile(url, tmpFile);
    List<String> lines=TextUtils.readAsLines(tmpFile);
    int index=0;
    String line,actTypeLine;
    int nbLines=lines.size();
    for(int i=0;i<nbLines;i++)
    {
      line=lines.get(i);
      if (line.contains("&page_ref"))
      {
        index++;
        if ((index>1) && ((index-1)%2==0))
        {
          String packageId=TextTools.findBetween(line,"&page_ref=","&");
          actTypeLine=lines.get(i+1);
          String actType=TextTools.findBetween(actTypeLine,"&gt;","</p>").trim();
          ActsPackage newPackage=new ActsPackage();
          newPackage._actType=actType;
          newPackage._id=packageId;
          newPackage._period=from+"-"+to;
          newPackage._placeName=placeName;
          packages.add(newPackage);
        }
      }
    }
    return packages;
  }

  /*
  private void doItSalome() throws Exception
  {
    ADSession session=new ADSession();
    session.start();
    List<String> places=getPlaces(session);
    System.out.println(places);
    ActsPackage p=new ActsPackage();
    p._id="292115";
    p._placeName="Salomé";
    p._period="1737-1760";
    p._actType="BMS";
    //handlePlace(session,Constants.PLACE_NAME);
    getPages(session,p,false);
    session.stop();
    //292115
  }
  */

  private void getPages(ADSession session, ActsPackage actsPackage, boolean td) throws Exception
  {
    System.out.println("Handling "+actsPackage+" with session "+session.getTmpDir());
    int nbPages=getPage(session,actsPackage,1,td);
    for(int i=2;i<=nbPages;i++)
    {
      getPage(session,actsPackage,i,td);
    }
  }

  private int getPage(ADSession session, ActsPackage actsPackage, int pageNumber, boolean td) throws Exception
  {
    Downloader downloader=session.getDownloader();
    File tmpDir=session.getTmpDir();
    File toDir=actsPackage.getDirFile(Constants.ROOT_DIR);
    File imageFile=Constants.getImageFile(toDir,pageNumber);
    File parent=imageFile.getParentFile();
    parent.mkdirs();

    if ((pageNumber>1) && (imageFile.exists()))
    {
      return 0;
    }
    String packageId=actsPackage._id;
    String seed="TD_package"+packageId+"_page"+pageNumber;
    File tmpFile=new File(tmpDir,seed+".html");
    String url=Constants.getPageURL(packageId,pageNumber,td);
    downloader.downloadToFile(url, tmpFile);
    List<String> lines=TextUtils.readAsLines(tmpFile);
    tmpFile.delete();
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
    sizeFile.delete();

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
    ImageMontageMaker maker=new ImageMontageMaker();
    try
    {
      maker.doIt(files, imageFile);
    }
    catch(Exception e)
    {
      imageFile.delete();
      LOGGER.error("",e);
    }
    for(int vIndex=0;vIndex<nbV;vIndex++)
    {
      boolean ok=files[0][vIndex].delete();
      if (!ok)
      {
        System.err.println("Cannot delete : "+files[0][vIndex]);
      }
    }
    //downloader.downloadPage(imageURL,imageFile);
    //System.out.println("nbPages="+nbPages);
    //System.out.println("width="+width);
    //System.out.println("height="+height);
    return nbPages;
  }
}
