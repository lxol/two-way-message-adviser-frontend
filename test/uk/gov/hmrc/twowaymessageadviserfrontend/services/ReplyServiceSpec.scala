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

import com.codahale.metrics.SharedMetricRegistries
import org.scalatest.{Matchers, WordSpec}
import play.api.inject.guice.GuiceApplicationBuilder
import play.twirl.api.Html
import uk.gov.hmrc.auth.core.retrieve.Name
import uk.gov.hmrc.twowaymessageadviserfrontend.models._

class ReplyServiceSpec extends WordSpec with Matchers {

  private val injector = new GuiceApplicationBuilder().injector()

  private val replyService = injector.instanceOf[ReplyService]

  private val metadataWithTaxpayerName = MessageMetadata(
    "",
    TaxEntity("",TaxIdWithName("",""),None),
    "",
    MetadataDetails(None,None,None),
    Some(TaxpayerName(Some("Mr"),Some("Mickey"),None,Some("Mouse"))),
    "29th April 2019"
  )

  private val metadataWithoutTaxpayerName = MessageMetadata(
    "",
    TaxEntity("",TaxIdWithName("",""),None),
    "",
    MetadataDetails(None,None,None),
    Some(TaxpayerName()),
    "29th April 2019"
  )

  "ReplyService.getDefaultText" should {

    "return auto-filled text" in {

      val expectedText = """Dear Mr Mickey Mouse
      |
      |Thank you for your message of 29th April 2019.
      |
      |To recap your question, I think you're asking for help with
      |
      |I believe this answers your question and hope you are satisfied with the response. There's no need to send a reply. But if you think there's something important missing, just ask another question about this below.
      |
      |Regards
      |Minnie Mouse
      |HMRC digital team""".stripMargin
      val defaultText = replyService.getDefaultText(Some(metadataWithTaxpayerName), threadSize = 1,Name(Some("Minnie"),Some("Mouse")))
      defaultText shouldBe expectedText
    }

    "return text with default values" in {

      val expectedText = """Dear Customer
      |
      |Thank you for your message of 29th April 2019.
      |
      |To recap your question, I think you're asking for help with
      |
      |I believe this answers your question and hope you are satisfied with the response. There's no need to send a reply. But if you think there's something important missing, just ask another question about this below.
      |
      |Regards
      |
      |HMRC digital team""".stripMargin
      val defaultText = replyService.getDefaultText(Some(metadataWithoutTaxpayerName),threadSize = 1,Name(None,None))
      defaultText shouldBe expectedText
    }
    SharedMetricRegistries.clear()
  }

  "ReplyService.getDefaultHtml" should {

    "return auto-filled HTML" in {

      val expectedHtml = Html("<p>Dear Mr Mickey Mouse</p>" +
        "<p>Thank you for your message of 29th April 2019.</p>" +
        "<p>To recap your question, I think you\\'re asking for help with</p>" +
        "<p>I believe this answers your question and hope you are satisfied with the response." +
        " There\\'s no need to send a reply. But if you think there\\'s something important missing, just ask another question about this below.</p>" +
        "<p>Regards<br/>Minnie Mouse<br/>HMRC digital team</p>")

      val defaultHtml = replyService.getDefaultHtml(Some(metadataWithTaxpayerName), threadSize = 1,Name(Some("Minnie"),Some("Mouse")))
      defaultHtml shouldBe expectedHtml

    }

    "return HTML with default values" in {

      val expectedHtml = Html("<p>Dear Customer</p>" +
        "<p>Thank you for your message of 29th April 2019.</p>" +
        "<p>To recap your question, I think you\\'re asking for help with</p>" +
        "<p>I believe this answers your question and hope you are satisfied with the response." +
        " There\\'s no need to send a reply. But if you think there\\'s something important missing, just ask another question about this below.</p>" +
        "<p>Regards<br/>HMRC digital team</p>")

      val defaultHtml = replyService.getDefaultHtml(Some(metadataWithoutTaxpayerName),threadSize = 1,Name(None,None))
      defaultHtml shouldBe expectedHtml
    }
  }

}
