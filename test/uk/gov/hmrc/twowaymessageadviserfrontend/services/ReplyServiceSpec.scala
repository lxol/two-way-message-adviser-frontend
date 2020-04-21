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
    TaxEntity("", TaxIdWithName("", ""), None),
    "",
    MetadataDetails(None, "p800", None),
    Some(TaxpayerName(Some("mr"), Some("mickey"), None, Some("mouse"))),
    "29th April 2019"
  )

  private val metadataWithPaddedTaxpayerName = MessageMetadata(
    "",
    TaxEntity("", TaxIdWithName("", ""), None),
    "",
    MetadataDetails(None, "p800", None),
    Some(
      TaxpayerName(Some(" mr "), Some(" mickey    "), None, Some("  mouse  "))
    ),
    "29th April 2019"
  )

  private val metadataWithoutTaxpayerName = MessageMetadata(
    "",
    TaxEntity("", TaxIdWithName("", ""), None),
    "",
    MetadataDetails(None, "p800", None),
    Some(TaxpayerName()),
    "29th April 2019"
  )

  "ReplyService.getDefaultHtml" should {

    "return auto-filled HTML" in {

      val expectedHtml = Html(
        "<p>Dear Customer</p>" +
          "<p>We recently spoke to you by phone, and believe we have now answered your question.</p>" +
          "<p>Because of this we have now closed your query.</p>" +
          "<p>Regards<br/>HMRC adviser team</p>"
      )

      val defaultHtml = replyService.getDefaultHtml(
        metadataWithTaxpayerName,
        threadSize = 1,
        Name(Some("Minnie"), Some("Mouse"))
      )
      defaultHtml shouldBe expectedHtml

      val defaultHtml2 = replyService.getDefaultHtml(
        metadataWithPaddedTaxpayerName,
        threadSize = 1,
        Name(Some("Minnie"), Some("Mouse"))
      )
      defaultHtml2 shouldBe expectedHtml

    }

    "return HTML with default values" in {

      val expectedHtml = Html(
        "<p>Dear Customer</p>" +
          "<p>We recently spoke to you by phone, and believe we have now answered your question.</p>" +
          "<p>Because of this we have now closed your query.</p>" +
          "<p>Regards<br/>HMRC adviser team</p>"
      )

      val defaultHtml = replyService.getDefaultHtml(
        metadataWithoutTaxpayerName,
        threadSize = 1,
        Name(None, None)
      )
      defaultHtml shouldBe expectedHtml
    }

    SharedMetricRegistries.clear()

  }

}
