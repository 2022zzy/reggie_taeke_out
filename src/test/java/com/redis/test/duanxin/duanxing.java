package com.redis.test.duanxin;

import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;

import java.util.HashMap;
import java.util.Set;

public class duanxing {

    public static void main(String[] args) {
        //生产环境请求地址：app.cloopen.com
        String serverIp = "app.cloopen.com";

        //请求端口
        String serverPort = "8883";

        //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
        String accountSId = "8a216da882b402070182cdfc81d10429";

        String accountToken = "b06839803a9e451783f2dd535907bd7d";

        //请使用管理控制台中已创建应用的APPID
        String appId = "8a216da882b402070182cdfc82ff0430";

        CCPRestSmsSDK sdk = new CCPRestSmsSDK();

        sdk.init(serverIp, serverPort);

        sdk.setAccount(accountSId, accountToken);

        sdk.setAppId(appId);

        sdk.setBodyType(BodyType.Type_JSON);

        //发送短信至手机号
        String to = "19196403020";

        //短信模板 为1是测试模板
        String templateId= "1";

        //这里模拟一下验证码123456，10分钟内到期
        String[] datas = {"123456","10"};

        HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);

        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for(String key:keySet){
                Object object = data.get(key);
                System.out.println(key +" = "+object);
            }
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
        }
    }
}
