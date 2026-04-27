package eu.urbanage.GeoDataExtractor.Job;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.urbanage.GeoDataExtractor.model.Cron;
import eu.urbanage.GeoDataExtractor.model.Document;
import eu.urbanage.GeoDataExtractor.model.MultiPointRadius;
import eu.urbanage.GeoDataExtractor.model.MultiPolygon;
import eu.urbanage.GeoDataExtractor.service.CronService;
import eu.urbanage.GeoDataExtractor.service.DocumentService;
import eu.urbanage.GeoDataExtractor.service.GeojsonService;
import eu.urbanage.GeoDataExtractor.utils.GeoJsonUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Component
public class UserJob implements Job {

    @Autowired
    private CronService cs;

    @Autowired
    protected DocumentService ds;

    @Autowired
    protected GeojsonService geoserv;

    private static final Logger LOGGER = LoggerFactory.getLogger(FilterJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        LOGGER.info("Start User Cron Service");

        LocalDateTime startJob = LocalDateTime.now();

        List<Cron> cronList = cs.getAllCron();

        for (Cron selCron : cronList) {
            String document_id = selCron.getDocument_id();

            Document releated_document = ds.findDocumentObj(document_id);

            if (releated_document == null) {
                LOGGER.warn("Document {} not found for cron {}, skipping", document_id, selCron.getId());
                continue;
            }

            Date lastExecution = selCron.getData_last_execution();

            int scheduled = selCron.getRepeat();

            LocalDateTime lastExecutionLocal = lastExecution.toInstant().atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            long differenzaInOre = ChronoUnit.HOURS.between(lastExecutionLocal, startJob);

            if (differenzaInOre >= scheduled) {

                MultiPolygon testMP = new MultiPolygon();

                testMP.setFilter(releated_document.getFilter());

                testMP.setSubfilter(releated_document.getSubfilter());

                testMP.setCityName(releated_document.getCityName());

                testMP.setPolygon(selCron.getMultiPolygon());

                String geojsonMP = geoserv.getFromMultiPolygon(testMP).toString();

                MultiPointRadius testMPR = new MultiPointRadius();

                testMPR.setFilter(releated_document.getFilter());
                testMPR.setSubfilter(releated_document.getSubfilter());
                testMPR.setCityName(releated_document.getCityName());
                testMPR.setMultipoint(selCron.getMultipoint());
                String geojsonMPR;
                try {
                    geojsonMPR = geoserv.getFromMultiPointRadius(testMPR).toString();
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                releated_document.setGeojson(GeoJsonUtils.mergeFeatureCollections(geojsonMP, geojsonMPR));

                ds.updateDocument(releated_document);
                selCron.setData_last_execution(new Date());
                cs.updateCronAfterExec(selCron);

            }

        }

        LOGGER.info("End User Cron Service");

    }
}
