package com.lee.gateway;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lee
 * @date 2018/9/13
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bird {
    @JSONField(ordinal = 0)
    private String id;
    @JSONField(ordinal = 1)
    private String name;
    @JSONField(ordinal = 2)
    private String address;
    @JSONField(ordinal = 3)
    private int port;
}
