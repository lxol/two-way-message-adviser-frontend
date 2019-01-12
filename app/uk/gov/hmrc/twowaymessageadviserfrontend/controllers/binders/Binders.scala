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

// import play.api.mvc.PathBindable
// import reactivemongo.bson.BSONObjectID

// import scala.util.{Failure, Success}

// object Binders {

//   implicit def bsonIdBinder(implicit stringBinder: PathBindable[String]) = new PathBindable[BSONObjectID] {
//     def bind(key: String, value: String): Either[String, BSONObjectID] = stringBinder.bind(key,value) match {
//       case Left(msg) => Left(msg)
//       case Right(id) => BSONObjectID.parse(id) match {
//         case Success(boid) => Right(boid)
//         case Failure(_) => Left(s"ID $id was invalid")
//       }
//     }

//     def unbind(key: String, value: BSONObjectID): String = value.stringify
//   }
// }
// package uk.gov.hmrc.entityresolver.binders

import play.api.Play
import play.api.mvc.QueryStringBindable
import reactivemongo.bson.BSONObjectID
import play.api.mvc.PathBindable
import scala.util.{Failure, Success}
// import uk.gov.hmrc.domain.Nino
// import uk.gov.hmrc.domain.SaUtr
// import uk.gov.hmrc.entityresolver.models.EntityId

import scala.util.Try

object Binders {
  // implicit val saUtrBinder = new SimpleObjectBinder[SaUtr](SaUtr.apply, _.value)
  // implicit val ninoBinder = new SimpleObjectBinder[Nino](Nino.apply, _.value)
  // implicit val entityIdBinder = new SimpleObjectBinder[EntityId](EntityId.apply, _.value)
  // implicit val pathMappingBinder = new PathMappingBinder(Play.current.configuration)
  
  implicit val BSONObjectIdBinder = new QueryStringBindable[BSONObjectID] {
    def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, BSONObjectID]] = Try {
      params.get(key).flatMap(_.headOption).map(value => Right(BSONObjectID(value)))
    } recover {
      case e: Exception => Some(Left(s"Cannot parse parameter '$key' with parameters '$params' as 'BSONObjectID'"))
    } get

    def unbind(key: String, value: BSONObjectID): String = QueryStringBindable.bindableString.unbind(key, value.stringify)
  }

  implicit def bsonIdBinder(implicit stringBinder: PathBindable[String]) = new PathBindable[BSONObjectID] {
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
