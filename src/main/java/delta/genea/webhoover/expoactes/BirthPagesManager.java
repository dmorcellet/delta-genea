package delta.genea.webhoover.expoactes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import delta.common.utils.NumericTools;
import delta.common.utils.text.EncodingNames;
import delta.common.utils.text.TextTools;
import delta.common.utils.text.TextUtils;
import delta.downloads.Downloader;
import delta.genea.webhoover.utils.TmpFilesManager;

/**
 * @author DAM
 */
public class BirthPagesManager
{
  private static Log logger=LogFactory.getLog("BirthPagesManager");
  private static final String BIRTHS_LINE_START="<td>&nbsp;<a href=\"/releves/tab_naiss.php?args=";
  private static final String BIRTH_ACT_REF_LINE_SEED="<a href=\"/releves/acte_naiss.php?xid=";
  private static final String PARTIAL_BIRTH_URL="/releves/acte_naiss.php?xid=";
  private static final String PARTIAL_BIRTH_ACTS_FOR_PLACE_URL="/releves/tab_naiss.php?args=";

  private ExpoActeSession _session;
  private BirthActPageParser _parser;
  private List<BirthAct> _acts;

  public BirthPagesManager(ExpoActeSession session)
  {
    _session=session;
    _parser=new BirthActPageParser();
  }

  public List<BirthAct> downloadBirthActs(String placeName) throws Exception
  {
    String siteRoot=_session.getSiteRoot();
    String url=siteRoot+PARTIAL_BIRTH_ACTS_FOR_PLACE_URL+placeName;
    _acts=new ArrayList<BirthAct>();
    handleBirthPage(url,true);
    List<BirthAct> ret=_acts;
    _acts=null;
    return ret;
  }
  
  private void handleBirthPage(String url, boolean lookForMultiplePages) throws Exception
  {
    TmpFilesManager tmpFilesManager=_session.getTmpFilesManager();
    File tmpFile=tmpFilesManager.newTmpFile("birthIndexPage.html");
    Downloader downloader=_session.getDownloader();
    downloader.downloadToFile(url,tmpFile);
    String siteRoot=_session.getSiteRoot();
    List<String> lines=TextUtils.readAsLines(tmpFile,EncodingNames.ISO8859_1);
    boolean foundBirthRefs=false;
    for(String line : lines)
    {
      if (line.startsWith(BIRTHS_LINE_START))
      {
        String partialURL=TextTools.findBetween(line,"<a href=\"","\">");
        //System.out.println(partialURL);
        String newURL=siteRoot+partialURL;
        handleBirthPage(newURL,lookForMultiplePages);
      }
      if (line.contains(BIRTH_ACT_REF_LINE_SEED))
      {
        String partialURL=TextTools.findBetween(line,"<a href=\"","\">");
        String newURL=siteRoot+partialURL;
        //System.out.println(newURL);
        foundBirthRefs=true;
        int index=newURL.indexOf("?xid=");
        String idStr=newURL.substring(index+1);
        int id=-1,ct=-1;
        String[] ids=idStr.split("&");
        if ((ids!=null) && (ids.length>0))
        {
          for(int i=0;i<ids.length;i++)
          {
            if (ids[i].startsWith("xid="))
            {
              id=NumericTools.parseInt(ids[i].substring(4),-1);
            }
            else if (ids[i].startsWith("xct="))
            {
              ct=NumericTools.parseInt(ids[i].substring(4),-1);
            } 
          }
        }
        if ((id!=-1) && (ct!=-1))
        {
          handleActPage(id,ct);
        }
      }
      if ((lookForMultiplePages) && (!foundBirthRefs))
      {
        if (line.contains("&amp;xord=D&amp;pg="))
        {
          String partialURL=TextTools.findBetween(line,"<a href=\"","\">");
          //System.out.println(partialURL);
          String newURL=siteRoot+partialURL;
          newURL=newURL.replace("&amp;","&");
          handleBirthPage(newURL,false);
        }
      }
    }
  }

  private void handleActPage(int xid, int xct) throws Exception
  {
    // Download
    TmpFilesManager tmpFilesManager=_session.getTmpFilesManager();
    File f=tmpFilesManager.newTmpFile("birthAct.html");
    f.getParentFile().mkdirs();
    String siteRoot=_session.getSiteRoot();
    String url=siteRoot+PARTIAL_BIRTH_URL+xid+"&xct="+xct;
    Downloader downloader=_session.getDownloader();
    downloader.downloadToFile(url,f);
    _session.incrementActsCounter();
    // Parsing
    try
    {
      BirthAct act=_parser.readFile(f);
      if (act!=null)
      {
        _acts.add(act);
        System.out.println(act);
      }
    }
    catch(Exception e)
    {
      logger.error("Error when parsing birth file ["+f+"]",e);
    }
  }
}
