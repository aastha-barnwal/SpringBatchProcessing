package com.example.SpringBatchProcessing.config;
import com.example.SpringBatchProcessing.entity.Customer;
import com.example.SpringBatchProcessing.entity.CustomerWriter;
import com.example.SpringBatchProcessing.repository.CustomerRespository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.MongoPagingItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.data.builder.MongoPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;
import java.util.HashMap;


@Configuration
public class BatchConfig {

    @Autowired
    private CustomerRespository customerRespository;

    @Autowired
    private DataSource dataSource;
    @Autowired
    MongoTemplate mongoTemplate;


    // mongodb to mysql

    //    creating Reader
    public MongoPagingItemReader<CustomerWriter> customerMongoReader(){
        return new MongoPagingItemReaderBuilder<CustomerWriter>().name("mongoReader")
        .template(mongoTemplate).targetType(CustomerWriter.class).sorts(new HashMap<>()).
                jsonQuery("{}").pageSize(5).build();
    }
    //Process
    @Bean
    public CustomerMongoProcessor customerMongoProcessor(){
        return new CustomerMongoProcessor();
    }

    //writer
    @Bean
    public RepositoryItemWriter<Customer> customerMongoWriter(){
        RepositoryItemWriter<Customer> repositoryWriter = new RepositoryItemWriter<>();
        repositoryWriter.setRepository(customerRespository);
        repositoryWriter.setMethodName("save");

        return repositoryWriter;
    }


    // file to mongodb

    // reader
    @Bean
    public FlatFileItemReader<CustomerWriter> customerReader() {
        FlatFileItemReader<CustomerWriter> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
        itemReader.setName("csv_reader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    private LineMapper<CustomerWriter> lineMapper() {
        DefaultLineMapper<CustomerWriter> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

        BeanWrapperFieldSetMapper<CustomerWriter> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(CustomerWriter.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    // process
    @Bean
    public CustomerProcessor customerProcessor() {
        return new CustomerProcessor();
    }
// writer
    @Bean
    public MongoItemWriter<CustomerWriter> customerWriter(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<CustomerWriter>()
                .template(mongoTemplate)
                .collection("CustomerWriter")
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }



    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, MongoTemplate mongoTemplate) {
        return new StepBuilder("step1", jobRepository)
                .<CustomerWriter, CustomerWriter>chunk(5, transactionManager)
                .reader(customerReader())
                .processor(customerProcessor())
                .writer(customerWriter(mongoTemplate))
                .build();
    }

    @Bean
    public Step step2(JobRepository jobRepository,PlatformTransactionManager transactionManager){
    return new StepBuilder("step2", jobRepository).<CustomerWriter,Customer>chunk(5,transactionManager)
            .reader(customerMongoReader()).processor(customerMongoProcessor()).writer(customerMongoWriter()).build();
    }



    @Bean
    public Job job(JobRepository jobRepository, Step step1) {
        return new JobBuilder("customer_job",jobRepository)
                .start(step1).next(step2(jobRepository,transactionManager()))
                .build();
    }


}
