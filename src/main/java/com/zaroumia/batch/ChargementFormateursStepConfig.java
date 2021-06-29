package com.zaroumia.batch;

import static com.zaroumia.batch.mappers.FormateurItemPreparedStatementSetter.FORMATEURS_INSERT_QUERY;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.zaroumia.batch.domaine.Formateur;
import com.zaroumia.batch.listeners.ChargementFormateursStepListener;
import com.zaroumia.batch.mappers.FormateurItemPreparedStatementSetter;

/**
 * Charger les formateurs issus d'un csv vers la base donnée
 * @author oadyamoapa
 *
 */
@Configuration
public class ChargementFormateursStepConfig {

	@Bean
	@StepScope // bean sera crée à l'intérieur de la step et car il accède au jobParameters
	public FlatFileItemReader<Formateur> formateurItemReader(
			@Value("#{jobParameters['formateursFile']}") final Resource inputFile) {
		return new FlatFileItemReaderBuilder<Formateur>() // type lu = Formateur
				.name("FormateurItemReader")
				.resource(inputFile)
				.delimited()
				.delimiter(";")
				.names(new String[] { "id", "nom", "prenom",
						"adresseEmail" }) //définir le nom des colonnes pour qu'ils correspondent aux noms des attributs dans la classe formateur
				.targetType(Formateur.class) // classe de domaine destination
				.build();
	}

	@Bean
	public JdbcBatchItemWriter<Formateur> formateurItemWriter(final DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Formateur>()
				.dataSource(dataSource) //acccéder à la datasource
				.sql(FORMATEURS_INSERT_QUERY) // requete d'insertion
				.itemPreparedStatementSetter(new FormateurItemPreparedStatementSetter()) // assurer le mapping 
				.build();
	}

	@Bean
	public Step chargementFormateursStep(final StepBuilderFactory builderFactory) {
		return builderFactory.get("chargementFormateursStep")
				.<Formateur, Formateur>chunk(10) //lit un formateur et ecrit un formateur, chunk(10) : traitement par blok de 10 
				.reader(formateurItemReader(null)) // déclaration reader
				.writer(formateurItemWriter(null)) // déclaration writer, est appélé quand le nombre d'éléments définit par le chunk est atteint afin de resuire  les apples au writer
				.listener(chargementFormateursListener()) // ajout du listener
				.build();
	}

	@Bean // pour qu'il soit gérer par spring
	public StepExecutionListener chargementFormateursListener() {
		return new ChargementFormateursStepListener();
	}
}
