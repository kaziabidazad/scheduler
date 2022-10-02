/**
 * 
 */
package duke.learn.scheduler.encrypt;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author Kazi
 *
 */
@Service
public class EncryptScheduler {

    final static Logger LOGGER = LoggerFactory.getLogger(EncryptScheduler.class);

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS, initialDelay = 3)
    public void testSchedule() {
	LOGGER.info("Inside a scheduler");
    }
}
