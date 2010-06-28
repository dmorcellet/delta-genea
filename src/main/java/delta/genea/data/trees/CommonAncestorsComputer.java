package delta.genea.data.trees;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import delta.common.utils.collections.BinaryTreeNode;
import delta.genea.data.Couple;
import delta.genea.data.Person;

public class CommonAncestorsComputer
{
  private AncestorsTree _t1;
  private AncestorsTree _t2;
  private Map<Couple,List<Long>> _couplesToSosas;

  public CommonAncestorsComputer(AncestorsTree t1, AncestorsTree t2)
  {
    _t1=t1;
    _t2=t2;
    _couplesToSosas=null;
  }

  public Set<Couple> compute()
  {
    Set<Couple> ret=new HashSet<Couple>();
    // Build Couples->sosa map for first tree
    _couplesToSosas=_t1.buildCoupleToSOSAMap(_t1);
    // Find common couples
    findCommons(_t2.getRootNode(),ret);
    return ret;
  }

  private void findCommons(BinaryTreeNode<Person> node, Set<Couple> ret)
  {
    Person father=node.getLeftData();
    Person mother=node.getRightData();

    if ((father==null)&&(mother==null)) return;

    Couple c=new Couple(father,mother);
    List<Long> list=_couplesToSosas.get(c);

    if (list!=null)
    {
      ret.add(c);
    }
    else
    {
      if (father!=null)
        findCommons(node.getLeftNode(),ret);
      if (mother!=null)
        findCommons(node.getRightNode(),ret);
    }
  }
}
