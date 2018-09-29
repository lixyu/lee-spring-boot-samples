package com.lee.ai.aip.auth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 获取token类
 * @Order可以指定自启动顺序
 */
@Order(1)
@Slf4j
@Component
public class AuthBaiduService implements CommandLineRunner {

	@Value("${baidu.api.key}")
	private String baiduApiKey;
	@Value("${baidu.secret.key}")
	private String baiduSecretKey;

	public static final String BAIDU_ACCESS_TOKEN_KEY = "BAIDU_ACCESS_TOKEN";
	public static final long timeout = 719;//30 * 24 - 1;提前一小时刷新,防止token过期

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * 随容器自启动
	 */
	@Override
	public void run(String... args) throws Exception {
		getAccessToken();
	}


    /**
	 * 获取权限access_token
	 * 并存入redis缓存中
	 */
	public String getAccessToken() {
		String accessToken = stringRedisTemplate.opsForValue().get(BAIDU_ACCESS_TOKEN_KEY);
		if (StringUtils.isBlank(accessToken)) {
			accessToken = getAuth(baiduApiKey, baiduSecretKey);
			stringRedisTemplate.opsForValue().set(BAIDU_ACCESS_TOKEN_KEY, accessToken, timeout, TimeUnit.HOURS);
		}
		return accessToken;
    }

    /**
     * 获取API访问token
     * 该token有一定的有效期，需要自行管理，当失效时需重新获取.
     * @param ak - 百度云官网获取的 API Key
     * @param sk - 百度云官网获取的 Securet Key
     * @return assess_token 示例：
     * "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567"
     */
	public String getAuth(String ak, String sk) {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * 返回结果示例
             */
			log.info("result:" + result);
            JSONObject jsonObject = new JSONObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
			log.error("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }
    
	/*public static void main(String[] args) {
		// 官网获取的 API Key 更新为你注册的
		String clientId = "dZ3GPnEf1lqWp0OnvEzjc7aI";
		// 官网获取的 Secret Key 更新为你注册的
		String clientSecret = "ialSzfATv9jTtkcaGqqt2vAFe6mhs7Ng";
		System.out.println(getAuth(clientId, clientSecret));
	}*/

}
