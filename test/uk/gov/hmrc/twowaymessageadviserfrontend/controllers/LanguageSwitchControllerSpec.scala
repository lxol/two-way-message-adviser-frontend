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
