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
    Some(TaxpayerName(Some("mr"),Some("mickey"),None,Some("mouse"))),
    "29th April 2019"
  )

  private val metadataWithPaddedTaxpayerName = MessageMetadata(
    "",
    TaxEntity("",TaxIdWithName("",""),None),
    "",
    MetadataDetails(None,None,None),
    Some(TaxpayerName(Some(" mr "),Some(" mickey    "),None,Some("  mouse  "))),
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

  "ReplyService.getDefaultHtml" should {

    "return auto-filled HTML" in {

      val expectedHtml = Html("<p>Dear Mr Mickey Mouse</p>" +
        "<p>Thank you for your message of 29th April 2019.</p>" +
        "<p>To recap your question, I think you\\'re asking for help with</p>" +
        "<p>I believe this answers your question and hope you are satisfied with the response.</p>" +
        "<p>If you think there is something important missing, use the link at the end of this message to find out how to contact HMRC.</p>" +
        "<p>Regards<br/>Minnie Mouse<br/>HMRC Digital Team</p>")

      val defaultHtml = replyService.getDefaultHtml(Some(metadataWithTaxpayerName), threadSize = 1,Name(Some("Minnie"),Some("Mouse")))
      defaultHtml shouldBe expectedHtml

      val defaultHtml2 = replyService.getDefaultHtml(Some(metadataWithPaddedTaxpayerName), threadSize = 1,Name(Some("Minnie"),Some("Mouse")))
      defaultHtml2 shouldBe expectedHtml

    }

    "return HTML with default values" in {

      val expectedHtml = Html("<p>Dear Customer</p>" +
        "<p>Thank you for your message of 29th April 2019.</p>" +
        "<p>To recap your question, I think you\\'re asking for help with</p>" +
        "<p>I believe this answers your question and hope you are satisfied with the response.</p>" +
        "<p>If you think there is something important missing, use the link at the end of this message to find out how to contact HMRC.</p>" +
        "<p>Regards<br/>HMRC Digital Team</p>")

      val defaultHtml = replyService.getDefaultHtml(Some(metadataWithoutTaxpayerName),threadSize = 1,Name(None,None))
      defaultHtml shouldBe expectedHtml
    }

    SharedMetricRegistries.clear()

  }

}
