package com.mbank.project.batch;


import com.mbank.project.entity.Transaction;
import com.mbank.project.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;

/*
 * Class to handle batch configurations
 */
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final TransactionRepository transactionRepository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

	@Autowired
	private BatchProperties batchProperties;
	
    @Bean
    public FlatFileItemReader<Transaction> transactionReader() {
    	BeanWrapperFieldSetMapper<Transaction> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Transaction.class);

        // Register custom editors for LocalDate and LocalTime
        fieldSetMapper.setCustomEditors(Map.of(
            LocalDate.class, new PropertyEditorSupport() {
                @Override
                public void setAsText(String text) throws IllegalArgumentException {
                    if (text == null || text.isBlank()) {
                        setValue(null);
                    } else {
                        setValue(LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    }
                }
            },
            LocalTime.class, new PropertyEditorSupport() {
                @Override
                public void setAsText(String text) throws IllegalArgumentException {
                    if (text == null || text.isBlank()) {
                        setValue(null);
                    } else {
                        setValue(LocalTime.parse(text, DateTimeFormatter.ofPattern("HH:mm:ss")));
                    }
                }
            }
        ));
        return new FlatFileItemReaderBuilder<Transaction>()
                .name("transactionItemReader")
                .recordSeparatorPolicy(new BlankLineRecordSeparatorPolicy())
                .resource(new ClassPathResource(batchProperties.getInputFile()))
                .linesToSkip(1)
                .delimited()
                .delimiter("|")
                .names("accountNumber", "trxAmount", "description", "trxDate", "trxTime", "customerId")
                .fieldSetMapper(fieldSetMapper)
                .build();
    }

    @Bean
    public TransactionItemProcessor transactionProcessor() {
        return new TransactionItemProcessor(transactionRepository);
    }

    @Bean
    public ItemWriter<Transaction> transactionWriter() {
    	
        return items -> {
            // remove duplicates
            Map<String, Transaction> uniqueMap = new LinkedHashMap<>();
            for (Transaction item : items) {
                String key = String.join("|",
                        safe(item.getAccountNumber()),
                        safe(item.getTrxAmount()),
                        safe(item.getDescription()),
                        safe(item.getTrxDate()),
                        safe(item.getTrxTime()),
                        safe(item.getCustomerId())
                );
                uniqueMap.putIfAbsent(key, item);
            }

            Collection<Transaction> uniqueItems = uniqueMap.values();
            for (Transaction item : uniqueItems) {
            	//remove null
            	if(item==null) {
            		continue;
            	}
                try {
                    transactionRepository.save(item);
                } catch (DataIntegrityViolationException e) {
                    System.out.println("Duplicate record rejected by DB constraint: " + item);
                }
            }
        };
    }

    private String safe(Object obj) {
        return obj == null ? "" : obj.toString().trim();
    }
    
    @Bean
    public Step transactionStep() {
        return new StepBuilder("transactionStep", jobRepository)
                .<Transaction, Transaction>chunk(5, transactionManager)
                .reader(transactionReader())
                .processor(transactionProcessor())
                .writer(transactionWriter())
                .build();
    }

    @Bean
    public Job transactionJob() {
        return new JobBuilder("transactionJob", jobRepository)
                .start(transactionStep())
                .build();
    }
    
    @Bean
    public CommandLineRunner runJob(JobLauncher jobLauncher, Job importTransactionJob) {
        return args -> {
            JobParameters params = new JobParametersBuilder()
                    .addString("inputFile", batchProperties.getInputFile())
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(importTransactionJob, params);
        };
    }
}