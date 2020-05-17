package com.dnatic.exception

import scala.util.control.NoStackTrace

case class InvalidContentException(key: String) extends RuntimeException(s"Invalid result for dataId ($key). Result content must contain ABC.") with NoStackTrace
