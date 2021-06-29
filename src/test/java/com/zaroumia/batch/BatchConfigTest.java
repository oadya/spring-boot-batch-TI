package com.zaroumia.batch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetup;
import com.zaroumia.batch.dao.FormateurDao;
import com.zaroumia.batch.dao.FormationDao;
import com.zaroumia.batch.dao.SeanceDao;

/**
 * Tester le job de bout en bout, éxécuter l'enchainement de tous les steps
 * @author oadyamoapa
 *
 */
public class BatchConfigTest extends BaseTest {

	// Tester l'envoi des emails en simulant le comportement d'un serveur smtp
	@Rule // permet d'ajouter le comportement avant et apres chacune de nos methodess de test
	public GreenMailRule serverSmtp = new GreenMailRule(new ServerSetup(2525, "localhost", ServerSetup.PROTOCOL_SMTP));

	@Autowired
	private FormateurDao formateurDao;
	@Autowired
	private FormationDao formationDao;
	@Autowired
	private SeanceDao seanceDao;

	@Test
	public void shouldExecuteJobWithSuccess() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("formateursFile", "classpath:inputs/formateursFile.csv")
				.addString("formationsFile", "classpath:inputs/formationsFile.xml")
				.addString("seancesFile", "classpath:inputs/seancesFile.csv")
				.toJobParameters();

		JobExecution result = jobLauncherTestUtils.launchJob(jobParameters);

		assertThat(result.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
		assertThat(formateurDao.count()).isEqualTo(16);
		assertThat(formationDao.count()).isEqualTo(4);
		assertThat(seanceDao.count()).isEqualTo(20);
		assertThat(serverSmtp.getReceivedMessages()).hasSize(4);
		assertThat(serverSmtp.getReceivedMessages()[0].getSubject()).isEqualTo("Votre planning de formations");
	}

}
