/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.message.controllers.binders

import play.api.mvc.{PathBindable, QueryStringBindable}
import reactivemongo.bson.BSONObjectID

import scala.util.{Failure, Success}

object Binders {

  implicit val BSONObjectIdBinder = new QueryStringBindable[BSONObjectID] {
    def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, BSONObjectID]] =
      params.get(key).flatMap(_.headOption).map(value => BSONObjectID.parse(value) match {
        case Success(boid) => Some(Right(boid))
        case Failure(_) => Some(Left(s"ID $value was invalid"))
      }).getOrElse( Some(Left(s"Cannot parse parameter '$key' with parameters '$params' as 'BSONObjectID'")) )

    def unbind(key: String, value: BSONObjectID): String = QueryStringBindable.bindableString.unbind(key, value.stringify)
  }

  implicit def bsonIdBinder(implicit stringBinder: PathBindable[String]): PathBindable[BSONObjectID] = new PathBindable[BSONObjectID] {
    def bind(key: String, value: String): Either[String, BSONObjectID] = stringBinder.bind(key,value) match {
      case Left(msg) => Left(msg)
      case Right(id) => BSONObjectID.parse(id) match {
        case Success(boid) => Right(boid)
        case Failure(_) => Left(s"ID $id was invalid")
      }
    }

    def unbind(key: String, value: BSONObjectID): String = value.stringify
  }
}
