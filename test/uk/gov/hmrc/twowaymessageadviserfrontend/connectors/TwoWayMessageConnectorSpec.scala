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

package connectors

import base.SpecBase
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import org.mockito.ArgumentMatchers.{any, _}
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import play.api.Mode.Mode
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.{Application, Mode}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.twowaymessageadviserfrontend.assets.Fixtures
import uk.gov.hmrc.twowaymessageadviserfrontend.connectors.TwoWayMessageConnector

import scala.concurrent.{ExecutionContext, Future}

import uk.gov.hmrc.twowaymessageadviserfrontend.model.Message
import uk.gov.hmrc.twowaymessageadviserfrontend.model.MessageFormat._

class TwoWayMessageConnectorSpec extends SpecBase with MockitoSugar with Fixtures  {

  lazy implicit val hc = new HeaderCarrier()
  lazy val mockHttpClient = mock[HttpClient]

  override def fakeApplication(): Application = {

    new GuiceApplicationBuilder()
      .overrides(new AbstractModule with ScalaModule {
        override def configure(): Unit = {
          bind[Mode].toInstance(Mode.Test)
          bind[HttpClient].toInstance(mockHttpClient)
        }
      })
      .build()
  }

  val twoWayMessageConnector = injector.instanceOf[TwoWayMessageConnector]

  "twoWayMessageConnector.getMessages" should {

    "respond with body of the successfull request to the two-way-message microservice" in {
      val messageId = "1234567890"
      val messagesStr = v3Messages("123", "321")
      val messages:List[Message] = Json.parse(messagesStr).validate[List[Message]].get
      when(mockHttpClient.GET(endsWith(s"/message/messages-list/${messageId}"))
        (rds = any[HttpReads[HttpResponse]], hc = any[HeaderCarrier], ec = any[ExecutionContext]))
        .thenReturn(Future.successful(HttpResponse(200, Some(Json.parse(messagesStr)), Map.empty, None)))

      val result:List[Message] = await(twoWayMessageConnector.getMessages(messageId))
      result mustBe(messages)
    }
  }
}
