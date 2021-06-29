package com.zaroumia.batch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.jdbc.Sql;

import com.zaroumia.batch.dao.SeanceDao;
import com.zaroumia.batch.services.MailContentGenerator;
import com.zaroumia.batch.services.PlanningMailSenderService;

@Sql(scripts = { "classpath:init-formations-formateurs-tables.sql" })
public class ChargementSeancesStepConfigTest extends BaseTest {

	@Autowired
	private SeanceDao seanceDao;

	//@MockBean
	//PlanningMailSenderService  planningMailSenderService; 
	
	//@MockBean
	//MailContentGenerator  mailContentGenerator; 
	
	
	@Test
	//@Sql("classpath:init-formations-formateurs-tables.sql") //permets d'éxécuter un script sql avant une methode de test et peut etre placer au niveau de la classe et sera exécuter avant chaque méthode
	public void shouldLoadSeancesCsvWithSuccess() {
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("seancesFile", "classpath:inputs/seancesFile.csv")
				.toJobParameters();

		JobExecution result = jobLauncherTestUtils.launchStep("chargementSeancesCsvStep", jobParameters);

		assertThat(result.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

		assertThat(seanceDao.count()).isEqualTo(20);
	}

	@Test
	public void shouldLoadSeancesTxtWithSuccess() {
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("seancesFile", "classpath:inputs/seancesFile.txt")
				.toJobParameters();

		JobExecution result = jobLauncherTestUtils.launchStep("chargementSeancesTxtStep", jobParameters);

		assertThat(result.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

		assertThat(seanceDao.count()).isEqualTo(20);
	}

}
