package com.lee.gateway;

import org.apache.commons.lang3.StringUtils;

/**
 * @author lee
 * @date 2018/9/13
 */

public class TT {

    public static void main(String[] args){
        String s="/RMCloud/API/datacollect/miGuanVarByToken/V1";

        String[] arr=StringUtils.split(s,"/");
        System.out.println(arr.length);
        System.out.println(arr[0]);
        System.out.println(arr[1]);
        System.out.println(arr[2]);
        System.out.println(arr[3]);

    }
}
