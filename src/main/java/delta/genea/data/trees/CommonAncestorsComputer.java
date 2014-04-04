package delta.genea.data.trees;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import delta.common.utils.collections.BinaryTreeNode;
import delta.genea.data.Couple;
import delta.genea.data.Person;

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
    // Build Couples->sosa map for first tree
    Map<Couple,List<Long>> mapCouplesT1=t1.buildCoupleToSOSAMap();
    // Find common couples
    findCommons(t2.getRootNode(),ret,mapCouplesT1);
    return ret;
  }

  private void findCommons(BinaryTreeNode<Person> node, Set<Couple> ret, Map<Couple,List<Long>> mapCouplesT1)
  {
    Person father=node.getLeftData();
    Person mother=node.getRightData();

    if ((father==null)&&(mother==null)) return;

    Couple c=new Couple(father,mother);
    List<Long> list=mapCouplesT1.get(c);

    if (list!=null)
    {
      ret.add(c);
    }
    else
    {
      if (father!=null)
        findCommons(node.getLeftNode(),ret,mapCouplesT1);
      if (mother!=null)
        findCommons(node.getRightNode(),ret,mapCouplesT1);
    }
  }
}
