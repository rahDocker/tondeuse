package com.mowltnow.tondeuse.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.PathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.mowltnow.tondeuse.entity.Position;
import com.mowltnow.tondeuse.entity.Tondeuse;
import com.mowltnow.tondeuse.service.TondeuseService;

@Configuration
public class BatchConfiguration {
	public static  int height ;
	public static int width;
	@Autowired
	private TondeuseService tondeuseService;
	private static int currentLineNumber = 0;

	@Bean
	public FlatFileItemReader<Tondeuse> itemReader() {
		currentLineNumber = 0;
		return new FlatFileItemReaderBuilder<Tondeuse>().name("textFileItemReader")
				.resource(new ClassPathResource("tondeuses.txt")).lineMapper(new DefaultLineMapper<Tondeuse>() {
					{
						setLineTokenizer(new LineTokenizer() {
							@Override
							public FieldSet tokenize(String line) {
								if(currentLineNumber == 0) {
								String[] list = line.split("\\s+");
								height = Integer.parseInt(list[0]);
								width = Integer.parseInt(list[1]);
							
								   return new DefaultFieldSet(new String[]{});
                                } else {
                                    return new DefaultFieldSet(new String[]{line});
                                }}
                                
						});

						setFieldSetMapper(new FieldSetMapper<Tondeuse>() {
							@Override
							public Tondeuse mapFieldSet(FieldSet fieldSet) {

								if (currentLineNumber != 0) {
									String[] list = fieldSet.readString(0).split("\\s+");
									Position position = new Position();

									position.setX(Integer.parseInt(list[0]));
									position.setY(Integer.parseInt(list[1]));
									position.setOrientation(list[2].charAt(0));

									System.out.println("Position initial : " + position.toString());
									return new Tondeuse(list[3], position);
								} else
									++currentLineNumber;
								return new Tondeuse("", new Position());

							}
						});
					}
				}).build();

	}

	@Bean
	public ItemProcessor<Tondeuse, Position> itemProcessor() {
		return item -> tondeuseService.getFinalPosition(item,height,width);

	}

	@Bean
	public ItemWriter<Position> itemWriter() {
		return items -> {
			for (Position position : items) {
				System.out.println("Position final : " + position);
			}
		};
	}

	@Bean
	public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("step1", jobRepository).<Tondeuse, Position>chunk(10, transactionManager)
				.reader(itemReader()).processor(itemProcessor()).writer(itemWriter()).build();
	}

	@Bean
	public Job job(JobRepository jobRepository, Step step1) {
		return new JobBuilder("jobTondeuse", jobRepository).start(step1).build();
	}

}
