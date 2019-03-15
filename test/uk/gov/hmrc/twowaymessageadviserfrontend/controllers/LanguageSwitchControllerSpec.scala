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


import base.SpecBase
import controllers.LanguageSwitchController
import play.api.Configuration
import play.api.test.Helpers._
import play.api.test.FakeRequest

class LanguageSwitchControllerSpec extends SpecBase  {

  val config =  Configuration.empty

  "Hitting language selection endpoint" must {
    "redirect to Welsh translated start page if Welsh language is selected" in {
      val request = FakeRequest()
      val result = new LanguageSwitchController(config, frontendAppConfig, messagesApi).switchToLanguage("cymraeg")(request)
      header("Set-Cookie",result) mustBe Some("PLAY_LANG=cy; Path=/;;PLAY_FLASH=switching-language=true; Path=/; HTTPOnly")
    }

    "redirect to English translated start page if English language is selected" in {
      val request = FakeRequest()
      val result = new LanguageSwitchController(config, frontendAppConfig, messagesApi).switchToLanguage("english")(request)
      header("Set-Cookie",result) mustBe Some("PLAY_LANG=en; Path=/;;PLAY_FLASH=switching-language=true; Path=/; HTTPOnly")
    }
  }
}
