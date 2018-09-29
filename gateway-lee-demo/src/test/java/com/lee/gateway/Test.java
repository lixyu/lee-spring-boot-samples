package com.lee.gateway;

import com.alibaba.fastjson.JSON;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author lee
 * @date 2018/9/13
 */

public class Test {

    public static void main(String[] args) throws IOException{
        String path="D:/rmcloud_route/test3.txt";
//        String path="D:/prd.txt";
        readFile1(path);
    }

    private static void readFile1(String path) throws IOException {
        File fin=new File(path);
        FileInputStream fis = new FileInputStream(fin);

        //Construct BufferedReader from InputStreamReader
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        Map<String, List<Bird>> map=new HashMap<>();
        List<Bird> list=new LinkedList<>();
        Set<String> stringSet=new HashSet<>();
        Map<String,String> hash=new HashMap<>();
        int i=0;
        String line = null;
        while ((line = br.readLine()) != null) {
           // System.out.println(line);
            String[] arr=line.split("\t");

            try {
                URL uri=new URL(arr[0]);
                String id="rmcloud-"+arr[1];
                String name =arr[1];
                String address=uri.getHost();
                int port=uri.getPort()==-1?80:uri.getPort();
                Bird bird=new Bird(id,name,address,port);
                list.add(bird);
               //System.out.println(JSON.toJSONString(bird));
                String p=uri.getPath();
                //System.out.println(p);
                String[] first=p.split("/");

                stringSet.add(address+":"+port);
                hash.put(address+":"+port+"/"+first[1],first[1]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

//        System.out.println(stringSet.size());
//        System.out.println(stringSet.toString());
        map.put("services",list);

        br.close();



        Set<String> set=new HashSet<>();
        Map<String, List<Bird>> map1=new HashMap<>();
        List<Bird> list1=new LinkedList<>();
        for(Map.Entry<String,String> entry:hash.entrySet()){
            String key=entry.getKey();
            String value=entry.getValue();
            URL url=new URL("http://"+entry.getKey());

            Bird b1=new Bird("rmcloud-"+value,value,url.getHost(),url.getPort());

            if (!set.contains(value)){
                list1.add(b1);
//                System.out.println("- id: rmcloud-"+value);
//                System.out.println("  uri: lb://"+value);
//                System.out.println("  predicates:");
//                System.out.println("  - Path=/RMCloud/{api}/{data}/"+value+"*/{version}");
//                System.out.println("  filters:");
//                System.out.println("  - OauthFilter");
//                System.out.println("  - RmcloudResponseFilter");
            }

            set.add(value);
        }
        map1.put("services",list1);
        System.out.println(JSON.toJSONString(map1));
        System.out.println(list1.size());
        System.out.println(set.size());
    }

    public String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }
}
