package delta.genea.webhoover.free;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import delta.common.utils.files.TextFileReader;
import delta.common.utils.text.EncodingNames;
import delta.genea.webhoover.Downloader;
import delta.genea.webhoover.TextTools;
import delta.genea.webhoover.gennpdc.TmpFilesManager;

/**
 * @author DAM
 */
public class Main
{
  private Downloader _d;
  private int _nbDownloadedActs;
  private TmpFilesManager _tmp;
  
  public Main()
  {
    _d=new Downloader();
    _nbDownloadedActs=0;
  }

  private void downloadActs()
  {
    _tmp=new TmpFilesManager(Constants.TMP_NAME);
    handleDirPage(Constants.SITE,Constants.OUT_DIR);
    //tmp.cleanup();
    System.out.println(_nbDownloadedActs);
  }
  
  private void handleDirPage(String url, File out)
  {
    out.mkdirs();
    File tmpFile=_tmp.newTmpFile("main.html");
    _d.downloadPage(url,tmpFile);
    List<String> lines=TextFileReader.readAsLines(tmpFile,EncodingNames.ISO8859_1);

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
          else if (line.startsWith("<IMG SRC=\"/icons/image2.gif\""))
          {
            String href=TextTools.findBetween(line,"<A HREF=\"","</A>");
            String fileName=TextTools.findAfter(href,"\">");
            File newFile=new File(out,fileName);
            String fileUrl=url+fileName.replace(" ","%20");
            if (!newFile.canRead())
            {
              System.out.println(fileUrl+" -> "+newFile);
              _d.downloadPage(fileUrl,newFile);
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
  
  public static void main(String[] args)
  {
    new Main().downloadActs();
  }
}
