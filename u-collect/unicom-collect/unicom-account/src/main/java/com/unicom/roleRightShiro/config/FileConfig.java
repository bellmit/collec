package com.unicom.roleRightShiro.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration

@Data
public class FileConfig {
	@Value("${unicom.upload.collectUpload}")
	private String upLoadFilePath;


}
