package delta.genea.webhoover.ad01;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.utils.NumericTools;
import delta.common.utils.text.TextUtils;
import delta.downloads.DownloadException;
import delta.downloads.Downloader;
import delta.genea.webhoover.ActsPackage;
import delta.genea.webhoover.ImageMontageMaker;

/**
 * Provides downloading facilities for pages of an acts package.
 * @author DAM
 */
public class PackagePageParser
{
  private static final Logger LOGGER=LoggerFactory.getLogger(PackagePageParser.class);

  private static final String START_OF_NB_PAGES_LINE="var iNbMax = ";
  private static final String START_IMAGE_NAME_LINE="CTX[\"IMAGE\"]=\"";
  private static final String START_CHUNK_HSIZE_LINE="CTX[\"WDALX\"]=";
  private static final String START_CHUNK_VSIZE_LINE="CTX[\"WDALY\"]=";
  private static final String START_HSIZE_LINE="CTX[\"AFORM\"][1][\"LARG\"]=";
  private static final String START_VSIZE_LINE="CTX[\"AFORM\"][1][\"HAUT\"]=";

  private int _nbPages=0;
  private Downloader _downloader;
  private ActsPackage _actsPackage;

  /**
   * Constructor.
   * @param downloader Downloader to use.
   * @param actsPackage Acts package to use.
   */
  public PackagePageParser(Downloader downloader, ActsPackage actsPackage)
  {
    _downloader=downloader;
    _actsPackage=actsPackage;
  }

  /**
   * Parse pages.
   * @throws Exception If a problem occurs.
   */
  public void parse() throws Exception
  {
    parsePage(0);
    for(int i=1;i<_nbPages;i++)
    {
      parsePage(i);
    }
  }

  /**
   * Get the directory name for a package.
   * @return A directory name.
   */
  public String getDirName()
  {
    String ret=_actsPackage._period;
    if ((ret.indexOf("AN 6-AN 9")!=-1) ||
      (ret.indexOf("AN 6-AN 10")!=-1) ||
      (ret.indexOf("AN 2-AN 2")!=-1) ||
      (ret.indexOf("AN 2-AN 5")!=-1) ||
      (ret.indexOf("AN 6")!=-1) ||
      (ret.indexOf("AN 7")!=-1))
    {
      ret=ret+" ("+_actsPackage._actType+")";
    }
    return ret;
  }

  private void parsePage(int nb) throws Exception
  {
    File out=getImageFileName(nb);
    File bigOut=getBigImageFileName(nb);
    File rootDir=new File(Constants.ROOT_DIR,getDirName());
    rootDir.mkdirs();
    File page=new File(rootDir,"page.html");
    String url=_actsPackage._link;
    if (nb>0)
    {
      if ((out.exists())&&(bigOut.exists()))
      {
        // System.out.println("ALREADY EXISTS page : "+page2);
        return;
      }
      url=url.substring(0,url.indexOf('?'));
      url=url+"?INB="+(nb+1);
    }
    _downloader.downloadToFile(url,page);
    List<String> lines=TextUtils.readAsLines(page);
    String line;
    int index;
    String imageName="";
    int value;
    int chunkHSize=0;
    int chunkVSize=0;
    int hsize=0;
    int vsize=0;
    for(Iterator<String> it=lines.iterator();it.hasNext();)
    {
      line=it.next();
      if (nb==0)
      {
        value=parseIntValue(line,START_OF_NB_PAGES_LINE);
        if (value!=-1) _nbPages=value;
      }
      index=line.indexOf(START_IMAGE_NAME_LINE);
      if (index!=-1)
      {
        imageName=line.substring(START_IMAGE_NAME_LINE.length());
        imageName=imageName.substring(0,imageName.length()-2);
      }
      value=parseIntValue(line,START_CHUNK_HSIZE_LINE);
      if (value!=-1) chunkHSize=value;
      value=parseIntValue(line,START_CHUNK_VSIZE_LINE);
      if (value!=-1) chunkVSize=value;
      value=parseIntValue(line,START_HSIZE_LINE);
      if (value!=-1) hsize=value;
      value=parseIntValue(line,START_VSIZE_LINE);
      if (value!=-1) vsize=value;
    }
    downloadImage(nb,imageName);
    int nbH=(hsize/chunkHSize)+(((hsize%chunkHSize)!=0)?1:0);
    int nbV=(vsize/chunkVSize)+(((vsize%chunkVSize)!=0)?1:0);
    downloadBigImage(nb,imageName,nbH,nbV);
    page.delete();
    System.gc();
  }

  private int parseIntValue(String line, String startOfLine)
  {
    int ret=-1;
    int index=line.indexOf(startOfLine);
    if (index!=-1)
    {
      String str=line.substring(index+startOfLine.length());
      str=str.substring(0,str.length()-1);
      str=str.trim();
      ret=NumericTools.parseInt(str,0);
    }
    return ret;
  }

  private void downloadImage(int nb, String imageName) throws DownloadException
  {
    String url=Constants.VISU_PAGE+"IMAGE="+imageName+"&SI=img0";
    File out=getImageFileName(nb);
    File parent=out.getParentFile();
    parent.mkdirs();
    if (!out.exists())
    {
      _downloader.downloadToFile(url,out);
    }
  }

  private File getImageFileName(int nb)
  {
    File rootDir=new File(Constants.ROOT_DIR,getDirName());
    String nbStr=String.valueOf(nb);
    if (nb<10) nbStr="0"+nbStr;
    if (nb<100) nbStr="0"+nbStr;
    File out=new File(rootDir,nbStr+".jpg");
    return out;
  }

  private File getBigImageFileName(int nb)
  {
    File rootDir=new File(Constants.ROOT_DIR,getDirName());
    String nbStr=String.valueOf(nb);
    if (nb<10) nbStr="0"+nbStr;
    if (nb<100) nbStr="0"+nbStr;
    File out=new File(rootDir,"big"+nbStr+".png");
    return out;
  }

  private void downloadBigImage(int nb, String imageName, int nbH, int nbV) throws Exception
  {
    File out=getBigImageFileName(nb);
    File parent=out.getParentFile();
    parent.mkdirs();
    if (out.exists())
    {
      return;
    }
    File[][] files=new File[nbH][nbV];
    for(int hIndex=0;hIndex<nbH;hIndex++)
    {
      for(int vIndex=0;vIndex<nbV;vIndex++)
      {
        String hStr=String.valueOf(hIndex);
        if (hIndex<10) hStr="0"+hStr;
        String vStr=String.valueOf(vIndex);
        if (vIndex<10) vStr="0"+vStr;

        String name=hStr+"_"+vStr+"_"+out.getName().replace("png","jpg");
        String url=Constants.VISU_PAGE+"IMAGE="+imageName+"&SI=1/img_"+hStr+"_"+vStr;
        File image=new File(parent,name);
        _downloader.downloadToFile(url,image);
        files[hIndex][vIndex]=image;
      }
    }
    ImageMontageMaker maker=new ImageMontageMaker();
    try
    {
      maker.doIt(files,out);
    }
    catch (Exception e)
    {
      LOGGER.warn("Could not make image!",e);
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
}
