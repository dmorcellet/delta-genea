package delta.genea.webhoover.agena;

import java.io.File;
import java.util.HashMap;

import delta.downloads.Downloader;
import delta.genea.webhoover.expoactes.ExpoActeSession;
import delta.genea.webhoover.utils.TmpFilesManager;

/**
 * Agena downloads.
 * @author DAM
 */
public class Main
{
  private ExpoActeSession _session;
  private static final File OUTPUT_DIR=new File("/home/dm/tmp/agena");

  // private static final File ACTS_FILE=new File("/home/dm/agena.txt");

  /**
   * Constructor.
   */
  private Main()
  {
    _session=new ExpoActeSession("agena",Constants.SITE_ROOT,OUTPUT_DIR);
  }

  private void doIt()
  {
    TmpFilesManager tmpFilesManager=_session.getTmpFilesManager();
    File mainPage=tmpFilesManager.newTmpFile("main.html");
    HashMap<String,String> params=new HashMap<String,String>();
    params.put("login","AGA1766MORSte");
    params.put("passwd","");
    params.put("codedpass","40af47d6b115e0b8b9494fa0ced4b51004def483");
    params.put("iscoded","Y");
    Downloader d=_session.getDownloader();
    d.downloadPageAsPost(Constants.TEST_PAGE,mainPage,params);
  }

  /**
   * Main method for this tool.
   * @param args Not used.
   */
  public static void main(String[] args)
  {
    new Main().doIt();
  }
}
