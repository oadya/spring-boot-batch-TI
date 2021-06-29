package com.zaroumia.batch.policies;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Spring offre la possiblite d'ignorer un enregistrement via SkipPolicy
 * @author oadyamoapa
 *
 */
public class SeanceSkipPolicy implements SkipPolicy {

	@Override
	public boolean shouldSkip(final Throwable t, final int skipCount) throws SkipLimitExceededException {
		// on peut ignorer un enregistrement à partir du type de l'exception  
		// ou à partir du nombre d'enregistrement skipCount que l'on autorise avant de considérer que l'échec d'éxécution de la step
		if (t instanceof DataIntegrityViolationException && skipCount < 10) {
			return true;
		}
		return false;
	}

}
