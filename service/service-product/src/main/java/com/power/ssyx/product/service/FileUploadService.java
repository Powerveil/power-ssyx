package com.power.ssyx.product.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Powerveil
 * @Date 2023/8/5 21:01
 */
public interface FileUploadService {
    Object fileUpload(MultipartFile file);
}
