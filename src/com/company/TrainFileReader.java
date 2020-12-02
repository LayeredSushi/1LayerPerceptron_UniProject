package com.company;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.walkFileTree;

public class TrainFileReader implements FileVisitor {

    Path srcDir;
    TreeMap<String,Double> occurance;
    Pattern p;
    TreeMap<String,ArrayList<TreeMap<String,Double>>> mapOccurance;
    String flag;

    public TrainFileReader(String datapath,String flag)
    {
        srcDir= Paths.get(datapath);
        this.flag=flag;
    }

    public TreeMap<String,ArrayList<TreeMap<String,Double>>> reader()
    {
        mapOccurance = new TreeMap<>();
        p = Pattern.compile("[a-z]");
        try {
            walkFileTree(srcDir,this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapOccurance;
    }

    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {

        Path path1 = (Path)file;
        File file1 = path1.toFile();
        Charset charset = Charset.forName("unicode");
        CharsetDecoder cd = charset.newDecoder();
        Matcher m;
        occurance = new TreeMap<>();
        for(char i='a';i<='z';i++)
            occurance.put(String.valueOf(i),0.0);

        if(file1.getName().startsWith(flag))
            return CONTINUE;


        if(attrs.isRegularFile()){

            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file1), cd));
            String buffer = in.readLine(),temp;
            while(buffer!=null) {
                for(int i=0;i<buffer.length();i++){
                    temp=String.valueOf(buffer.charAt(i)).toLowerCase();
                    m= p.matcher(temp);
                    if(occurance.containsKey(temp) &&  m.matches())
                        occurance.put(temp,occurance.get(temp)+1.0);
                }
                buffer=in.readLine();
            }
            in.close();

            occurance=normalize(occurance);

            File langfile = path1.toFile();
            String langname = langfile.getParentFile().getParentFile().getName();

            if(mapOccurance.containsKey(langname))
                mapOccurance.get(langname).add(occurance);
            else{
                mapOccurance.put(langname,new ArrayList<>());
                mapOccurance.get(langname).add(occurance);
            }

        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException {
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException {
        return CONTINUE;
    }

    public static TreeMap<String,Double>  normalize( TreeMap<String,Double> occurance){

        double vectorMod = 0;

        for(Map.Entry<String,Double> e: occurance.entrySet())
            vectorMod+=Math.pow(e.getValue(),2);

        vectorMod=Math.sqrt(Math.abs(vectorMod));


        for(Map.Entry<String,Double> e: occurance.entrySet())
            occurance.put(e.getKey(),e.getValue()/vectorMod);

        return occurance;
    }
}
