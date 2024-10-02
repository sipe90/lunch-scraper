package com.github.sipe90.lunchscraper.tasks

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import dev.starry.ktscheduler.triggers.Trigger
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.jvm.optionals.getOrNull

class CronExpressionTrigger(
    cronString: String,
) : Trigger {
    private val executionTime =
        CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ).let {
            val cronParser = CronParser(it)
            val cron = cronParser.parse(cronString)
            ExecutionTime.forCron(cron)
        }

    override fun getNextRunTime(
        currentTime: ZonedDateTime,
        timeZone: ZoneId,
    ): ZonedDateTime? = executionTime.nextExecution(currentTime).getOrNull()
}
