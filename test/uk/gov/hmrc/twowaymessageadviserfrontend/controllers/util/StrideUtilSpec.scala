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

package uk.gov.hmrc.twowaymessageadviserfrontend.controllers.util

import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import uk.gov.hmrc.twowaymessageadviserfrontend.base.SpecBase

class StrideUtilSpec extends SpecBase {

  override val injector = new GuiceApplicationBuilder()
    .configure(Configuration("metrics.enabled" -> false, "includeHostInRedirect" -> true))
    .injector()

  val strideUtl = injector.instanceOf[StrideUtil]

  "When configured to include host in redirect" should {
    "host is included in redirect" in {
      val a = await(strideUtl.redirectToStrideLogin())
      a.header.status mustBe 303
      a.header.headers.get("Location") mustBe Some(
        "/stride/sign-in?successURL=http%3A%2F%2F%2F&origin=two-way-message-adviser-frontend")
    }
  }
}

class IncludeHostRedirectStrideUtilSpec extends SpecBase {

  override val injector = new GuiceApplicationBuilder()
    .configure(Configuration("metrics.enabled" -> false, "includeHostInRedirect" -> false))
    .injector()

  val strideUtl = injector.instanceOf[StrideUtil]

  "When configured to not include host in redirect" should {

    "do not include host in redirect" in {
      val response = await(strideUtl.redirectToStrideLogin())
      response.header.status mustBe 303
      response.header.headers.get("Location") mustBe Some(
        "/stride/sign-in?successURL=%2F&origin=two-way-message-adviser-frontend")
    }
  }
}
