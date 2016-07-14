package com.apnavaidya;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by neeraj on 29/2/16.
 */
public class ProblemList {

    static ArrayList<String> problemList=null;
    static Map<Integer,String> fragmentMap=new HashMap<>();
    void setProblemList(ArrayList<String> problemList)
    {
        this.problemList=problemList;
    }
    ArrayList<String> getProblemList()
    {
     return problemList;
    }

    void dataCheck(int n,String fragmentName)
    {
       fragmentMap.put(n,fragmentName);
    }

    Map<Integer,String> getdataCheck()
    {
      return fragmentMap;
    }



}
