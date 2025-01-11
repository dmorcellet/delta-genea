package delta.genea.webhoover.expoactes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.utils.NumericTools;
import delta.common.utils.text.EncodingNames;
import delta.common.utils.text.TextTools;
import delta.common.utils.text.TextUtils;
import delta.downloads.DownloadException;
import delta.downloads.Downloader;
import delta.genea.webhoover.utils.TmpFilesManager;

/**
 * Downloads birth acts for a single place.
 * @author DAM
 */
public class BirthPagesManager
{
  private static final String A_HREF_SEED="<a href=\"";

  private static final Logger LOGGER=LoggerFactory.getLogger(BirthPagesManager.class);

  private static final String BIRTHS_LINE_START="<td>&nbsp;<a href=\"/releves/tab_naiss.php?args=";
  private static final String BIRTH_ACT_REF_LINE_SEED="<a href=\"/releves/acte_naiss.php?xid=";
  private static final String PARTIAL_BIRTH_URL="/releves/acte_naiss.php?xid=";
  private static final String PARTIAL_BIRTH_ACTS_FOR_PLACE_URL="/releves/tab_naiss.php?args=";

  private ExpoActeSession _session;
  private BirthActPageParser _parser;
  private List<BirthAct> _acts;

  /**
   * Constructor.
   * @param session Download session.
   */
  public BirthPagesManager(ExpoActeSession session)
  {
    _session=session;
    _parser=new BirthActPageParser();
  }

  /**
   * Download all birth acts for a place.
   * @param placeName Place name.
   * @return A list of birth acts.
   * @throws DownloadException If a problem occurs.
   */
  public List<BirthAct> downloadBirthActs(String placeName) throws DownloadException
  {
    String siteRoot=_session.getSiteRoot();
    String url=siteRoot+PARTIAL_BIRTH_ACTS_FOR_PLACE_URL+placeName;
    _acts=new ArrayList<BirthAct>();
    handleBirthPage(url,true);
    List<BirthAct> ret=_acts;
    _acts=null;
    return ret;
  }

  private void handleBirthPage(String url, boolean lookForMultiplePages) throws DownloadException
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
        String partialURL=TextTools.findBetween(line,A_HREF_SEED,"\">");
        LOGGER.debug("Partial URL: {}",partialURL);
        String newURL=siteRoot+partialURL;
        handleBirthPage(newURL,lookForMultiplePages);
      }
      if (line.contains(BIRTH_ACT_REF_LINE_SEED))
      {
        String partialURL=TextTools.findBetween(line,A_HREF_SEED,"\">");
        String newURL=siteRoot+partialURL;
        LOGGER.debug("New URL: {}",newURL);
        foundBirthRefs=true;
        int index=newURL.indexOf("?xid=");
        String idStr=newURL.substring(index+1);
        int id=-1;
        int ct=-1;
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
          String partialURL=TextTools.findBetween(line,A_HREF_SEED,"\">");
          LOGGER.debug("Partial URL: {}",partialURL);
          String newURL=siteRoot+partialURL;
          newURL=newURL.replace("&amp;","&");
          handleBirthPage(newURL,false);
        }
      }
    }
  }

  private void handleActPage(int xid, int xct) throws DownloadException
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
        LOGGER.info("{}",act);
      }
    }
    catch(Exception e)
    {
      LOGGER.error("Error when parsing birth file ["+f+"]",e);
    }
  }
}
