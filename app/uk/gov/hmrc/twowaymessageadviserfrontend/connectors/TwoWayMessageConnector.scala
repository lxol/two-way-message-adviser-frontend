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

package uk.gov.hmrc.twowaymessageadviserfrontend.connectors

import javax.inject.Inject

import models.ReplyDetails
import org.apache.commons.codec.binary.Base64
import play.api.libs.json.Json
import play.api.{Configuration, Environment}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.config.ServicesConfig

import scala.concurrent.{ExecutionContext, Future}

class TwoWayMessageConnector @Inject()(httpClient: HttpClient,
                                       override val runModeConfiguration: Configuration,
                                       val environment: Environment)(implicit ec: ExecutionContext)
  extends ServicesConfig {

  override protected def mode = environment.mode

  lazy val twoWayMessageBaseUrl: String = baseUrl("two-way-message")

  def postMessage(reply: ReplyDetails, replyTo: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val encodedReply = reply.copy(content = Base64.encodeBase64String(reply.content.getBytes("UTF-8")))
    httpClient.POST(s"$twoWayMessageBaseUrl/two-way-message/message/advisor/${replyTo}/reply", encodedReply)
  }

  def retrieveRecipientIdentifier(originalMessageId: String)(implicit hc: HeaderCarrier): Future[String] = {
    httpClient.GET(s"$twoWayMessageBaseUrl/two-way-message/message/adviser/recipient-metadata/$originalMessageId")(hc = hc.withExtraHeaders(("Content-Type", "application/json")), ec = ec, rds = HttpReads.readRaw)
      .map(e => {
        (Json.parse(e.body) \ "recipient" \ "identifier" \ "value").as[String]
      })
  }

  def getMessages(messageId: String)(implicit hc: HeaderCarrier): Future[HttpResponse] =
        httpClient.GET(s"${twoWayMessageBaseUrl}/message/messages-list/$messageId")
}
