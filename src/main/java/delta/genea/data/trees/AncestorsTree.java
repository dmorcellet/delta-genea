package delta.genea.data.trees;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import delta.common.utils.collections.BinaryTreeNode;
import delta.genea.data.Couple;
import delta.genea.data.Person;

public class AncestorsTree
{
  public static final int MAX_DEPTH=100;
  private static final String SPECIAL_KEY="0";
  private Person _rootPerson;
  private BinaryTreeNode<Person> _tree;
  private int _depth;

  public AncestorsTree(Person rootPerson, int depth)
  {
    _depth=depth;
    _rootPerson=rootPerson;
    _tree=new BinaryTreeNode<Person>(null, rootPerson);
  }

  public BinaryTreeNode<Person> getRootNode()
  {
    return _tree;
  }

  public boolean build()
  {
    return build(false);
  }

  public boolean build(boolean tolerantMode)
  {
    return build(_rootPerson, _tree, _depth, tolerantMode);
  }

  private boolean build(Person person, BinaryTreeNode<Person> node, int depth, boolean tolerantMode)
  {
    boolean ret=true;

    if(depth>0)
    {
      if(person.getFatherProxy()!=null)
      {
        Person father=person.getFather();
        node.setLeftData(father);
        if (father!=null)
        {
          ret=build(father, node.getLeftNode(), depth-1,tolerantMode);
        }
        else
        {
          ret=tolerantMode;
        }
      }
      if((ret)&&(person.getMotherProxy()!=null))
      {
        Person mother=person.getMother();
        node.setRightData(mother);
        if (mother!=null)
        {
          ret=build(mother, node.getRightNode(), depth-1,tolerantMode);
        }
        else
        {
          ret=tolerantMode;
        }
      }
    }
    return ret;
  }

  public Person getSosa(long sosa)
  {
    Person ret=null;
    BinaryTreeNode<Person> node=getNodeForSosa(sosa);
    if(node!=null)
    {
      ret=node.getData();
    }
    return ret;
  }

  public long getNumberOfAncestors()
  {
    return _tree.getNumberOfDescendants();
  }

  public List<Long> getSosas(long personKey)
  {
    List<Long> ret=new ArrayList<Long>();
    getSosasForPerson(1,personKey,_tree,ret);
    return ret;
  }

  private void getSosasForPerson(long sosa, long personKey, BinaryTreeNode<Person> node, List<Long> result)
  {
    Person p=node.getData();
    if (p.getPrimaryKey()==personKey)
    {
      result.add(Long.valueOf(sosa));
    }
    else
    {
      BinaryTreeNode<Person> next=node.getLeftNode();
      if (next!=null)
      {
        getSosasForPerson(2*sosa,personKey,next,result);
      }
      next=node.getRightNode();
      if (next!=null)
      {
        getSosasForPerson(2*sosa+1,personKey,next,result);
      }
    }
  }

  private BinaryTreeNode<Person> getNodeForSosa(long sosa)
  {
    long path=0;
    int steps=0;
    long current=sosa;
    while(current!=1)
    {
      path<<=1;
      path+=(current&1);
      current/=2;
      steps++;
    }

    BinaryTreeNode<Person> node=_tree;
    long mask=1;
    for(int i=0;i<steps;i++)
    {
      if((path&mask)!=0)
      {
        node=node.getRightNode();
      }
      else
      {
        node=node.getLeftNode();
      }
      if(node==null)
      {
        break;
      }
      mask<<=1;
    }

    return node;
  }

  public HashMap<String, Implex> getImplexes()
  {
    HashMap<String, Implex> implexes=new HashMap<String, Implex>();
    HashMap<String, ArrayList<Long>> keysToSosas=new HashMap<String, ArrayList<Long>>();
    getImplexes(_tree, 1, keysToSosas, implexes);
    return implexes;
  }

  private void getImplexes(BinaryTreeNode<Person> node, long sosa, HashMap<String, ArrayList<Long>> keysToSosas, HashMap<String, Implex> implexes)
  {
    if (node==null) return;
    boolean foundImplex=testImplex(node,sosa,true,true,keysToSosas,implexes);

    if (!foundImplex)
    {
      foundImplex=testImplex(node,sosa,true,false,keysToSosas,implexes);
      if (!foundImplex)
      {
        foundImplex=testImplex(node,sosa,false,true,keysToSosas,implexes);
        if (!foundImplex)
        {
          // Father and mother
          getImplexes(node.getLeftNode(), sosa*2, keysToSosas, implexes);
          getImplexes(node.getRightNode(), sosa*2+1, keysToSosas, implexes);
        }
        else
        {
          // Just father
          getImplexes(node.getLeftNode(), sosa*2, keysToSosas, implexes);
        }
      }
      else
      {
        // Just mother
        getImplexes(node.getRightNode(), sosa*2+1, keysToSosas, implexes);
      }
    }
  }

  private boolean testImplex(BinaryTreeNode<Person> node, long sosa,
      boolean useFather, boolean useMother,
      HashMap<String, ArrayList<Long>> keysToSosas,
      HashMap<String, Implex> implexes)
  {
    boolean foundImplex=false;
    Person father=node.getLeftData();
    Person mother=node.getRightData();
    if (!useFather) father=null;
    if (useFather && (father==null)) return false;
    if (!useMother) mother=null;
    if (useMother && (mother==null)) return false;

    if ((father==null)&&(mother==null)) return false;

    String fatherKey=SPECIAL_KEY;
    if (father!=null)
      fatherKey=String.valueOf(father.getPrimaryKey());
    String motherKey=SPECIAL_KEY;
    if (mother!=null)
      motherKey=String.valueOf(mother.getPrimaryKey());

    String globalKey=fatherKey+"/"+motherKey;

    ArrayList<Long> list=keysToSosas.get(globalKey);
    if(list==null)
    {
      list=new ArrayList<Long>();
      list.add(Long.valueOf(sosa));
      keysToSosas.put(globalKey, list);
    }
    else
    {
      foundImplex=true;
      Implex implex=implexes.get(globalKey);
      if(implex==null)
      {
        implex=new Implex(_rootPerson, father, mother);
        implexes.put(globalKey, implex);
        Long firstSosa=list.get(0);
        Filiation f=buildFiliation(1, firstSosa.longValue());
        implex.addFiliation(f);
      }
      Filiation f=buildFiliation(1, sosa);
      implex.addFiliation(f);
    }
    return foundImplex;
  }

  private Filiation buildFiliation(long baseSosa, long targetSosa)
  {
    Filiation ret=null;
    boolean possible=false;

    {
      long sosa=targetSosa;
      while(sosa>baseSosa)
      {
        sosa=sosa/2;
      }
      if(sosa==baseSosa)
      {
        possible=true;
      }
    }

    if(possible)
    {
      ret=new Filiation();

      long path=0;
      int steps=0;
      long sosa=targetSosa;
      while(sosa!=baseSosa)
      {
        path<<=1;
        path+=(sosa&1);
        sosa=sosa/2;
        steps++;
      }

      BinaryTreeNode<Person> node=getNodeForSosa(baseSosa);
      if(node!=null)
      {
        ret.addAncestor(node.getData());

        long mask=1;
        for(int i=0;i<steps;i++)
        {
          if((path&mask)!=0)
          {
            node=node.getRightNode();
          }
          else
          {
            node=node.getLeftNode();
          }
          if(node!=null)
          {
            ret.addAncestor(node.getData());
          }
          else
          {
            break;
          }
          mask<<=1;
        }
      }
    }

    return ret;
  }

  public void dump(PrintStream out)
  {
    if(out==null)
    {
      out=System.out;
    }
    dump(out, _tree, 0);
  }

  private void dump(PrintStream out, BinaryTreeNode<Person> node, int offset)
  {
    for(int i=0;i<offset;i++)
    {
      out.print(" ");
    }
    out.println(node);
    if(node.getLeftNode()!=null)
    {
      dump(out, node.getLeftNode(), offset+3);
    }
    if(node.getRightNode()!=null)
    {
      dump(out, node.getRightNode(), offset+3);
    }
  }

  public HashMap<Couple,List<Long>> buildCoupleToSOSAMap(AncestorsTree tree)
  {
    HashMap<Couple,List<Long>> ret=new HashMap<Couple,List<Long>>();
    buildKeyToSosaMap(tree.getRootNode(),1,ret);
    return ret;
  }

  private void buildKeyToSosaMap(BinaryTreeNode<Person> node, long sosa,
      HashMap<Couple,List<Long>> ret)
    {
      Person father=node.getLeftData();
      Person mother=node.getRightData();

      if ((father==null)&&(mother==null)) return;

      Couple c=new Couple(father,mother);
      List<Long> list=ret.get(c);
      if(list==null)
      {
        list=new ArrayList<Long>();
        ret.put(c, list);
      }
      list.add(Long.valueOf(sosa));
      if (father!=null)
        buildKeyToSosaMap(node.getLeftNode(),sosa*2,ret);
      if (mother!=null)
        buildKeyToSosaMap(node.getRightNode(),1+sosa*2,ret);
    }
}
