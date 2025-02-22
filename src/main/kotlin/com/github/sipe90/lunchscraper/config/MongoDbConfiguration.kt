package com.github.sipe90.lunchscraper.config

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MongoDbConfiguration(
    val config: MongoDbConfig,
) {
    @Bean
    open fun mongoClient(): MongoClient = MongoClient.create(config.url)

    @Bean
    open fun mongoDatabase(client: MongoClient): MongoDatabase = client.getDatabase(config.database)
}
