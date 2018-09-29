package com.lee.gateway;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author lee
 * @date 2018/9/13
 */

public class Test3 {

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
                System.out.println("- id: rmcloud-"+name);
                System.out.println("  uri: lb://"+name);
                System.out.println("  predicates:");
                System.out.println("  - Path="+arr[2].replace("V1","{version}"));
                System.out.println("  filters:");
                System.out.println("  - OauthFilter");
                System.out.println("  - RmcloudResponseFilter");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        map.put("services",list);
        System.out.println(list.size());

        //System.out.println(JSON.toJSONString(map));


    }

}
