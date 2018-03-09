package com.example.aliyunossdemo;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.PolicyConditions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

@Controller
@RequestMapping(path = "/")
public class FileController {

    @Resource
    private AliyunOssConfig ossConfig;

    @RequestMapping(path = "getUploadToken", method = RequestMethod.GET)
    @ResponseBody
    public UploadTokenVo getUploadToken(@RequestParam("fileName") String fileName) throws UnsupportedEncodingException {
        String accessKey = ossConfig.getAccessKey();
        String secretKey = ossConfig.getSecretKey();
        String bucket = ossConfig.getBucket();
        String endPoint = ossConfig.getEndPoint();
        String uploadUrl = "//" + bucket + "." + endPoint;

        OSSClient client = new OSSClient(endPoint, accessKey, secretKey);

        // 生成过期时间
        LocalDateTime ldt = LocalDateTime.now().plusMinutes(10);
        Instant instant = ldt.toInstant(ZoneOffset.ofHours(8));
        Date expireDate = Date.from(instant);

        // 生成key
        String ext = fileName.substring(fileName.lastIndexOf("."));
        String key = UUID.randomUUID().toString() + ext;

        // 上传Policy
        final int minFileSize = 1; // 1Byte
        final int maxFileSize = 5120000; // 5MB
        PolicyConditions policyConds = new PolicyConditions();
        policyConds.addConditionItem(PolicyConditions.COND_KEY, key);
        // 文件大小限制
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, minFileSize, maxFileSize);
        String postPolicy = client.generatePostPolicy(expireDate, policyConds);
        String encodedPolicy = BinaryUtil.toBase64String(postPolicy.getBytes("utf-8"));

        // 签名
        String postSignature = client.calculatePostSignature(postPolicy);
        
        // 重要，OSSClient用完要关掉，不然会内存泄漏
        ossClient.shutdown();

        UploadTokenVo uploadTokenVo = new UploadTokenVo();
        uploadTokenVo.setAccessKey(accessKey);
        uploadTokenVo.setKey(key);
        uploadTokenVo.setPolicy(encodedPolicy);
        uploadTokenVo.setUploadUrl(uploadUrl);
        uploadTokenVo.setSignature(postSignature);
        return uploadTokenVo;
    }
}
