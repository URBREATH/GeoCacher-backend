package eu.urbanage.GeoDataExtractor.Job;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.urbanage.GeoDataExtractor.service.FilterDocumentService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilterJob implements Job {
    @Autowired
    protected FilterDocumentService fds;
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterJob.class);

    @Override
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        LOGGER.info("Start Filter Cron Service");
        try {
            fds.checkFilter();
        } catch (JsonProcessingException e) {
            LOGGER.error("FilterJob failed due to JSON processing error", e);
            throw new JobExecutionException(e);
        }
        LOGGER.info("End Filter Cron Service");
    }

}