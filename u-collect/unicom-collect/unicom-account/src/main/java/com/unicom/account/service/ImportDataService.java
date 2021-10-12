package com.unicom.account.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ImportDataService {

    Map<String, Object> importData(MultipartFile file, Map<String, Object> parm, int type);

    Map<String, Object> listAll(Map<String, Object> parm);

    Map<String, Object> validateCard(MultipartFile file, Map<String, Object> parm);
}
