package delta.genea.webhoover.gennpdc;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private static final Logger LOGGER=LoggerFactory.getLogger(Main.class);

  /**
   * Constructor.
   */
  private Main()
  {
  }

  private void doIt(File toFile) throws DownloadException
  {
    String placeName="Billy-Berclau";
    downloadActs(placeName,toFile);
  }

  private void downloadActs(String placeName, File toFile) throws DownloadException
  {
    ExpoActeSession session=new ExpoActeSession("gennpdc","http://www.gennpdc.net");
    BirthPagesManager birthManager=new BirthPagesManager(session);
    List<BirthAct> acts=birthManager.downloadBirthActs(placeName);
    BirthActsIO.writeActs(toFile,acts);
    session.terminate();
    LOGGER.info("Loaded {} acts",Integer.valueOf(session.getActsCounter()));
  }

  /**
   * Main method for this tool.
   * @param args Not used.
   * @throws Exception
   */
  public static void main(String[] args) throws Exception
  {
    File toFile=new File(args[0]);
    new Main().doIt(toFile);
  }
}
