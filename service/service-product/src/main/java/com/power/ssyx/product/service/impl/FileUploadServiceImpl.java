package com.power.ssyx.product.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.power.ssyx.product.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Powerveil
 * @Date 2023/8/5 21:01
 */
@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${aliyun.endpoint}")
    private String endpoint;
    @Value("${aliyun.keyid}")
    private String keyid;
    @Value("${aliyun.keysecret}")
    private String keysecret;
    // 填写Bucket名称，例如examplebucket。
    @Value("${aliyun.bucketname}")
    private String bucketname;

    @Override
    public String fileUpload(MultipartFile file) throws com.aliyuncs.exceptions.ClientException {
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();

        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String objectName = "exampledir/exampleobject.txt";
        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
//        String filePath= "D:\\localpath\\examplefile.txt";

//        objectName = file.getOriginalFilename();
//        objectName = createPath(file.getOriginalFilename());
        objectName = createPath3(file.getOriginalFilename());


        // 创建ClientBuilderConfiguration。
// ClientBuilderConfiguration是OSSClient的配置类，可配置代理、连接超时、最大连接数等参数。
//        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
//
//// 设置OSSClient允许打开的最大HTTP连接数，默认为1024个。
//        conf.setMaxConnections(200);
//// 设置Socket层传输数据的超时时间，默认为50000毫秒。
//        conf.setSocketTimeout(10000);
//// 设置建立连接的超时时间，默认为50000毫秒。
//        conf.setConnectionTimeout(10000);
//// 设置从连接池中获取连接的超时时间（单位：毫秒），默认不超时。
//        conf.setConnectionRequestTimeout(1000);
//// 设置连接空闲超时时间。超时则关闭连接，默认为60000毫秒。
//        conf.setIdleConnectionTime(10000);
//// 设置失败请求重试次数，默认为3次。
//        conf.setMaxErrorRetry(5);
//// 设置是否支持将自定义域名作为Endpoint，默认支持。
//        conf.setSupportCname(true);
//// 设置是否开启二级域名的访问方式，默认不开启。
//        conf.setSLDEnabled(true);
//// 设置连接OSS所使用的协议（HTTP或HTTPS），默认为HTTP。
//        conf.setProtocol(Protocol.HTTP);
//// 设置用户代理，指HTTP的User-Agent头，默认为aliyun-sdk-java。
//        conf.setUserAgent("aliyun-sdk-java");
//// 设置代理服务器端口。
//        conf.setProxyHost("<yourProxyHost>");
//// 设置代理服务器验证的用户名。
//        conf.setProxyUsername("<yourProxyUserName>");
//// 设置代理服务器验证的密码。
//        conf.setProxyPassword("<yourProxyPassword>");
//// 设置是否开启HTTP重定向，默认开启。
////        conf.setRedirectEnable(true); // 目前版本较低不能使用 3.10.1及以上
//// 设置是否开启SSL证书校验，默认开启。
////        conf.setVerifySSLEnable(true); // 目前版本较低不能使用 3.10.1及以上

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, keyid, keysecret);
//        OSS ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);
//        OSS ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider, conf);

        try {
            InputStream inputStream = file.getInputStream();
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketname, objectName, inputStream);
            putObjectRequest.setProcess("true");
            // 创建PutObject请求。
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            log.info("statusCode={}", result.getResponse().getStatusCode());
            log.info("uri={}", result.getResponse().getUri());
            log.info("errorResponseAsString={}", result.getResponse().getErrorResponseAsString());
            //返回上传文件在阿里云的路径
            String uri = result.getResponse().getUri();
            return uri;
        } catch (OSSException oe) {
            log.info("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            log.info("Error Message:{}", oe.getErrorMessage());
            log.info("Error Code:{}", oe.getErrorCode());
            log.info("Request ID:{}", oe.getRequestId());
            log.info("Host ID:{}", oe.getHostId());
        } catch (ClientException ce) {
            log.info("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            log.info("Error Message:{}", ce.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return null;
    }


    /**
     * 最终生成一个年/月/日/文件名
     * 例如2023/8/13
     *
     * @param fileName 文件名
     * @return 最终在OSS的文件路径
     */
    private String createPath(String fileName) {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        log.info("currentDate={}", currentDate);

        // 获取当前年份、月份和日期
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        fileName = uuid + fileName;

        String filePath = year + "/" + month + "/" + day + "/" + fileName;
        return filePath;
    }

    /**
     * 最终生成一个年/月/日/文件名
     * 例如2023/08/13
     *
     * @param fileName 文件名
     * @return 最终在OSS的文件路径
     */
    private String createPath2(String fileName) {
        // 获取文件后缀名（包含.）
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        log.info("currentDate={}", currentDate);
        // 获取当前年份、月份和日期
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        String filePath = year + "/" + month + "/" + day + "/" + uuid + suffix;
        return filePath;
    }


    /**
     * 最终生成一个年/月/日/文件名
     *
     * @param fileName 文件名
     * @return 最终在OSS的文件路径
     */
    private String createPath3(String fileName) {
        // 获取文件后缀名（包含.）
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String timeUrl = new DateTime().toString("yyyy/MM/dd");
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        String filePath = timeUrl + "/" + uuid + suffix;
        return filePath;
    }
}
