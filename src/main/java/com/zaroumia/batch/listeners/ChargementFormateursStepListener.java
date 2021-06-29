package com.zaroumia.batch.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.listener.StepListenerSupport;

import com.zaroumia.batch.domaine.Formateur;

/**
 * Listener pour tracer le nombre de formateur traité par la Step
 * StepListenerSupport évite Override de void beforeStep(StepExecution stepExecution), déjà présente  dans StepListenerSupport
 * @author oadyamoapa
 *
 */
public class ChargementFormateursStepListener extends StepListenerSupport<Formateur, Formateur>
		implements StepExecutionListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChargementFormateursStepListener.class);

	@Override
	public ExitStatus afterStep(final StepExecution stepExecution) {
		LOGGER.info("Chargement des formateurs :{} formateur(s) enregistré(s) ", stepExecution.getWriteCount());
		return stepExecution.getExitStatus();
	}

}
