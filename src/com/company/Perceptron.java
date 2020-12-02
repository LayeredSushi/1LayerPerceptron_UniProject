package com.company;

import java.util.ArrayList;
import java.util.TreeMap;

public class Perceptron {

    String language,datapath;
    double thetta,rate;
    ArrayList<Double> weight;
    int datasize;
    TreeMap<String,ArrayList<TreeMap<String,Double>>> mapOccurance;

    public Perceptron(String language, String datapath, double thetta, double rate,int datasize) {
        this.language = language;
        this.datapath = datapath;
        this.thetta = thetta;
        this.rate = rate;
        this.datasize=datasize;
        perceptReader();
    }

    public void perceptReader(){
        weight=weightinit(datasize);
        //Second parameter Test is for EXCLUDING test files
        TrainFileReader tfr = new TrainFileReader(datapath,"Test");
        mapOccurance = tfr.reader();

    }

    public void mainP() {

        mapOccurance.entrySet().stream().forEach(e->{
                int desired=0;
                if(language.equals(e.getKey()))
                    desired=1;

                for(TreeMap<String,Double> n: e.getValue()){
                    delta(n, desired);
                }
        });

    }

    private void delta(TreeMap<String,Double> occurance,int desired){

        ArrayList<Double> tempdata =new ArrayList<>(occurance.values());
        int y=perceptron(new ArrayList<>(occurance.values()));
        ArrayList<Double> tempweight=weight;
        ArrayList<Double> newWeight = new ArrayList<>();

        tempweight.add(thetta);
        tempdata.add(-1.0);


        int i=0;
        for(Double e: tempweight)
        {
            newWeight.add(e+(desired-y)*tempdata.get(i)*rate);
            i++;
        }

        thetta=thetta+rate*(y-desired);
        newWeight.remove(datasize-1);

        weight.clear();
        for(Double e:newWeight)
            weight.add(e);

    }

    public int perceptron(ArrayList<Double> temp)
    {
        double result=0;

        int i=0;
        for(Double e:temp)
        {
            result+=(e*weight.get(i));
            i++;
        }

        if(result>=thetta)
            return 1;
        return 0;
    }


    private static ArrayList<Double> weightinit(int datasize)
    {
        ArrayList<Double> weight = new ArrayList<>();
        for(int i=0;i<datasize;i++)
            weight.add(1.0);
        return weight;
    }

}
