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

package uk.gov.hmrc.twowaymessageadviserfrontend.controllers


import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers._
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.auth.core.retrieve.{Name, Retrievals}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.twowaymessageadviserfrontend.base.SpecBase
import uk.gov.hmrc.twowaymessageadviserfrontend.connectors.TwoWayMessageConnector
import uk.gov.hmrc.twowaymessageadviserfrontend.connectors.mocks.{MockAuthConnector, MockTwoWayMessageConnector}
import uk.gov.hmrc.twowaymessageadviserfrontend.models.MessageMetadata
import uk.gov.hmrc.twowaymessageadviserfrontend.services.ReplyService

import scala.concurrent.Future

class ReplyControllerSpec extends SpecBase with MockAuthConnector with MockTwoWayMessageConnector {

  private val ID: BSONObjectID = BSONObjectID.parse("5c18eb166f0000110204b160").get

  private val mockReplyService = mock[ReplyService]
  private val mockMessageMetadata = mock[MessageMetadata]

  override val injector = new GuiceApplicationBuilder()
      .configure(Configuration("metrics.enabled" -> false))
    .overrides(new AbstractModule with ScalaModule {
      override def configure(): Unit = {
        bind[AuthConnector].toInstance(mockAuthConnector)
        bind[TwoWayMessageConnector].toInstance(mockTwoWayMessageConnector)
        bind[ReplyService].toInstance(mockReplyService)
      }
    })
      .injector()

  implicit val hc: HeaderCarrier = mock[HeaderCarrier]
  val controller = injector.instanceOf[ReplyController]
  val fakeReplyRequest = FakeRequest(routes.ReplyController.onSubmit(ID))

  "On page load" should {

    "Given request to onload when request does not have authorization then expect stride redirect" in {
      mockAuthorise(AuthProviders(PrivilegedApplication),Retrievals.name)(Future.failed(UnsupportedAuthProvider()))
      val result = await(call(controller.onPageLoad(ID), fakeRequest))
      result.header.status mustBe 303
      result.header.headers.get("Location") mustBe Some("/stride/sign-in?successURL=http%3A%2F%2F%2F&origin=two-way-message-adviser-frontend")
    }

    "Given request to onload when request is authorised, return reply screen with original customer message" in {
      mockAuthorise(AuthProviders(PrivilegedApplication),Retrievals.name)(Future.successful(Name(Some("TestUser"),None)))
      mockSuccessfulMetadata(ID.stringify)(hc)
      mockSuccessfulConversationPartial(ID.stringify)(hc)
      mockSuccessfulMessageListSize(ID.stringify)(hc)
      when(mockReplyService.getMessageMetadata(any[String])(any[HeaderCarrier])).thenReturn(Future.successful(Some(mockMessageMetadata)))
      val result = call(controller.onPageLoad(ID), fakeRequest)

      contentAsString(result) contains "<h1 class=\"heading-large\">Reply to a secure question</h1>"
      contentAsString(result) contains s"$messagePartial"
    }
  }

  "On submit" should {
    "Given request to submit when request does not have authorization then expect stride redirect" in {
      mockAuthorise(AuthProviders(PrivilegedApplication))(Future.failed(UnsupportedAuthProvider()))
      val result = await(call(controller.onSubmit(ID), fakeRequest))
      result.header.status mustBe 303
      result.header.headers.get("Location") mustBe Some("/stride/sign-in?successURL=http%3A%2F%2F%2F&origin=two-way-message-adviser-frontend")
    }

    "Given authorised request with well formed form - then expect success" in {
      val goodRequestWithFormData: FakeRequest[AnyContentAsFormUrlEncoded] =
        fakeReplyRequest
          .withFormUrlEncodedBody("adviser-reply" -> "content " * 50, "identifier" -> "P800")
      mockSuccessfulMessagePartial(ID.stringify)(hc)
      mockAuthorise(AuthProviders(PrivilegedApplication))(Future.successful(Some("")))
      mockPostMessage(ID.stringify)(hc)

      val result = await(call(controller.onSubmit(ID), goodRequestWithFormData))
      result.header.status mustBe 303
      result.header.headers.get("Location") mustBe Some(s"/two-way-message-adviser-frontend/message/submitted?id=${ID.stringify}")

    }

    "Given authorised request with badly formed form - then expect error and original customer message" in {
      val badRequestWithFormData: FakeRequest[AnyContentAsFormUrlEncoded] =
        fakeReplyRequest
          .withFormUrlEncodedBody("adviser-reply" -> "not enough content", "identifier" -> "p800")
      mockAuthorise(AuthProviders(PrivilegedApplication))(Future.successful(Some("")))
      mockSuccessfulConversationPartial(ID.stringify)(hc)
      mockPostMessage(ID.stringify)(hc)

      val result = call(controller.onSubmit(ID), badRequestWithFormData)
      contentAsString(result) contains "<a href=\"#content\">Minimum length is 100</a>"
      contentAsString(result) contains s"${messagePartial}"
    }
  }
}
