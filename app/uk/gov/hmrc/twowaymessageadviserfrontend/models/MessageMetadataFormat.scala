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

package uk.gov.hmrc.twowaymessageadviserfrontend.models

import play.api.libs.json.{Format, Json}

object MessageMetadataFormat {

  implicit val taxIdWithNameFormat: Format[TaxIdWithName] = Json.format[TaxIdWithName]

  implicit val taxEntityFormat: Format[TaxEntity] = Json.format[TaxEntity]

  implicit val adviserFormat: Format[Adviser] = Json.format[Adviser]

  implicit val metadataDetailsFormat: Format[MetadataDetails] = Json.format[MetadataDetails]

  implicit val taxpayerNameFormat: Format[TaxpayerName] = Json.format[TaxpayerName]

  implicit val messageMetadataFormat: Format[MessageMetadata] = Json.format[MessageMetadata]

}
case class TaxIdWithName(name: String, value: String)

case class TaxEntity(regime: String, identifier: TaxIdWithName, email: Option[String] = None)

case class Adviser(pidId: String)

case class MetadataDetails(threadId: Option[String], enquiryType: Option[String], adviser: Option[Adviser])

case class TaxpayerName(title: Option[String] = None, forename: Option[String] = None,
                        secondForename: Option[String] = None, surname: Option[String] = None, honours:
                        Option[String] = None, line1: Option[String] = None, line2: Option[String] = None,
                        line3: Option[String] = None ) {

  override def toString: String =
    s"""${title.getOrElse("")} ${forename.getOrElse("")} ${surname.getOrElse("")}"""
}

case class MessageMetadata(id: String, recipient: TaxEntity, subject: String, details: MetadataDetails,
                           taxpayerName: Option[TaxpayerName] = None, messageDate: String)