package com.mbank.project.batch;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "batch")
@Data
public class BatchProperties {
	private String inputFile;
}
