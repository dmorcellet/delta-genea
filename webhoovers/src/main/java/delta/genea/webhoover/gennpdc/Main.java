package delta.genea.webhoover.gennpdc;

import java.io.File;
import java.util.List;

import delta.downloads.DownloadException;
import delta.genea.webhoover.expoactes.BirthAct;
import delta.genea.webhoover.expoactes.BirthActsIO;
import delta.genea.webhoover.expoactes.BirthPagesManager;
import delta.genea.webhoover.expoactes.ExpoActeSession;

/**
 * Gen N-PdC webhoover.
 * @author DAM
 */
public class Main
{
  private static final File OUTPUT_DIR=new File("/home/dm/tmp/gennpdc");
  /**
   * Path of the output file.
   */
  public static final File ACTS_FILE=new File("/home/dm/bb.txt");

  /**
   * Constructor.
   */
  private Main()
  {
  }

  private void doIt() throws DownloadException
  {
    String placeName="Billy-Berclau";
    downloadActs(placeName);
  }

  private void downloadActs(String placeName) throws DownloadException
  {
    ExpoActeSession session=new ExpoActeSession("gennpdc","http://www.gennpdc.net",OUTPUT_DIR);
    BirthPagesManager birthManager=new BirthPagesManager(session);
    List<BirthAct> acts=birthManager.downloadBirthActs(placeName);
    BirthActsIO.writeActs(ACTS_FILE,acts);
    session.terminate();
    System.out.println(session.getActsCounter());
  }

  /**
   * Main method for this tool.
   * @param args Not used.
   * @throws Exception
   */
  public static void main(String[] args) throws Exception
  {
    new Main().doIt();
  }
}
