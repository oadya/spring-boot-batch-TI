package com.zaroumia.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;

import javax.mail.MessagingException;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.test.context.jdbc.Sql;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetup;

public class PlanningStepConfigTest extends BaseTest {

	@Rule
	public GreenMailRule serverSmtp = new GreenMailRule(new ServerSetup(2525, "localhost", ServerSetup.PROTOCOL_SMTP));

	@Test
	@Sql("classpath:init-all-tables.sql") //permets d'éxécuter un script sql avant une methode de test
	public void shouldSendPlanningsWithSuccess() throws MessagingException {

		JobExecution result = jobLauncherTestUtils.launchStep("planningStep");

		assertThat(result.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
		
		assertThat(serverSmtp.getReceivedMessages()).hasSize(4);
		// Mockito.verify(planningMailSenderService, times(4)).send(ArgumentMatchers.any(), ArgumentMatchers.any());
		
		assertThat(serverSmtp.getReceivedMessages()[0].getSubject()).isEqualTo("Votre planning de formations");
	}

}
