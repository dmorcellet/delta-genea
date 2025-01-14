package delta.genea.webhoover.free;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.utils.text.EncodingNames;
import delta.common.utils.text.TextTools;
import delta.common.utils.text.TextUtils;
import delta.downloads.DownloadException;
import delta.downloads.Downloader;
import delta.genea.webhoover.utils.TmpFilesManager;

/**
 * Main class of the free webhoover.
 * @author DAM
 */
public class Main
{
  private static final Logger LOGGER=LoggerFactory.getLogger(Main.class);

  /**
   * Root site URL.
   */
  // Could use: http://geneasion.free.fr/Numerisation/BMSCamphin/
  // Could use: http://ph.sion.free.fr/tabsion/
  private static final String SITE="http://geneavenir.free.fr/prisonniers%20de%20guerres/";

  /**
   * Name for the directory of temporary files.
   */
  private static final String TMP_NAME="prisonniers";

  private Downloader _d;
  private int _nbDownloadedActs;
  private TmpFilesManager _tmp;

  /**
   * Constructor.
   */
  public Main()
  {
    _d=new Downloader();
    _nbDownloadedActs=0;
  }

  private void downloadActs(File toDir) throws DownloadException
  {
    _tmp=new TmpFilesManager(TMP_NAME);
    handleDirPage(SITE,toDir);
    LOGGER.info("Downloaded {} acts",Integer.valueOf(_nbDownloadedActs));
  }
  
  private void handleDirPage(String url, File out) throws DownloadException
  {
    out.mkdirs();
    File tmpFile=_tmp.newTmpFile("main.html");
    _d.downloadToFile(url,tmpFile);
    List<String> lines=TextUtils.readAsLines(tmpFile,EncodingNames.ISO8859_1);

/* Looks like:
<IMG SRC="/icons/back.gif" ALT="[DIR]"> <A HREF="/Numerisation/">Parent Directory</A>        12-Mar-2010 18:07      -  
<IMG SRC="/icons/folder.gif" ALT="[DIR]"> <A HREF="bms%201694/">bms 1694/</A>               12-Mar-2010 08:27      -  
<IMG SRC="/icons/folder.gif" ALT="[DIR]"> <A HREF="bms%201695/">bms 1695/</A>               12-Mar-2010 08:27      -  
<IMG SRC="/icons/folder.gif" ALT="[DIR]"> <A HREF="bms%201696/">bms 1696/</A>               12-Mar-2010 08:28      -  
<IMG SRC="/icons/image2.gif" ALT="[IMG]"> <A HREF="1694-6.jpg">1694-6.jpg</A>              12-Mar-2010 08:27   332k  
<IMG SRC="/icons/image2.gif" ALT="[IMG]"> <A HREF="an%201694.jpg">an 1694.jpg</A>             12-Mar-2010 08:27   300k  
 */
    boolean gotParentDirLine=false;
    List<String> dirUrls=new ArrayList<String>();
    List<String> dirItems=new ArrayList<String>();
    for(String line : lines)
    {
      if (line.contains(">Parent Directory<"))
      {
        gotParentDirLine=true;
      }
      else
      {
        if (gotParentDirLine)
        {
          if (line.startsWith("<IMG SRC=\"/icons/folder.gif\""))
          {
            String href=TextTools.findBetween(line,"<A HREF=","</A>");
            String dirURL=TextTools.findBetween(href,"\"","/");
            dirUrls.add(dirURL);
            String dirName=TextTools.findBetween(href,"\">","/");
            dirItems.add(dirName);
          }
          else
          {
            String href=TextTools.findBetween(line,"<A HREF=","</A>");
            String fileUrl=TextTools.findBetween(href,"\"","\"");
            String fileName=fileUrl.replace("%20"," ");
            fileName=fileName.replace("%c3%a7","ç");
            File newFile=new File(out,fileName);
            if (!newFile.canRead())
            {
              fileUrl=url+fileUrl;
              LOGGER.info("Downloading URL {} to file {}",fileUrl,newFile);
              _d.downloadToFile(fileUrl,newFile);
            }
          }
        }
      }
    }
    int nbDirs=dirItems.size();
    for(int i=0;i<nbDirs;i++)
    {
      String dirItem=dirItems.get(i);
      String dirURL=dirUrls.get(i);
      if (dirItem.contains("9033"))
        handleDirPage(url+dirURL+"/",new File(out,dirItem));
    }
  }

  /**
   * Main method for this tool.
   * @param args Output directory.
   * @throws DownloadException
   */
  public static void main(String[] args) throws DownloadException
  {
    File toDir=new File(args[0]);
    new Main().downloadActs(toDir);
  }
}
