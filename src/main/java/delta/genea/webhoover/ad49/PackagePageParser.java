package delta.genea.webhoover.ad49;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import delta.common.utils.NumericTools;
import delta.common.utils.files.TextFileWriter;
import delta.common.utils.text.StringSplitter;
import delta.common.utils.text.TextUtils;
import delta.downloads.Downloader;
import delta.genea.webhoover.ActsPackage;
import delta.genea.webhoover.ImageMontageMaker;

public class PackagePageParser
{
  private File _rootActsPackageDir;
  private AD49Session _session;
	private ActsPackage _actsPackage;
  private int _nbPages;

  public PackagePageParser(AD49Session session, ActsPackage actsPackage)
	{
    _session=session;
		_actsPackage=actsPackage;
		_rootActsPackageDir=_actsPackage.getDirFile(Constants.ROOT_DIR);
	}

  private File downloadTile(int pageNumber, int hIndex, int vIndex, int x, int y, int width, int height) throws Exception
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
    downloader.downloadToFile(tileUrl, tileFile);
    tileFileCacheFile.delete();
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

  private void downloadPage(File out, int pageNumber, int width, int height, int tileSize) throws Exception
  {
    out.getParentFile().mkdirs();
    System.out.println("Handling "+_actsPackage._placeName+" / "+_actsPackage._period+" - page "+pageNumber);
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
        files[hIndex][vIndex]=downloadTile(pageNumber,hIndex,vIndex,x,y,tileWidth,tileHeight);
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

  private void buildInfoFile(File to)
  {
    TextFileWriter w=new TextFileWriter(to);
    if (w.start())
    {
      w.writeNextLine("Commune : "+_actsPackage._placeName);
      if (_actsPackage._church.length()>0)
      {
        w.writeNextLine("Paroisse : "+_actsPackage._church);
      }
      w.writeNextLine("Actes : "+_actsPackage._actType);
      w.writeNextLine("Période : "+_actsPackage._period);
      w.writeNextLine("Collection : "+_actsPackage._source);
      if (_actsPackage._comments.length()>0)
      {
        w.writeNextLine("Notes, détails, lacunes : ");
        w.writeNextLine(_actsPackage._comments);
      }
      w.terminate();
    }
  }

  public File getRootDir()
  {
    return _rootActsPackageDir;
  }

  public void parse() throws Exception
	{
    _rootActsPackageDir.mkdirs();
    buildInfoFile(new File(_rootActsPackageDir,"infos.txt"));
    String id=_actsPackage._id;
    Downloader downloader=_session.getDownloader();
    String phpSID=_session.getPHPSessionID();
    File tmpDir=_session.getTmpDir();
    String urlRegistrePrepare=Constants.ROOT_SITE+"/cg49work/registre_prepare.php?id="+id+"&PHPSID="+phpSID+"&hauteur=1024&largeur=1280&code=Mozilla&nom=Netscape&version=5.0%20(X11;%20fr)&langue=fr&platform=Linux%20x86_64";
    File tmpFile=new File(tmpDir,"registre_prepare.php.html");
    downloader.downloadToFile(urlRegistrePrepare, tmpFile);
    tmpFile.delete();
    String urlVisu=Constants.ROOT_SITE+"/cg49work/visu_affiche.php?PHPSID="+phpSID+"&param=visu&page=1";
    tmpFile=new File(tmpDir,"visu_affiche.php.html");
    downloader.downloadToFile(urlVisu, tmpFile);

    // Calcule le nombre de pages
    int nbPages=0;
    {
      List<String> lines=TextUtils.readAsLines(tmpFile);
      String line;
      int index;
      String start="if (pageLoad>";
      for(Iterator<String> it2=lines.iterator();it2.hasNext();)
      {
        line=it2.next();
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
    }
    tmpFile.delete();
    _nbPages=nbPages;
	}

  public void downloadPages(final String name, int minIndex, int maxIndex) throws Exception
  {
    Downloader downloader=_session.getDownloader();
    String phpSID=_session.getPHPSessionID();
    File tmpDir=_session.getTmpDir();
    if (minIndex<=0)
    {
      minIndex=1;
      System.out.println(name+": "+"Bad min page index : 1");
      return;
    }
    if (minIndex>_nbPages)
    {
      minIndex=_nbPages;
      System.out.println(name+": "+"Bad min page index : ("+minIndex+">"+_nbPages+")");
      return;
    }
    if (maxIndex>_nbPages)
    {
      maxIndex=_nbPages;
      System.out.println(name+": "+"Bad max page index : ("+maxIndex+">"+_nbPages+")");
      return;
    }
    if (maxIndex<minIndex)
    {
      maxIndex=minIndex;
      System.out.println(name+": "+"Bad max page index : ("+maxIndex+"<"+minIndex+")");
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
          newName=name.replace(".jpg","-"+String.valueOf(page-minIndex+1)+".jpg");
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
        downloader.downloadToFile(urlAffiche, infoFile);
        List<String> lines=TextUtils.readAsLines(infoFile);
        infoFile.delete();
        String infosStr=lines.get(0);
        String[] infos=StringSplitter.split(infosStr,'\t');

        int width=NumericTools.parseInt(infos[3],-1);
        int height=NumericTools.parseInt(infos[4],-1);
        int tile=2280;
        downloadPage(out,page,width,height,tile);
        //System.out.println(out);
        //downloader.status();
      }
    }
  }

  public void downloadAllPages() throws Exception
  {
    downloadPages(null,1,_nbPages);
  }
}
