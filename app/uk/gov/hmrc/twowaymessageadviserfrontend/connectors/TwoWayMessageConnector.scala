/*
 * Copyright 2020 HM Revenue & Customs
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
import org.apache.commons.codec.binary.Base64
import play.api.{Configuration, Environment, Mode}
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.partials.HtmlPartial
import uk.gov.hmrc.play.partials.HtmlPartial.connectionExceptionsAsHtmlPartialFailure
import uk.gov.hmrc.twowaymessageadviserfrontend.models.{ReplyDetails, ReplyDetailsOptionalTopic}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration

class TwoWayMessageConnector @Inject()(httpClient: HttpClient,
                                       override val runModeConfiguration: Configuration,
                                       val environment: Environment)(implicit ec: ExecutionContext)
  extends ServicesConfig {

  override protected def mode: Mode.Mode = environment.mode

  lazy val twoWayMessageBaseUrl: String = baseUrl("two-way-message")

  /**
    * Posts the html content from the adviser reply text editor to the two-way-message service
    */
  def postMessage(reply: ReplyDetailsOptionalTopic, replyTo: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val encodedReply = reply.copy(content = Base64.encodeBase64String(reply.getContent.getBytes("UTF-8")))
    httpClient.POST(s"$twoWayMessageBaseUrl/two-way-message/message/adviser/$replyTo/reply", encodedReply)
  }

  /**
    * Gets the customer identifier for a specific message as a string from the two-way-message service
    */
  def getCustomerIdentifier(originalMessageId: String)(implicit hc: HeaderCarrier): Future[String] = {
    httpClient.GET[HttpResponse](s"$twoWayMessageBaseUrl/two-way-message/message/adviser/recipient-metadata/$originalMessageId")
      .map(e => {
        (Json.parse(e.body) \ "recipient" \ "identifier" \ "value").as[String]
      })
  }

  /**
    * Gets an HTML fragment rendering a single message by message ID from the two-way-message service
    */
  def getMessagePartial(messageId: String)(implicit hc: HeaderCarrier): Future[HtmlPartial] =
    httpClient.GET[HtmlPartial](url(s"/two-way-message/message/adviser/message-content/$messageId"))
      .recover {connectionExceptionsAsHtmlPartialFailure}

  /**
    * Gets the message metadata for a single message as JSON from the two-way-message service
    */
  def getMessageMetadata(messageId: String)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    httpClient.GET(s"$twoWayMessageBaseUrl/two-way-message/message/adviser/recipient-metadata/$messageId")

  /**
    * Gets the integer value of the number of messages in a conversation history from the two-way-message service
    */
  def getMessageListSize(messageId: String)(implicit hc: HeaderCarrier): Future[Int] = {
    httpClient.GET(s"$twoWayMessageBaseUrl/two-way-message/message/messages-list/$messageId/size").map(e => {
      Json.parse(e.body).as[Int]
    })
  }

  /**
    * Gets an HTML fragment rendering a message conversation history by the latest message ID from the two-way-message service
    */
  def getConversationPartial(messageId: String)(implicit hc: HeaderCarrier): Future[HtmlPartial] =
    httpClient.GET[HtmlPartial](s"$twoWayMessageBaseUrl/messages/$messageId/adviser-content")
    .recover {connectionExceptionsAsHtmlPartialFailure}

  private def url(path: String): String = baseUrl("two-way-message") + path
}
