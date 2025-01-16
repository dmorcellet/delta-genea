package delta.genea.webhoover.ad49;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import delta.common.utils.text.TextTools;
import delta.common.utils.text.TextUtils;
import delta.downloads.DownloadException;
import delta.downloads.Downloader;
import delta.genea.webhoover.ActsPackage;
import delta.genea.webhoover.utils.FileUtils;
import delta.genea.webhoover.utils.ParsingUtils;

/**
 * Parser for a place index page.
 * @author DAM
 */
public class PlacePageParser
{
  private static final String PLACE_PAGE_URL=Constants.ROOT_SITE+"/cg49work/registre_recherche.php?PHPSID=";
  private static final String TABLE_START="<table >";
  private static final String TABLE_END="</table>";
  private static final String TR_START="<tr";
  private static final String TR_END="</tr>";
  private static final String TD_START="<td ";
  private static final String BEFORE_ID="registre_prepare.php?id=";
  private static final String AFTER_ID="&PHPSID";

  private AD49Session _session;
  private int _placeID;

  /**
   * Constructor.
   * @param session Session to use.
   * @param placeID Identifier of the place to use.
   */
  public PlacePageParser(AD49Session session, int placeID)
  {
    _session=session;
    _placeID=placeID;
  }

  /**
   * Extract definitions of acts packages for this place.
   * @return A list of acts packages.
   * @throws DownloadException If a problem occurs.
   */
  public List<ActsPackage> parse() throws DownloadException
  {
    String phpSID=_session.getPHPSessionID();
    Downloader downloader=_session.getDownloader();
    String url=PLACE_PAGE_URL+phpSID+"&id="+_placeID;
    File tmpDir=_session.getTmpDir();
    File placePageFile=new File(tmpDir,"placePage.html");
    downloader.downloadToFile(url,placePageFile);
    List<ActsPackage> result=new ArrayList<ActsPackage>();
    String tableContents="";
    List<String> lines=TextUtils.readAsLines(placePageFile);
    for(Iterator<String> it=lines.iterator();it.hasNext();)
    {
      String line=it.next();
      int index=line.indexOf(TABLE_START);
      if (index!=-1)
      {
        String tmp=line.substring(index+TABLE_START.length());
        index=tmp.indexOf(TABLE_START);
        tmp=tmp.substring(index+TABLE_START.length());
        index=tmp.indexOf(TABLE_END);
        tableContents=tmp.substring(0,index);
        break;
      }
    }
    List<String> rows=TextTools.findAllBetween(tableContents,TR_START,TR_END);
    for(String row : rows)
    {
      ActsPackage packageInfo=handleRow(row);
      if (packageInfo!=null)
      {
        result.add(packageInfo);
      }
    }
    FileUtils.deleteFile(placePageFile);
    return result;
  }

  private ActsPackage handleRow(String row)
  {
    int index;
    index=row.indexOf(TD_START);
    if (index==-1) return null;
    String tmp=row.substring(0,index);
    String cells=row.substring(index);
    index=tmp.indexOf(BEFORE_ID);
    if (index==-1) return null;
    tmp=tmp.substring(index+BEFORE_ID.length());
    index=tmp.indexOf(AFTER_ID);
    if (index==-1) return null;
    String id=tmp.substring(0,index);
    List<String> items=ParsingUtils.splitAsTags("td",cells);
    ActsPackage actsPackage=new ActsPackage();
    actsPackage.setId(id);
    actsPackage.setPlaceName(items.get(0));
    actsPackage.setChurch(items.get(1));
    actsPackage.setActType(items.get(2));
    actsPackage.setPeriod(items.get(3));
    actsPackage.setSource(items.get(4));
    actsPackage.setComments(items.get(5));
    return actsPackage;
  }
}
