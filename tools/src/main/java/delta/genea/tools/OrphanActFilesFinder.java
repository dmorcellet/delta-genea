package delta.genea.tools;

import java.io.File;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.utils.files.iterator.AbstractFileIteratorCallback;
import delta.common.utils.files.iterator.FileIterator;
import delta.common.utils.files.iterator.FileIteratorCallback;
import delta.genea.data.Act;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.misc.GeneaCfg;

/**
 * Finds missing acts in a ancestors tree.
 * <br>
 * Given a database name and a root person key, it iterates on this root person's
 * ancestors and builds a list of all the missing acts (i.e. birth/wedding/wedding contract/death
 * date without a matching image file.
 * @author DAM
 */
public class OrphanActFilesFinder
{
  private static final Logger LOGGER=LoggerFactory.getLogger(OrphanActFilesFinder.class);

  private PrintStream _out;
  private final String _dbName;

  /**
   * Constructor.
   * @param out Output.
   * @param dbName Database identifier.
   */
  public OrphanActFilesFinder(PrintStream out, String dbName)
  {
    _out=out;
    _dbName=dbName;
  }

  /**
   * Do the job.
   */
  public void doIt()
  {
    try
    {
      GeneaDataSource dataSource=GeneaDataSource.getInstance(_dbName);

      File rootDir=GeneaCfg.getInstance().getActsRootPath();
      List<Act> acts=dataSource.getManager(Act.class).loadAll();
      Set<File> referencedFiles=new HashSet<File>();
      for(Act act : acts)
      {
        int nbFiles=act.getNbFiles();
        for(int i=0;i<nbFiles;i++)
        {
          File actFile=act.getActFile(i);
          if (actFile!=null)
          {
            referencedFiles.add(actFile);
          }
        }
      }
      Set<File> foundFiles=new HashSet<File>();
      FileIteratorCallback c=new AbstractFileIteratorCallback()
      {
        @Override
        public void handleFile(File absolute, File relative)
        {
          foundFiles.add(absolute);
        }
      };
      FileIterator it=new FileIterator(rootDir,true,c);
      it.run();
      foundFiles.removeAll(referencedFiles);
      for(File f : foundFiles)
      {
        _out.println(f);
      }
    }
    catch (Exception e)
    {
      LOGGER.error("Error!",e);
    }
  }

  /**
   * Main method for this tool.
   * @param args 
   */
  public static void main(String[] args)
  {
    if (args.length==1)
    {
      String dbName=args[0];
      if (dbName.length()>0)
      {
        new OrphanActFilesFinder(System.out,dbName).doIt(); // NOSONAR
      }
    }
  }
}
