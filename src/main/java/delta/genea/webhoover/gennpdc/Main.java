package delta.genea.webhoover.gennpdc;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import delta.common.utils.NumericTools;
import delta.common.utils.files.FilesFinder;
import delta.common.utils.files.TextFileReader;
import delta.common.utils.files.filter.ExtensionPredicate;
import delta.common.utils.text.EncodingNames;
import delta.genea.webhoover.Downloader;
import delta.genea.webhoover.TextTools;

/**
 * @author DAM
 */
public class Main
{
  private static final String BIRTHS_LINE_START="<td>&nbsp;<a href=\"/releves/tab_naiss.php?args=";
  private static final String BIRTH_ACT_REF_LINE_SEED="<a href=\"/releves/acte_naiss.php?xid=";
  private static final String PLACE_SEED="<strong>Commune/Paroisse</strong>"; 
  private static final String BABY_SEED="<strong>Nouveau-n";
  private static final String DATE_SEED="Acte daté du : ";
  private static final String FATHER_SEED="<strong>Père</strong> : </td>";
  private static final String MOTHER_SEED="<strong>Mère</strong> : </td>";
  private static final DateFormat DATE_FORMAT=new SimpleDateFormat("dd/MM/yyyy");

  private Downloader _d;
  private int _nbDownloadedActs;
  
  public Main()
  {
    _d=new Downloader();
    _nbDownloadedActs=0;
  }

  private BirthAct readFile(File f)
  {
    //System.out.println(f);
    BirthAct act=null;
    List<String> lines=TextFileReader.readAsLines(f,EncodingNames.ISO8859_1);
    for(String line : lines)
    {
      if (line.contains("Acte de naissance/bap"))
      {
        act=new BirthAct();
      }
      if (line.contains(PLACE_SEED))
      {
        int index=line.indexOf(PLACE_SEED);
        act._place=TextTools.findBetween(line.substring(index+PLACE_SEED.length()),"<strong>","</strong></a>");
      }
      if (line.contains(BABY_SEED))
      {
        int index=line.indexOf(BABY_SEED);
        String tmp=line.substring(index+BABY_SEED.length());
        act._lastName=TextTools.findBetween(tmp,"<strong>","</strong></a>");
        act._firstName=TextTools.findBetween(tmp,"</strong></a> <strong>","</strong></td></tr>");
      }
      if (line.contains(DATE_SEED))
      {
        String dateStr=TextTools.findBetween(line,"<strong>","</strong></td>");
        try
        {
          act._date=DATE_FORMAT.parse(dateStr);
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }
      }
      if (line.contains(FATHER_SEED))
      {
        act._father=TextTools.findBetween(line,"<td class=\"fich1\">","</td></tr>");
      }
      if (line.contains(MOTHER_SEED))
      {
        act._mother=TextTools.findBetween(line,"<td class=\"fich1\">","</td></tr>");
      }
    }
    return act;
  }

  private void downloadBirthActs(String placeName)
  {
    String url=Constants.URL_BIRTH_ACTS_FOR_PLACE+"Billy-Berclau";
    TmpFilesManager tmp=new TmpFilesManager("gennpdc");
    File tmpFile=tmp.newTmpFile("main.html");
    handleBirthPage(url,tmpFile,true);
    tmp.cleanup();
    System.out.println(_nbDownloadedActs);
  }
  
  private void handleBirthPage(String url, File out, boolean lookForMultiplePages)
  {
    _d.downloadPage(url,out);
    List<String> lines=TextFileReader.readAsLines(out,EncodingNames.ISO8859_1);
    boolean foundBirthRefs=false;
    for(String line : lines)
    {
      if (line.startsWith(BIRTHS_LINE_START))
      {
        //<a href="/releves/tab_naiss.php?args=Billy-Berclau,_C">CARLIER à CUVELIER</a></td>
        String partialURL=TextTools.findBetween(line,"<a href=\"","\">");
        System.out.println(partialURL);
        String newURL=Constants.SITE+partialURL;
        handleBirthPage(newURL,out,lookForMultiplePages);
      }
      if (line.contains(BIRTH_ACT_REF_LINE_SEED))
      {
        String partialURL=TextTools.findBetween(line,"<a href=\"","\">");
        String newURL=Constants.SITE+partialURL;
        System.out.println(newURL);
        foundBirthRefs=true;
        _nbDownloadedActs++;
        int index=newURL.indexOf("xid=");
        int id=NumericTools.parseInt(newURL.substring(index+4),-1);
        if (id!=-1)
        {
          downloadAct(id);
        }
      }
      if ((lookForMultiplePages) && (!foundBirthRefs))
      {
        if (line.contains("&amp;xord=D&amp;pg="))
        {
          //<a href="/releves/tab_naiss.php?args=Billy-Berclau,QUEVA&amp;xord=D&amp;pg=2">2</a>
          String partialURL=TextTools.findBetween(line,"<a href=\"","\">");
          System.out.println(partialURL);
          String newURL=Constants.SITE+partialURL;
          newURL=newURL.replace("&amp;","&");
          handleBirthPage(newURL,out,false);
        }
      }
    }
  }

  private void downloadAct(int id)
  {
    File f=new File(Constants.OUT_DIR,String.valueOf(id)+".html");
    f.getParentFile().mkdirs();
    String url=Constants.ROOT_SITE_N+id;
    _d.downloadPage(url,f);
  }

  private void parseBirthActs()
  {
    List<BirthAct> acts=new ArrayList<BirthAct>();
    FilesFinder finder=new FilesFinder();
    FileFilter htmlFiles=new ExtensionPredicate("html");
    List<File> files=finder.find(FilesFinder.ABSOLUTE_MODE,Constants.OUT_DIR,htmlFiles,true);
    for(File f : files)
    {
      BirthAct act=readFile(f);
      if (act!=null)
      {
        acts.add(act);
      }
    }
    BirthActsIO.writeActs(Constants.ACTS_FILE,acts);
  }

  public static void main(String[] args)
  {
    new Main().parseBirthActs();
  }
}
