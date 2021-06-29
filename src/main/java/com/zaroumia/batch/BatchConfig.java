package com.zaroumia.batch;

import java.util.Arrays;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.zaroumia.batch.deciders.SeancesStepDecider;
import com.zaroumia.batch.validators.MyJobParametersValidator;

@Configuration
//activer le mode batch
@EnableBatchProcessing
public class BatchConfig {

	/**
	 * Pour valider les paramètres requis sont passés
	*/
	@Bean
	public JobParametersValidator defaultJobParametersValidator() {
		DefaultJobParametersValidator bean = new DefaultJobParametersValidator();
		bean.setRequiredKeys(new String[] { "formateursFile", "formationsFile", "seancesFile" }); //liste de noms des paramètres obligatoires
		bean.setOptionalKeys(new String[] { "run.id" });
		return bean;
	}

	@Bean
	public JobParametersValidator myJobParametersValidator() {
		return new MyJobParametersValidator();
	}

	/**
	 * Composer les JobParametersValidator
	 * */
	@Bean
	public JobParametersValidator compositeJobParametersValidator() {
		CompositeJobParametersValidator bean = new CompositeJobParametersValidator();
		bean.setValidators(Arrays.asList(defaultJobParametersValidator(), myJobParametersValidator()));
		return bean;
	}

	@Bean
	public JobExecutionDecider seancesStepDecider() {
		return new SeancesStepDecider();
	}

	/**
	 * Flow pour chargementFormateursStep
	 * @param chargementFormateursStep
	 * @return
	 */
	@Bean
	public Flow chargementFormateursFlow(final Step chargementFormateursStep) {
		return new FlowBuilder<Flow>("chargementFormateursFlow")
				.start(chargementFormateursStep)
				.end();
	}

	/**
	 * Flow pour chargementFormationsStep
	 * @param chargementFormationsStep
	 * @return
	 */
	@Bean
	public Flow chargementFormationsFlow(final Step chargementFormationsStep) {
		return new FlowBuilder<Flow>("chargementFormationsFlow")
				.start(chargementFormationsStep)
				.end();
	}

	/**
	 * Flow pour parallélisé les 2 flow
	 * @return
	 */
	@Bean
	public Flow parallelFlow() {
		return new FlowBuilder<Flow>("parallelFlow")
				.split(new SimpleAsyncTaskExecutor())
				.add(chargementFormateursFlow(null), chargementFormationsFlow(null)) //ajout des flows
				.end();
	}

	/**
	 * Job avec parallélisation
	 * 
	 * @param jobBuilderFactory
	 * @param chargementSeancesCsvStep
	 * @param chargementSeancesTxtStep
	 * @param planningStep
	 * @return
	 */
	@Bean
	public Job job(final JobBuilderFactory jobBuilderFactory, final Step chargementSeancesCsvStep,
			final Step chargementSeancesTxtStep, final Step planningStep) {
		return jobBuilderFactory.get("formations-batch")
				.start(parallelFlow()) // parallélisation des steps
				.next(seancesStepDecider()).on("txt").to(chargementSeancesTxtStep)
				.from(seancesStepDecider()).on("csv").to(chargementSeancesCsvStep)
				.from(chargementSeancesTxtStep).on("*").to(planningStep) // envoie du mail
				.from(chargementSeancesCsvStep).on("*").to(planningStep) // envoie du mail
				.end()
				.validator(compositeJobParametersValidator()) // déclaration des validateurs
				.incrementer(new RunIdIncrementer()) // génération in id auto-incremantal pour distinguer de façon unique la liste la paramtre passer à un job
				.build();
	}
	
	/**
	 * Job sans parallélisation
	 * @param jobBuilderFactory
	 * @param chargementFormateursStep
	 * @param chargementFormationsStep
	 * @param chargementSeancesCsvStep
	 * @param chargementSeancesTxtStep
	 * @return
	 */
	 /* @Bean public Job job(final JobBuilderFactory jobBuilderFactory, final Step
	  chargementFormateursStep, final Step chargementFormationsStep,  final Step chargementSeancesCsvStep, final Step chargementSeancesTxtStep ) { return jobBuilderFactory.get("formations-batch")
	  .start(chargementFormateursStep)
	  .next(chargementFormationsStep)
	  .next(seancesStepDecider()) // le job passe dans un decider
	  .from(seancesStepDecider()).on("txt").to(chargementSeancesTxtStep) // lancer le step txt
	  .from(seancesStepDecider()).on("csv").to(chargementSeancesCsvStep) // lancer le step csv
	  .from(chargementSeancesTxtStep).on("*").end() // pour n'importe qu'elle valeur, mettre fin
	  .from(chargementSeancesCsvStep).on("*").end() // pour n'importe qu'elle valeur, mettre fin
	  .end()
	  .validator(compositeJobParametersValidator())
	  .incrementer(new RunIdIncrementer())
	  .build();
	  }*/
	 

}
