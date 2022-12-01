package io.github.dan2097.jnarinchi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.github.dan2097.jnainchi.InchiAtom;
import io.github.dan2097.jnainchi.InchiBond;
import io.github.dan2097.jnainchi.InchiBondType;

public class __ROSDALParser
{
    static String mErrorMessages[] = {
            "",
            // 1
            "Missing atom at the end of the chain",
            // 2
            "Arbitrary bond not allowed!",        
            // 3
            "Not allowed symbol for a bond specification",
            // 4
            "Unknown atom type",
            // 5
            "Atom type for is previously defined as a different type",
    };

    public boolean allowArbitraryBond = false;

    String rosdal;
    //RedConTable rct;
    RinchiInputComponent ric;
    List<Integer> errors = new ArrayList<Integer>();
    List<String> errorParams = new ArrayList<String>();    
    Map<Integer,Integer> indexAtomMap = new TreeMap<Integer,Integer>();
    int prevAt;
    int currBond;
    boolean flagNewAtom;
    
    public String getErrorMessages()
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < errors.size(); i++)
            sb.append(mErrorMessages[errors.get(i)]+ "  "
                    + errorParams.get(i).toString() + "\n");
        return(sb.toString());
    }
    
    public RinchiInputComponent parseROSDAL(String ros) {
        ric = new RinchiInputComponent();
        errors.clear();
        errorParams.clear();
        indexAtomMap.clear();
        
        String frags[] = ros.split(",");
        int res;
        for (int i = 0; i < frags.length; i++) {
            res = parseLinearChain(frags[i]);
            if (res!=0)
                return null;
        }    
        
        return ric;
    }
    
    int parseLinearChain(String chain)
    {
        //System.out.println("ROSDAL chain: " + chain);
        prevAt = -1;
        int pos = 0;
        int pos2 = atomEndPos(chain,pos);
        parseAtom(chain.substring(pos,pos2));
        
        pos = pos2;
        while (pos < chain.length())
        {
            currBond = parseBond(chain,pos);
            if (currBond < 0)
                return(currBond);
            
            pos++;
            if (pos>= chain.length())
            {    
                addError(1,chain);
                return(-1);
            }
            pos2 = atomEndPos(chain,pos);
            if (parseAtom(chain.substring(pos,pos2)) != 0)
                return(-1);
            pos = pos2;
        }
        
        return(0);
    }
    
    int parseAtom(String atom)
    {
        //System.out.print("atom --> "+atom + ":  ");
        flagNewAtom = false;
        int rosdalAtIndex = 0;
        int atType = 0;
        int atNum;
        int pos;
        String atSymbol = "";
        
        for(pos = 0; pos<atom.length(); pos++)
        {    
            if (Character.isDigit(atom.charAt(pos)))
                rosdalAtIndex = rosdalAtIndex*10 + Character.getNumericValue(atom.charAt(pos));
            else
                break;
        }
        
        if (pos >= atom.length()) {
            atType = 6;
            atSymbol = "C";
        }    
        else
        {    
            int pos2 = pos+1;
            while (pos2<atom.length())
            {        
                if (atom.charAt(pos2) == 'H')
                    break;
                pos2++;
            }
            atSymbol = atom.substring(pos,pos2);            
            atType = PerTable.getAtomNumber(atSymbol);
            
            if (atType == 0)
            {    
                addError(4,atSymbol);
                return(-1);
            }                
        }
        
        atNum = atomIndexToRCTAtomNumber(rosdalAtIndex);
        if (flagNewAtom) {
            //New InchiAtom is created            
            InchiAtom at = new InchiAtom(atSymbol);
            ric.addAtom(at);
        }    
        else
        {    
            if (!ric.getAtom(atNum).getElName().equals(atSymbol))
            {
                addError(5,atom);
                return(-1);
            }
        }
        
        
        if (prevAt != -1)
        {
            //Created new Bond
            InchiAtom at1 = ric.getAtom(prevAt);
            InchiAtom at2 = ric.getAtom(atNum);
            InchiBond bo = new InchiBond(at1, at2, InchiBondType.of((byte)currBond));
            ric.addBond(bo);
        }
        
        prevAt = atNum;
        
        //System.out.print(atIndex + "   " + atType + "    rct num = "+ atNum);        
        //System.out.print("\n");
        return(0);
    }
    
    int atomIndexToRCTAtomNumber(int rosdalIndex)
    {
        Integer o = indexAtomMap.get(new Integer(rosdalIndex));
        if (o == null)
        {
            int newAtNum = ric.getAtoms().size();
            indexAtomMap.put(new Integer(rosdalIndex), new Integer(newAtNum));
            flagNewAtom = true;
            return(newAtNum);
        }
        else            
            return(o);
    }
    
    int atomEndPos(String chain, int startPos)
    {
        int endPos;
        for(endPos=startPos; endPos<chain.length(); endPos++)
        {    
            if (!Character.isLetterOrDigit(chain.charAt(endPos)))
                break;
        }        
        return(endPos);
    }
    
    int parseBond(String chain, int pos)
    {    
        switch (chain.charAt(pos))
        {
            case '-':
                return(1);
            case '=':
                return(2);
            case '#':
                return(3);
            case '?':
            {    
                if (allowArbitraryBond)
                    return(0);
                else
                {    
                    addError(2,chain);
                    return(-1);
                }    
            }    
        }            
        
        addError(3,chain);
        return(-1);
    }
    
    void addError(int errorType, String params)
    {
        errors.add(new Integer(errorType));
        errorParams.add(params);
    }
    
    
    //Inner class
    public static class PerTable {
        
        public static String mElementSymbol[] =
        {
            "*",                                                 //0 any atom
            "H","He","Li","Be","B","C","N","O","F","Ne",         //1-10
            "Na","Mg","Al","Si","P","S","Cl","Ar","K","Ca",      //11-20
            "Sc","Ti","V","Cr","Mn","Fe","Co","Ni","Cu","Zn",    //21-30
            "Ga","Ge","As","Se","Br","Kr","Rb","Sr","Y","Zr",    //31-40
            "Nb","Mo","Tc","Ru","Rh","Pd","Ag","Cd","In","Sn",   //41-50
            "Sb","Te","I","Xe","Cs","Ba","La","Ce","Pr","Nd",    //51-60
            "Pm","Sm","Eu","Gd","Tb","Dy","Ho","Er","Tm","Yb",   //61-70
            "Lu","Hf","Ta","W","Re","Os","Ir","Pt","Au","Hg",    //71-80
            "Tl","Pb","Bi","Po","At","Rn","Fr","Ra","Ac","Th",   //81-90
            "Pa","U","Np","Pu","Am","Cm","Bk","Cf","Es","Fm",    //91-100
            "Md","No","Lr","Rf","Db","Sg","Bh","Hs","Mt","Uun",  //101-110
            "Uuu","Uub"                                             //111-120
        };
        
        
        public static double mAtomMass[] = {
            0.0D, 1.008D, 4.0030000000000001D, 6.9409999999999998D, 9.0120000000000005D, 10.81D, 12.01D, 14.01D, 16D, 19D,
            20.18D, 22.989999999999998D, 24.300000000000001D, 26.98D, 28.09D, 30.969999999999999D, 32.07D, 35.450000000000003D, 39.950000000000003D, 39.100000000000001D,
            40.079999999999998D, 44.960000000000001D, 47.880000000000003D, 50.939999999999998D, 52D, 54.939999999999998D, 55.850000000000001D, 58.93D, 58.689999999999998D, 63.549999999999997D,
            65.390000000000001D, 69.719999999999999D, 72.609999999999999D, 74.920000000000002D, 78.959999999999994D, 79.900000000000006D, 83.799999999999997D, 85.469999999999999D, 87.620000000000005D, 88.909999999999997D,
            91.219999999999999D, 92.909999999999997D, 95.939999999999998D, 98.909999999999997D, 101.09999999999999D, 102.90000000000001D, 106.40000000000001D, 107.90000000000001D, 112.40000000000001D, 114.8D,
            118.7D, 121.8D, 127.59999999999999D, 126.90000000000001D, 131.30000000000001D, 132.90000000000001D, 137.30000000000001D, 138.90000000000001D, 140.09999999999999D, 140.90000000000001D,
            144.19999999999999D, 144.90000000000001D, 150.40000000000001D, 152D, 157.19999999999999D, 158.90000000000001D, 162.5D, 164.90000000000001D, 167.30000000000001D, 168.90000000000001D,
            173D, 175D, 178.5D, 180.90000000000001D, 183.80000000000001D, 186.19999999999999D, 190.19999999999999D, 192.19999999999999D, 195.09999999999999D, 197D,
            200.59999999999999D, 204.40000000000001D, 207.19999999999999D, 209D, 210D, 210D, 222D, 223D, 226D, 227D,
            232D, 231D, 238D, 237D, 239.09999999999999D, 243.09999999999999D, 247.09999999999999D, 247.09999999999999D, 252.09999999999999D, 252.09999999999999D,
            257.10000000000002D, 256.10000000000002D, 259.10000000000002D, 260.10000000000002D
        };
        

        
        public static int getAtomNumber(String s)
        {
            for (int i=1; i < mElementSymbol.length; i++)
            {
                if (s.compareTo(mElementSymbol[i])==0)
                    return (i);
            }

            return(0);
        };

    }
}
