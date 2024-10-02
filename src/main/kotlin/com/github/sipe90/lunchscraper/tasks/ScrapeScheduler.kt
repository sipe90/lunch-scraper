package com.github.sipe90.lunchscraper.tasks

import com.github.sipe90.lunchscraper.config.LunchScraperConfiguration
import com.github.sipe90.lunchscraper.scraping.ScrapeService
import dev.starry.ktscheduler.job.Job
import dev.starry.ktscheduler.scheduler.KtScheduler
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class ScrapeScheduler(
    config: LunchScraperConfiguration,
    private val scrapeService: ScrapeService,
) {
    private val cron = config.schedulerConfig.cron
    private val scheduler = KtScheduler()

    fun start() {
        logger.info { "Starting scheduler" }

        val trigger = CronExpressionTrigger(cron)
        val job =
            Job(
                jobId = "scrape-job",
                trigger = trigger,
                runConcurrently = false,
                callback = ::scrape,
            )

        scheduler.addJob(job)
        scheduler.start()
    }

    fun pause() {
        logger.info { "Pausing scheduler" }
        scheduler.pause()
    }

    fun resume() {
        logger.info { "Resuming scheduler" }
        scheduler.resume()
    }

    fun shutdown() {
        logger.info { "Shutting down scheduler" }
        scheduler.shutdown()
    }

    fun isRunning() = scheduler.isRunning()

    private suspend fun scrape() {
        logger.info { "Starting scheduled scrape" }
        scrapeService.scrapeAllMenus()
        logger.info { "Finished scheduled scrape" }
    }
}
