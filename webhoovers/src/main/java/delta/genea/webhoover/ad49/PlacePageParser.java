package delta.genea.webhoover.ad49;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import delta.common.utils.text.TextUtils;
import delta.downloads.Downloader;
import delta.genea.webhoover.ActsPackage;
import delta.genea.webhoover.HtmlTools;

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
  private static final String START_TAG="<";
  private static final String END_TAG=">";
  private static final String SLASH="/";

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
	 * @throws Exception If a problem occurs.
	 */
	public List<ActsPackage> parse() throws Exception
	{
    String phpSID=_session.getPHPSessionID();
    Downloader downloader=_session.getDownloader();
    String url=PLACE_PAGE_URL+phpSID+"&id="+_placeID;
    File tmpDir=_session.getTmpDir();
    File placePageFile=new File(tmpDir,"placePage.html");
    downloader.downloadToFile(url, placePageFile);
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
    String row;
    while(true)
    {
      int index=tableContents.indexOf(TR_START);
      if (index==-1) break;
      tableContents=tableContents.substring(index+TR_START.length());
      index=tableContents.indexOf(TR_END);
      if (index==-1) break;
      row=tableContents.substring(0,index);
      tableContents=tableContents.substring(index+TR_END.length());
      ActsPackage packageInfo=handleRow(row);
      result.add(packageInfo);
    }
    placePageFile.delete();
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
    List<String> items=splitAsTags("td",cells);
    ActsPackage actsPackage=new ActsPackage();
    actsPackage._id=id;
    actsPackage._placeName=items.get(0);
    actsPackage._church=items.get(1);
    actsPackage._actType=items.get(2);
    actsPackage._period=items.get(3);
    actsPackage._source=items.get(4);
    actsPackage._comments=items.get(5);
    return actsPackage;
	}

	private List<String> splitAsTags(String tag, String contents)
	{
	  String startTag=START_TAG+tag;
	  String endTag=START_TAG+SLASH+tag+END_TAG;
	  List<String> tags=new ArrayList<String>();
	  int index;
	  String contentsLeft=contents;
	  String item;
    while(true)
    {
      index=contentsLeft.indexOf(startTag);
      if (index==-1) break;
      contentsLeft=contentsLeft.substring(index+startTag.length());
      index=contentsLeft.indexOf(END_TAG);
      if (index==-1) break;
      contentsLeft=contentsLeft.substring(index+END_TAG.length());
      index=contentsLeft.indexOf(endTag);
      if (index==-1) break;
      item=contentsLeft.substring(0,index);
      contentsLeft=contentsLeft.substring(index+endTag.length());
      item=HtmlTools.htmlToString(item);
      tags.add(item);
    }
    return tags;
	}
}
