/**
 * 
 */
package duke.learn.scheduler.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Kazi
 *
 */
@SpringBootApplication(scanBasePackages = "duke.learn.scheduler")
public class SpringSchedulerApp {

    public static void main(String[] args) {
	SpringApplication.run(SpringSchedulerApp.class, args);
    }
}
