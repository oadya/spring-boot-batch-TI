package com.zaroumia.batch;

import org.junit.runner.RunWith;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zaroumia.batch.services.PlanningMailSenderService;

@RunWith(SpringRunner.class)
// @JdbcTest
@SpringBootTest // va scanner tous les packages et charger tous les beans nécessaire
@SpringBatchTest //activer les fonctionnalités de spring batch, plus besoin de creer les tables de spring batch metadata
//@ContextConfiguration(classes = ConfigurationForTest.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED) // pour désactiver le comportement transactionnel de jdbctest
public abstract class BaseTest {

	// classe utilitaire qui permet de démarrer un job ou une step dans les tests
	@Autowired
	protected JobLauncherTestUtils jobLauncherTestUtils;
	
	
	//@MockBean
	//PlanningMailSenderService  planningMailSenderService; 
	
	//@MockBean
	//MailContentGenerator  mailContentGenerator; 

}
