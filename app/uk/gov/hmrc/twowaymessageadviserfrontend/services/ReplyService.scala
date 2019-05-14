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

package uk.gov.hmrc.twowaymessageadviserfrontend.services

import com.google.inject.Inject
import play.api.http.Status.OK
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.Json
import play.twirl.api.Html
import uk.gov.hmrc.auth.core.retrieve.Name
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.twowaymessageadviserfrontend.connectors.TwoWayMessageConnector
import uk.gov.hmrc.twowaymessageadviserfrontend.models.MessageMetadata
import uk.gov.hmrc.twowaymessageadviserfrontend.models.MessageMetadataFormat._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random
import scala.xml.NodeBuffer

class ReplyService @Inject()(override val messagesApi: MessagesApi, twoWayMessageConnector: TwoWayMessageConnector)
                            (implicit ec: ExecutionContext) extends I18nSupport {

  val FIRST_CUSTOMER_MESSAGE = 1
  val SECOND_CUSTOMER_MESSAGE = 3
  val THIRD_CUSTOMER_MESSAGE = 5

  /**
    * Retrieve the message metadata
    */
  def getMessageMetadata(messageId: String)(implicit hc: HeaderCarrier): Future[Option[MessageMetadata]] = {
    twoWayMessageConnector.getMessageMetadata(messageId).flatMap(  response =>
      response.status match {
        case OK =>
          val metadata = Json.parse(response.body).validate[MessageMetadata]
          Future.successful(metadata.asOpt)
        case _ => Future.successful(None)
      })
  }

  /**
    * Get the default text with inserted values to pre-populate the adviser reply message
    */
  def getDefaultText(maybeMetadata: Option[MessageMetadata], threadSize: Int, name: Name): String = {
    val textVersion = getReplyTextVersion(threadSize)
    getReplyInfo(maybeMetadata) match {
      case Some(replyInfo) =>
        s"""${Messages("reply.text.para.1",replyInfo.taxpayerName)}\n
           |${Messages("reply.text.para.2",replyInfo.messageDate)}\n
           |${Messages("reply.text.para.3." + textVersion)}\n
           |${Messages("reply.text.para.4")}\n
           |${Messages("reply.text.signature.1")}
           |${getAdviserName(name)}
           |${Messages("reply.text.signature.2")}""".stripMargin.replace("\\'","'")
      case None => ""
    }
  }

  /**
    * Get the default HTML with inserted values to pre-populate the adviser's reply
    */
  def getDefaultHtml(maybeMetadata: Option[MessageMetadata], threadSize: Int, name: Name): Html = {
    getReplyInfo(maybeMetadata) match {
      case Some(replyInfo) => Html(getMessagesHtml(replyInfo,threadSize,name).mkString)
      case None => Html("")
    }
  }

  private def getReplyTextVersion(threadSize: Int): Int = {
    threadSize match {
      case FIRST_CUSTOMER_MESSAGE => 1
      case SECOND_CUSTOMER_MESSAGE => 2
      case THIRD_CUSTOMER_MESSAGE => 3
      case _ => getRandom1to3
    }
  }

  private def getRandom1to3: Int = {
    val r = Random
    r.nextInt(3) + 1
  }

  private def getAdviserName(name: Name): String = {
    val firstName = name.name.getOrElse("")
    val lastName = name.lastName.getOrElse("")
    (firstName + " " + lastName).trim
  }

  private def getMessagesHtml(replyInfo: ReplyInfo, threadSize: Int, name: Name): NodeBuffer = {
    val textVersion = getReplyTextVersion(threadSize)
    val adviserName = getAdviserName(name)
    <p>{Messages("reply.text.para.1", replyInfo.taxpayerName)}</p>
      <p>{Messages("reply.text.para.2", replyInfo.messageDate)}</p>
      <p>{Messages("reply.text.para.3." + textVersion)}</p>
      <p>{Messages("reply.text.para.4")}</p>
      <p>{Messages("reply.text.signature.1")}<br/>{adviserName}{if(adviserName!=""){<br/>}}{Messages("reply.text.signature.2")}</p>
  }

  private def readMetadata(metadata: MessageMetadata): Option[ReplyInfo] = {
    metadata.taxpayerName.flatMap(taxpayerName => {
      val name = taxpayerName.toString()
      if(name.trim != "") {
        Some(ReplyInfo(name, metadata.messageDate, ""))
      } else {
        Some(ReplyInfo("Customer",metadata.messageDate,""))
      }
    })
  }

  private def getReplyInfo(maybeMetadata: Option[MessageMetadata]): Option[ReplyInfo] = {
    maybeMetadata.flatMap(metadata => readMetadata(metadata))
  }

  case class ReplyInfo(taxpayerName: String, messageDate: String, adviserName: String)

}