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

package uk.gov.hmrc.twowaymessageadviserfrontend.connectors.mocks

import org.mockito.ArgumentMatchers.{any, eq => equalsMock}
import org.mockito.Mockito.{reset, when}
import org.scalatest.{BeforeAndAfterEach, Suite}
import org.scalatest.mockito.MockitoSugar
import play.api.http.Status
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.partials.HtmlPartial
import uk.gov.hmrc.twowaymessageadviserfrontend.connectors.TwoWayMessageConnector
import uk.gov.hmrc.twowaymessageadviserfrontend.models.{ReplyDetails, ReplyDetailsOptionalTopic}

import scala.concurrent.Future

trait MockTwoWayMessageConnector extends BeforeAndAfterEach with MockitoSugar {
  self: Suite =>

  val mockTwoWayMessageConnector: TwoWayMessageConnector = mock[TwoWayMessageConnector]

  def mockSuccessfulMetadata(id: String)(hc: HeaderCarrier): Unit = {
    when(mockTwoWayMessageConnector.getCustomerIdentifier(equalsMock(id))(any[HeaderCarrier])).thenReturn(Future.successful("AB123450"))
  }

  val messageContent = "<h>FakePartial</h>"

  val response = HttpResponse(responseStatus = Status.OK, responseString = Some(messageContent))

  val messagePartial: HtmlPartial = HtmlPartial.readsPartial.read("someMethod", "someUrl", response)

  def mockSuccessfulMessagePartial(id: String)(hc: HeaderCarrier): Unit = {
    when(mockTwoWayMessageConnector.getMessagePartial(equalsMock(id))(any[HeaderCarrier])).thenReturn(Future.successful(messagePartial))
  }

  def mockSuccessfulConversationPartial(id: String)(hc: HeaderCarrier): Unit = {
    when(mockTwoWayMessageConnector.getConversationPartial(equalsMock(id))(any[HeaderCarrier])).thenReturn(Future.successful(messagePartial))
  }

  def mockSuccessfulMessageListSize(id:String)(hc:HeaderCarrier): Unit = {
    when(mockTwoWayMessageConnector.getMessageListSize(equalsMock(id))(any[HeaderCarrier])).thenReturn(Future.successful(1))
  }

  def mockPostMessage(id: String)(hc:HeaderCarrier): Unit = {
    when(mockTwoWayMessageConnector.postMessage(any[ReplyDetailsOptionalTopic], equalsMock(id))(any[HeaderCarrier]))
      .thenReturn(Future.successful(mock[HttpResponse]))
  }

  def mock(replyDetails: ReplyDetails, id: String): Unit = {
    ()
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockTwoWayMessageConnector)
  }
}
