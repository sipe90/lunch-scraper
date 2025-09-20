package com.github.sipe90.lunchscraper.config

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.server.plugins.di.annotations.Property

fun provideMongoClient(
    @Property("lunch-scraper.mongo-db.url") url: String,
): MongoClient = MongoClient.create(url)

fun provideMongoDatabase(
    @Property("lunch-scraper.mongo-db.database") database: String,
    client: MongoClient,
): MongoDatabase = client.getDatabase(database)
