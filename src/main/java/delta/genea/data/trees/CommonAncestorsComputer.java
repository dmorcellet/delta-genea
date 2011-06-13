package delta.genea.data.trees;

import java.util.HashSet;
import java.util.Set;

import delta.genea.data.Couple;

/**
 * Computes commons ancestors between ancestors trees.
 * @author DAM
 */
public class CommonAncestorsComputer
{
  /**
   * Compute the set of couples common to two trees.
   * @param t1 First tree.
   * @param t2 Second tree.
   * @return A possibly empty set of couples. 
   */
  public Set<Couple> compute(AncestorsTree t1, AncestorsTree t2)
  {
    Set<Couple> ret=new HashSet<Couple>();
    // get the couples of the first tree
    Set<Couple> c1=t1.getCouplesSet();
    // get the couples of the first tree
    Set<Couple> c2=t2.getCouplesSet();
    // retain all couples of c1 that are also in c2
    c1.retainAll(c2);
    return ret;
  }
}
