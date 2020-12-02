package com.company;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {



    public static void main(String[] args) {

        Path mainpath = Paths.get("resources");

        TreeMap<String,Perceptron> layer = new TreeMap<>();
        double thetta=10.0,rate=askrate();
        int datasize=26;

        File mainfile = mainpath.toFile();
        File[] directoryList = mainfile.listFiles();

        //init perceptron layer
        for(File e: directoryList)
            layer.put(e.getName(),new Perceptron(e.getName(),mainpath.toString(),thetta,rate,datasize));


        //training
        for(Map.Entry<String,Perceptron> e:layer.entrySet())
            for(int i=0;i<1000;i++)
               e.getValue().mainP();



        //Getting test data. Second parameter for reader is train cuz to EXCLUDE train files
        TrainFileReader tfr = new TrainFileReader(mainpath.toString(),"Train");
        TreeMap<String,ArrayList<TreeMap<String,Double>>> testdata = tfr.reader();

        //init for testing
        HashMap<Perceptron,Integer> resultset= new HashMap<>();

        //testing
        System.out.println("Manual or testset?");
        Scanner reader = new Scanner(System.in);
        if(reader.next().equals("Manual")){
            Perceptron testP=null;
            ArrayList<Double> tempdata = new ArrayList<>(manualInput().values());
            for(Map.Entry<String,Perceptron> e:layer.entrySet()){
                resultset.put(e.getValue(),e.getValue().perceptron(tempdata));
                testP=selector(resultset,tempdata);
            }

            if(testP == null)
                System.out.println("Something went wrong");
            else
                System.out.println("Output: "+testP.language);
        }

        else
        {
            testdata.entrySet().stream().forEach(e->{
                //Perceptron testP;
                ArrayList<Double> tempdata;
                    for(TreeMap<String,Double> n: e.getValue()){

                        tempdata = new ArrayList<>(n.values());
                        for(Map.Entry<String,Perceptron> b:layer.entrySet())
                            
                            resultset.put(b.getValue(),b.getValue().perceptron(tempdata));

                        //testP=selector(resultset,tempdata);
                        VerifyData.verify(resultset,e.getKey());
                        resultset.clear();
                    }
            });
            System.out.println("TP: "+VerifyData.tp+"  FP: "+VerifyData.fp+"  FN: "+VerifyData.fn);
            System.out.println("Precision: "+VerifyData.tp/(VerifyData.tp+VerifyData.fp));
            System.out.println("Recall: "+VerifyData.tp/(VerifyData.tp+VerifyData.fn));
        }


    }

    private static double askrate()
    {
        Scanner sc1 = new Scanner(System.in);
        System.out.println("Please type in the learning rate");
        double rate = sc1.nextDouble();
        return rate;
    }

    public static TreeMap<String, Double> manualInput()
    {
        TreeMap<String,Double> occurance = new TreeMap<>();
        for(char i='a';i<='z';i++)
            occurance.put(String.valueOf(i),0.0);
        Pattern p = Pattern.compile("[a-z]");
        Matcher m;

        System.out.println("Please type in the text");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in,"utf-8"));
            String temp, buffer=in.readLine();
            while(!buffer.equals("-1")){
                for(int i=0;i<buffer.length();i++){
                    temp=String.valueOf(buffer.charAt(i)).toLowerCase();
                    m= p.matcher(temp);
                    if(occurance.containsKey(temp) &&  m.matches())
                        occurance.put(temp,occurance.get(temp)+1.0);
                }
                buffer=in.readLine();
            }

            in.close();

            occurance=TrainFileReader.normalize(occurance);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return occurance;
    }

    private static Perceptron selector(HashMap<Perceptron,Integer> resultset, ArrayList<Double> tempdata){

        Perceptron bufferP=null;
        int i=0;
        double result1=0.0,result2=0.0;
        for(Map.Entry<Perceptron,Integer> e: resultset.entrySet()) {
            if(bufferP!=null) {
                for(Double b:tempdata) {
                    result1+=(b*e.getKey().weight.get(i));
                    result2+=(b*bufferP.weight.get(i));
                    i++;
                }
                i=0;
                if(result1>result2)
                    bufferP=e.getKey();
            }
            else {
               bufferP=e.getKey();
            }
        }

        return bufferP;
    }
}
