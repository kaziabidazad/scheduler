/**
 * 
 */
package duke.learn.scheduler.config;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @author Kazi
 *
 */
@Configuration
@EnableScheduling
@EnableAsync
public class SchedulerConfig implements SchedulingConfigurer {

    @Value(value = "${thread.maxthread}")
    private Integer MAX_THREAD_FOR_EXECUTOR;

    static {
	Configurator.initialize(null, "/opt/resources/springscheduler/log4j2.json");
    }

    @Bean(name = "messageSource")
    public ReloadableResourceBundleMessageSource getMessageSource() {
	ReloadableResourceBundleMessageSource resource = new ReloadableResourceBundleMessageSource();
	resource.setBasename("classpath:messages");
	resource.setDefaultEncoding("UTF-8");
	return resource;
    }

    @Bean(name = "propertyPlaceholderConfigurer")
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() throws IOException {
	PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
	propertyPlaceholderConfigurer
		.setLocations(new FileUrlResource("/opt/resources/springscheduler/scheduler.properties"));
	propertyPlaceholderConfigurer.setIgnoreResourceNotFound(false);
	propertyPlaceholderConfigurer.setIgnoreUnresolvablePlaceholders(false);
	return propertyPlaceholderConfigurer;
    }

    /**
     * <header> <div class="evo_post_title">
     * <h1>Rules of a ThreadPoolExecutor pool size</h1></div>
     * <div class="small text-muted">
     * <div class="evo_widget widget_core_item_info_line">Posted by
     * <span class="login user nowrap" rel="bubbletip_user_1"><span class=
     * "identity_link_username">davidnewcomb</span></span> on 27 Nov 2009 in
     * <a href="http://www.bigsoft.co.uk/blog/techie/" title="Browse
     * category">Techie</a> </div> </div> </header>
     * <div class="evo_container evo_container__item_single">
     * <div class="evo_widget widget_core_item_content"><section class=
     * "evo_post__full"><div class="evo_post__full_text clearfix">
     * <p>
     * The rules for the size of a
     * <code class="codespan">ThreadPoolExecutor</code>'s pool are generally
     * miss-understood, because it doesn't work the way that you think it ought to
     * or in the way that you want it to.
     * </p>
     * <p>
     * Take this example. Starting thread pool size is 1, core pool size is 5, max
     * pool size is 10 and the queue is 100.
     * </p>
     * <p>
     * Sun's way: as requests come in threads will be created up to 5, then tasks
     * will be added to the queue until it reaches 100. When the queue is full new
     * threads will be created up to <code class="codespan">maxPoolSize</code>. Once
     * all the threads are in use and the queue is full tasks will be rejected. As
     * the queue reduces so does the number of active threads.
     * </p>
     * <p>
     * User anticipated way: as requests come in threads will be created up to 10,
     * then tasks will be added to the queue until it reaches 100 at which point
     * they are rejected. The number of threads will rename at max until the queue
     * is empty. When the queue is empty the threads will die off until there are
     * <code class="codespan">corePoolSize</code> left.
     * </p>
     * <p>
     * The difference is that the users want to start increasing the pool size
     * earlier and want the queue to be smaller, where as the Sun method want to
     * keep the pool size small and only increase it once the load becomes to much.
     * </p>
     * <p>
     * Here are Sun's rules for thread creation in simple terms:
     * </p>
     * <ol>
     * <li>If the number of threads is less than the
     * <code class="codespan">corePoolSize</code>, create a new Thread to run a new
     * task.</li>
     * <li>If the number of threads is equal (or greater than) the
     * <code class="codespan">corePoolSize</code>, put the task into the queue.</li>
     * <li>If the queue is full, and the number of threads is less than the
     * <code class="codespan">maxPoolSize</code>, create a new thread to run tasks
     * in.</li>
     * <li>If the queue is full, and the number of threads is greater than or equal
     * to <code class="codespan">maxPoolSize</code>, reject the task.</li>
     * </ol>
     * <p>
     * The long and the short of it is that new threads are only created when the
     * queue fills up, so if you're using an unbounded queue then the number of
     * threads will not exceed <code class="codespan">corePoolSize</code>.
     * </p>
     * <p>
     * For a fuller explanation, get it from the horses mouth: <a href=
     * "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html"
     * class="extlink" target="_new">ThreadPoolExecutor API documentation</a>.
     * </p>
     * <p>
     * There is a really good forum post which talks you through the way that the
     * <code class="codespan">ThreadPoolExecutor</code> works with code examples:
     * <a href=
     * "http://forums.sun.com/thread.jspa?threadID=5401400&amp;tstart=0">http://forums.sun.com/thread.jspa?threadID=5401400&amp;tstart=0</a>
     * </p>
     * <p>
     * More info: <a href=
     * "http://forums.sun.com/thread.jspa?threadID=5224557&amp;tstart=450">http://forums.sun.com/thread.jspa?threadID=5224557&amp;tstart=450</a>
     * </p>
     * <p>
     * Most people want it the other way around, so that you increase the number of
     * threads to avoid adding to the queue. When the threads are all in use the
     * queue starts to fill up.
     * </p>
     * <p>
     * Using Sun's way, I think you are going to end up with a system that runs
     * slower when the load is light and a bit quicker as the load increases. Using
     * the other way means you are running flat out all the time to process
     * outstanding work.
     * </p>
     * <p>
     * I just don't see why they have done it this way. So far I have not seen a
     * satisfactory explanation of why Sun's implementation works the way it does.
     * Does anyone out there know?
     * </p>
     * </div></section></div><div class="evo_widget
     * widget_core_item_attachments"></div><div class="evo_widget
     * widget_core_item_link"></div><div class="evo_widget
     * widget_core_item_location"></div><div class="evo_widget
     * widget_core_item_tags"><nav class="small post_tags">Tags:
     * <a rel="tag" href="http://www.bigsoft.co.uk/blog/tag/java">java</a>,
     * <a rel="tag" href="http://www.bigsoft.co.uk/blog/tag/pooling">pooling</a>,
     * <a rel="tag" href=
     * "http://www.bigsoft.co.uk/blog/tag/threadpoolexecutor">threadpoolexecutor</a></nav></div>
     * </div>
     * 
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    @DependsOn(value = "propertyPlaceholderConfigurer")
    public Executor asyncExecutor() {
	ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	executor.setCorePoolSize(MAX_THREAD_FOR_EXECUTOR);
	executor.setMaxPoolSize(MAX_THREAD_FOR_EXECUTOR);
	executor.setAllowCoreThreadTimeOut(true);
	executor.setQueueCapacity(Integer.MAX_VALUE);
	executor.setThreadNamePrefix("Async-Task=>   ");
	executor.initialize();
	return executor;
    }

    @Bean(destroyMethod = "shutdown")
    public Executor taskScheduler() {
	ScheduledExecutorService executorService = Executors.newScheduledThreadPool(MAX_THREAD_FOR_EXECUTOR);
	return executorService;

    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
	taskRegistrar.setScheduler(taskScheduler());
    }
}
