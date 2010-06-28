package delta.genea.data.trees;

import java.util.HashMap;

import delta.genea.data.Person;
import delta.genea.data.sources.GeneaDataSource;

/**
 * Registry for all ancestors trees of a data source.
 * @author DAM
 */
public class AncestorsTreesRegistry
{
  private GeneaDataSource _dataSource;
  /**
   * Storage map for known ancestors trees.
   */
  private HashMap<Long, AncestorsTree> _knownTrees;

  /**
   * Private constructor.
   * The sole constructor of this class is private to
   * ensure that this class has at most a single instance.
   */
  public AncestorsTreesRegistry(GeneaDataSource dataSource)
  {
    _dataSource=dataSource;
    _knownTrees=new HashMap<Long, AncestorsTree>();
  }

  /**
   * Get an ancestors tree (build it if necessary).
   * @param key Root person key.
   * @return A tree or <code>null</code>.
   */
  public AncestorsTree getTree(long key)
  {
    return getTree(key,true,false);
  }

  /**
   * Get an ancestors tree (build it if necessary).
   * @param key Root person key.
   * @param builtItIfNeeded Built the tree it it is not loaded yet.
   * @param tolerantMode Tree building mode.
   * @return A tree or <code>null</code>.
   */
  public AncestorsTree getTree(long key, boolean builtItIfNeeded, boolean tolerantMode)
  {
    AncestorsTree ret=null;
    if (key!=0)
    {
      Long tmp=Long.valueOf(key);
      ret=_knownTrees.get(tmp);
      if ((ret==null) && (builtItIfNeeded))
      {
        Person root=_dataSource.getPersonDataSource().load(key);
        if (root!=null)
        {
          AncestorsTree at=new AncestorsTree(root,AncestorsTree.MAX_DEPTH);
          boolean ok=at.build(tolerantMode);
          if (ok)
          {
            ret=at;
            _knownTrees.put(tmp,ret);
          }
        }
      }
    }
    return ret;
  }
}
