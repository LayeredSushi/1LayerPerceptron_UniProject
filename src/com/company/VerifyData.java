package com.company;

import java.util.HashMap;
import java.util.Map;

public class VerifyData {
    static double tp=0,fp=0,fn=0;

    public static void verify(HashMap<Perceptron, Integer> resultset, String actualLang){

        System.out.println("-----------------------------");
        for(Map.Entry<Perceptron,Integer> e: resultset.entrySet()){
            System.out.println(e.getKey().language+" "+actualLang+" "+e.getValue());
            if(e.getKey().language.equals(actualLang) && e.getValue()==1)
                tp++;
            if(!e.getKey().language.equals(actualLang) && e.getValue()==1)
                fp++;
            if(e.getKey().language.equals(actualLang) && e.getValue()==0)
                fn++;
        }
    }
}
