package com.github.sipe90.lunchscraper.tasks

import com.github.sipe90.lunchscraper.scraping.ScrapeService
import dev.starry.ktscheduler.job.Job
import dev.starry.ktscheduler.scheduler.KtScheduler
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

class ScrapeScheduler(
    private val scrapeService: ScrapeService,
) : AutoCloseable {
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

    fun startOrResume(schedule: String) {
        if (scheduler.isRunning()) {
            updateSchedule(schedule)
            scheduler.resume()
        } else {
            start(schedule)
        }
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

    override fun close() {
        if (isRunning()) {
            shutdown()
        }
    }
}
