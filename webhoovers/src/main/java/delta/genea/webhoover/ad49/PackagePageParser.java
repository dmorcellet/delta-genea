package delta.genea.webhoover.ad49;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.utils.NumericTools;
import delta.common.utils.files.TextFileWriter;
import delta.common.utils.text.StringSplitter;
import delta.common.utils.text.TextUtils;
import delta.downloads.DownloadException;
import delta.downloads.Downloader;
import delta.genea.webhoover.ActsPackage;
import delta.genea.webhoover.utils.FileUtils;
import delta.genea.webhoover.utils.ImageUtils;

/**
 * Provides downloading facilities for pages of an acts package.
 * @author DAM
 */
public class PackagePageParser
{
  private static final Logger LOGGER=LoggerFactory.getLogger(PackagePageParser.class);

  private File _rootActsPackageDir;
  private AD49Session _session;
  private ActsPackage _actsPackage;
  private int _nbPages;

  /**
   * Constructor.
   * @param session Session to use.
   * @param actsPackage Acts package to use.
   */
  public PackagePageParser(AD49Session session, ActsPackage actsPackage)
  {
    _session=session;
    _actsPackage=actsPackage;
    _rootActsPackageDir=_actsPackage.getDirFile(Constants.ROOT_DIR);
  }

  private File downloadTile(int pageNumber, int hIndex, int vIndex, int x, int y, int width, int height) throws DownloadException
  {
    String phpSID=_session.getPHPSessionID();
    String urlTile=Constants.ROOT_SITE+"/cg49work/visu_affiche_util.php?o=TILE&param=visu&p="+pageNumber+"&x="+x+"&y="+y+"&l="+width+"&h="+height+"&ol="+width+"&oh="+height+"&r=0&n=0&b=0&c=0&PHPSID="+phpSID;

    Downloader downloader=_session.getDownloader();
    File tmpDir=_session.getTmpDir();
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

  private File getImageFile(File rootDir, int pageNumber)
  {
    String fileName=String.valueOf(pageNumber);
    if (pageNumber<10) fileName="0"+fileName;
    if (pageNumber<100) fileName="0"+fileName;
    if (pageNumber<1000) fileName="0"+fileName;
    File out=new File(rootDir,"page_"+fileName+".jpg");
    return out;
  }

  private void downloadPage(File out, int pageNumber, int width, int height, int tileSize) throws DownloadException
  {
    out.getParentFile().mkdirs();
    LOGGER.info("Handling {} / {} - page {}",_actsPackage.getPlaceName(),_actsPackage.getPeriod(),Integer.valueOf(pageNumber));
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
        files[hIndex][vIndex]=downloadTile(pageNumber,hIndex,vIndex,x,y,tileWidth,tileHeight);
        y+=tileHeight;
      }
      x+=tileWidth;
    }
    ImageUtils.makeImage(files,out);
  }

  private void buildInfoFile(File to)
  {
    TextFileWriter w=new TextFileWriter(to);
    if (w.start())
    {
      w.writeNextLine("Commune : "+_actsPackage.getPlaceName());
      if (_actsPackage.getChurch().length()>0)
      {
        w.writeNextLine("Paroisse : "+_actsPackage.getChurch());
      }
      w.writeNextLine("Actes : "+_actsPackage.getActType());
      w.writeNextLine("Période : "+_actsPackage.getPeriod());
      w.writeNextLine("Collection : "+_actsPackage.getSource());
      if (_actsPackage.getComments().length()>0)
      {
        w.writeNextLine("Notes, détails, lacunes : ");
        w.writeNextLine(_actsPackage.getComments());
      }
      w.terminate();
    }
  }

  /**
   * Parse package and get the number of pages.
   * @throws DownloadException If a problem occurs.
   */
  public void parse() throws DownloadException
  {
    _rootActsPackageDir.mkdirs();
    buildInfoFile(new File(_rootActsPackageDir,"infos.txt"));
    String id=_actsPackage.getId();
    Downloader downloader=_session.getDownloader();
    String phpSID=_session.getPHPSessionID();
    File tmpDir=_session.getTmpDir();
    String urlRegistrePrepare=Constants.ROOT_SITE+"/cg49work/registre_prepare.php?id="+id+"&PHPSID="+phpSID+"&hauteur=1024&largeur=1280&code=Mozilla&nom=Netscape&version=5.0%20(X11;%20fr)&langue=fr&platform=Linux%20x86_64";
    File tmpFile=new File(tmpDir,"registre_prepare.php.html");
    downloader.downloadToFile(urlRegistrePrepare,tmpFile);
    FileUtils.deleteFile(tmpFile);
    String urlVisu=Constants.ROOT_SITE+"/cg49work/visu_affiche.php?PHPSID="+phpSID+"&param=visu&page=1";
    tmpFile=new File(tmpDir,"visu_affiche.php.html");
    downloader.downloadToFile(urlVisu,tmpFile);

    // Calcule le nombre de pages
    int nbPages=0;
    List<String> lines=TextUtils.readAsLines(tmpFile);
    int index;
    String start="if (pageLoad>";
    for(String line:lines)
    {
      index=line.indexOf(start);
      if (index!=-1)
      {
        line=line.substring(index+start.length());
        index=line.indexOf(")");
        if (index!=-1)
        {
          nbPages=NumericTools.parseInt(line.substring(0,index),0);
        }
        break;
      }
    }
    FileUtils.deleteFile(tmpFile);
    _nbPages=nbPages;
  }

  /**
   * Download a series of pages.
   * @param name Hint on the name of generated files ("xxx.jpg"). May be <code>null</code>.
   * @param minIndex Index of the first page (starting at 1).
   * @param maxIndex Index of the last page (maximum is the number of pages in this package).
   * @throws DownloadException If a problem occurs.
   */
  public void downloadPages(final String name, int minIndex, int maxIndex) throws DownloadException
  {
    Downloader downloader=_session.getDownloader();
    String phpSID=_session.getPHPSessionID();
    File tmpDir=_session.getTmpDir();
    if (minIndex<=0)
    {
      LOGGER.warn("{}: "+"Bad min page index: 1",name);
      return;
    }
    if (minIndex>_nbPages)
    {
      LOGGER.warn("{}: "+"Bad min page index : ({}>{})",name,Integer.valueOf(minIndex),Integer.valueOf(_nbPages));
      return;
    }
    if (maxIndex>_nbPages)
    {
      LOGGER.warn("{}: "+"Bad max page index : ({}>{})",name,Integer.valueOf(maxIndex),Integer.valueOf(_nbPages));
      return;
    }
    if (maxIndex<minIndex)
    {
      LOGGER.warn("{}: "+"Bad max page index : ({}<{})",name,Integer.valueOf(maxIndex),Integer.valueOf(minIndex));
      return;
    }
    for(int page=minIndex;page<=maxIndex;page++)
    {
      File out=null;
      if (name!=null)
      {
        String newName=name;
        if (page>minIndex)
        {
          newName=name.replace(".jpg","-"+(page-minIndex+1)+".jpg");
        }
        out=new File(Constants.ROOT_ACTS_DIR,newName);
      }
      else
      {
        out=getImageFile(_rootActsPackageDir,page);
      }
      out.getParentFile().mkdirs();
      if (!out.exists())
      {
        String urlAffiche=Constants.ROOT_SITE+"/cg49work/visu_affiche_util.php?PHPSID="+phpSID+"&param=visu&uid="+System.currentTimeMillis()+"&o=IMG&p="+page;
        File infoFile=new File(tmpDir,"visu_affiche_util.php.html");
        downloader.downloadToFile(urlAffiche,infoFile);
        List<String> lines=TextUtils.readAsLines(infoFile);
        FileUtils.deleteFile(infoFile);
        String infosStr=lines.get(0);
        String[] infos=StringSplitter.split(infosStr,'\t');

        int width=NumericTools.parseInt(infos[3],-1);
        int height=NumericTools.parseInt(infos[4],-1);
        int tile=2280;
        downloadPage(out,page,width,height,tile);
      }
    }
  }

  /**
   * Download all pages in the managed package.
   * @throws DownloadException If a problem occurs.
   */
  public void downloadAllPages() throws DownloadException
  {
    downloadPages(null,1,_nbPages);
  }
}
