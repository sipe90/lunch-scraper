package com.github.sipe90.jakelunch.service

import com.github.sipe90.jakelunch.domain.MenuItem
import com.github.sipe90.jakelunch.domain.Venue
import com.github.sipe90.jakelunch.domain.WeekMenus
import org.springframework.stereotype.Service

@Service
class MenuService {
    fun getMenus(): WeekMenus =
        WeekMenus(
            year = 2024,
            week = 40,
            venues =
                listOf(
                    Venue(
                        name = "Test Venue",
                        url = "https://test-venue.com",
                        weeklyMenu = false,
                        buffet = false,
                        weekMenu =
                            listOf(
                                MenuItem(
                                    name = "Sika",
                                    description = "Nami",
                                    price = 13.50,
                                ),
                                MenuItem(
                                    name = "Porkkane",
                                    description = "Rousk",
                                    price = 12.45,
                                ),
                            ),
                    ),
                ),
        )
}
