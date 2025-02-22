package com.github.sipe90.lunchscraper.tasks

import com.github.sipe90.lunchscraper.scraping.ScrapeService
import dev.starry.ktscheduler.job.Job
import dev.starry.ktscheduler.scheduler.KtScheduler
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class ScrapeScheduler(
    private val scrapeService: ScrapeService,
) {
    private val scheduler = KtScheduler()

    fun start(schedule: String) {
        addJob(schedule)
        scheduler.start()

        logger.info { "Starting job with schedule: $schedule. Next run time is at: ${getJob().nextRunTime}." }
    }

    fun updateSchedule(schedule: String) {
        removeJob()
        addJob(schedule)

        logger.info { "Updating job with schedule: $schedule. Next run time is at: ${getJob().nextRunTime}." }
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
        logger.info { "Finished scheduled scrape. Next run time is at: ${getJob().nextRunTime}." }
    }

    private fun addJob(schedule: String) {
        val trigger = CronExpressionTrigger(schedule)
        val job =
            Job(
                jobId = "scrape-job",
                trigger = trigger,
                runConcurrently = false,
                callback = ::scrape,
            )

        scheduler.addJob(job)
    }

    private fun getJob() = scheduler.getJobs().first()

    private fun removeJob() = scheduler.removeJob(getJob().jobId)
}
