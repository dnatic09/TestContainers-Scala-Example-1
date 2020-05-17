package com.dnatic.exception

import scala.util.control.NoStackTrace

case class InvalidLengthException(key: String) extends RuntimeException(s"Invalid result for dataId ($key). Minimum length allowed is 10") with NoStackTrace
