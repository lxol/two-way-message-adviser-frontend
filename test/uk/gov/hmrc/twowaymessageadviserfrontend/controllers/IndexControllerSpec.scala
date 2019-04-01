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
import controllers.{ControllerSpecBase, IndexController}
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.auth.core.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.auth.core.{AuthConnector, AuthProviders, UnsupportedAuthProvider}
import uk.gov.hmrc.twowaymessageadviserfrontend.connectors.mocks.MockAuthConnector
import play.api.test.Helpers._

import scala.concurrent.Future

class IndexControllerSpec extends ControllerSpecBase with MockAuthConnector {

  override val injector = new GuiceApplicationBuilder()
    .configure(Configuration("metrics.enabled" -> false))
    .overrides(new AbstractModule with ScalaModule {
      override def configure(): Unit = {
        bind[AuthConnector].toInstance(mockAuthConnector)
      }
    })
    .injector()

  val indexController = injector.instanceOf[IndexController]

  "Index controller" should {
    "Given request when missing stride auth then request is redirected" in {
      mockAuthorise(AuthProviders(PrivilegedApplication))(Future.failed(UnsupportedAuthProvider()))
      val result = await(call(indexController.onPageLoad(), fakeRequest))
      result.header.status mustBe 303
      result.header.headers.get("Location") mustBe Some("/stride/sign-in?successURL=http%3A%2F%2F%2F&origin=two-way-message-adviser-frontend")
    }

    "Given request when stride auth is present then request is successful" in {
      mockAuthorise(AuthProviders(PrivilegedApplication))(Future.successful(Some("")))
      val result = await(call(indexController.onPageLoad(), fakeRequest))
      result.header.status mustBe 200
    }
  }

}