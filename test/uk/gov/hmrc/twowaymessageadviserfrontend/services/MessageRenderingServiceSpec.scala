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

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.twowaymessageadviserfrontend.assets.Fixtures
import uk.gov.hmrc.twowaymessageadviserfrontend.connectors.TwoWayMessageConnector
import uk.gov.hmrc.twowaymessageadviserfrontend.model.Message
import uk.gov.hmrc.twowaymessageadviserfrontend.model.MessageFormat._
import org.mockito.Mockito.when

import org.mockito.Mockito._

import scala.concurrent.{ExecutionContext, Future}

class MessageRenderingServiceSpec extends WordSpec  with MockitoSugar with  Fixtures with Matchers  {

  implicit val mockHeaderCarrier = mock[HeaderCarrier]
  val mockMessageConnector = mock[TwoWayMessageConnector]

  lazy val mockhttpClient = mock[HttpClient]

  val injector = new GuiceApplicationBuilder()
    .overrides(bind[TwoWayMessageConnector].to(mockMessageConnector))
    .injector()
    val messageRenderingService = injector.instanceOf[MessageRenderingService]


    "MessageRenderingService.renderMessage" should {
"render one message " in {

            val messages = List(Json.parse(messageString("1234")).validate[Message].get)
            when(mockMessageConnector.getMessages("12345"))
                .thenReturn(Future.successful(messages))

            ScalaFutures.whenReady( messageRenderingService.renderMessages("12345")) {
                result => result should be("<h1>")

            }
           //result should be("<asdfasdf")
        }
    }

}
