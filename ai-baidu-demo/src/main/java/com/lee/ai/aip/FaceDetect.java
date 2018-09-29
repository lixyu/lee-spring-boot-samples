package com.lee.ai.aip;

import java.util.HashMap;
import java.util.Map;

import com.lee.ai.aip.utils.Base64Util;
import com.lee.ai.aip.utils.GsonUtils;
import com.lee.ai.aip.utils.HttpUtil;

/**
 * 人脸检测与属性分析
 */
public class FaceDetect {

	/**
	 * 重要提示代码中所需工具类 FileUtil,Base64Util,HttpUtil,GsonUtils请从
	 * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
	 * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
	 * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
	 * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3 下载
	 */
	public static String detect() {
		// 请求url
		String url = "https://aip.baidubce.com/rest/2.0/face/v3/detect";
		try {
			String path = "http://a.hiphotos.baidu.com/image/pic/item/fc1f4134970a304eb5088f73ddc8a786c9175c14.jpg";
			String base64 = Base64Util.encode(path.getBytes());
			System.out.println(base64);
			Map<String, Object> map = new HashMap<>();
			map.put("image", path);
			map.put("face_field", "faceshape,facetype");
			map.put("image_type", "URL");

			String param = GsonUtils.toJson(map);

			// 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
			String accessToken = "24.b54b81f960c92ac3ba3f9ab13d4b46f6.2592000.1532566123.282335-10864518";

			String result = HttpUtil.post(url, accessToken, "application/json", param);
			System.out.println(result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		FaceDetect.detect();
	}
}