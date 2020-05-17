package com.dnatic.api

import com.dnatic.data.TestContainersDao
import com.dnatic.exception._

import scala.util.control.NonFatal

/**
 * Service layer
 * @param dao DAO instance
 */
class TestContainersApi(dao: TestContainersDao) {
  def getDataByDataId(dataId: String): Either[Throwable, Option[String]] = {
    try {
      val result = Option(dao.getData(dataId))

      // Arbitrary rules: Result must be 10 characters long and contain ABC
      result match {
        case Some(r) if r.length < 10 => Left(new InvalidLengthException(dataId))
        case Some(r) if !r.contains("ABC") => Left(new InvalidContentException(dataId))
        case r => Right(r)
      }
    } catch { case NonFatal(ex) => Left(ex) }
  }
}
