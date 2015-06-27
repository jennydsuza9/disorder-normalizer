/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tool.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author
 */
public class Util {
            
    public static <E> List<E> setList(List<E> list, E value) {
        if (!list.contains(value) && !value.equals(""))
            list.add(value);
        return list;
    }
    
    public static <E> List<E> addUnique(List<E> list, List<E> newList) {
        for (E value : newList)
            list = setList(list, value);
        return list;
    }    
    
    public static <E> Map<E,List<E>> setMap(Map<E,List<E>> keyValueListMap, E key, E value) {
        List<E> valueList = keyValueListMap.get(key);
        if (valueList == null)
            keyValueListMap.put(key, valueList=new ArrayList<>());
        valueList = setList(valueList, value);
        return keyValueListMap;
    }
       
    public static <E> boolean containsAny (List<E> first, List<E> second) {
        List<E> temp = new ArrayList<>(first);
        temp.retainAll(second);
        return !temp.isEmpty();
    }
    
    public static <E> int getTokenIndex (E[] tokens, String token) {
        int i = 0;
        while (i < tokens.length) {
            if (tokens[i].equals(token))
                return i;
            i++;
        }
        return -1;
    }
    
    public static <E> List<Integer> getTokenIndexes (E[] tokens, String token) {
        List<Integer> indexes = new ArrayList<>();
        int i = 0;
        while (i < tokens.length) {
            if (tokens[i].equals(token))
                indexes.add(i);
            i++;
        }
        return indexes;
    }
    
    public static int firstIndexOf(String[] tokens, int i, String pattern) {
        while (i >= 0) {
            if (tokens[i].matches(pattern+".*")) {
                i = i-1;
                return i;
            }
            i--;
        }
        return -1;
    }      
    
    public static String read(File file) throws IOException {
        byte[] data;
        try (FileInputStream fis = new FileInputStream(file)) {
            data = new byte[(int) file.length()];
            fis.read(data);
        }
        return new String(data, "UTF-8");
    }    
    
    public static String getExtension(File file) {
        String[] fileStringTokens = file.toString().split("\\.");
        return fileStringTokens[fileStringTokens.length-1];
    }
    
    public static void validateFilesInDirectory(File dir) throws IOException {
        for (File file : dir.listFiles()) {
            if (getExtension(file).equals("txt"))
                continue;
            BufferedReader in = new BufferedReader(new FileReader(file));    
            while (in.ready()) {
                String s = in.readLine().trim();
                if(s.split("\\|\\|").length != 5) {
                    System.out.println("Input Data Exception --> Check concept data file: "+file.toString());
                    System.out.println("Every line in file must have the following five main fields");
                    System.out.println("textfilename||concept_name_indexes||type||concept_name||normalized_identifier");
                    System.out.println("Multiple values within a field are delimited by \"|\"");
                    System.out.println("Only two fields \"concept_name_indexes\" and \"normalized_indentifier\" can have multiple values");
                    System.exit(1);
                }
            }
            if (!new File(file.toString().replace(".concept", ".txt")).exists()) {
                System.out.println("Input Data Exception --> Text data file for concept file: "+file.toString()+" is absent");
                System.exit(1);
            }
        }
    }
    
    public static void throwIllegalDirectoryException(String name) throws IOException {
        System.out.println("Input Parameter Exception --> "+name+" is not a directory");
        validateFilesInDirectory(new File(name));
        System.exit(1);
    }
    
    public static void throwIllegalFileException(String name) {
        System.out.println("Input Parameter Exception --> "+name+" is not a file");
        System.exit(1);
    }
    
}
