package org.github.ainr.unsplash.service

import org.github.ainr.unsplash.domain.{Photo, Statistics}

import java.io.File

case class AllUserStatistics(
    statistics: Statistics,
    charts: File
)
