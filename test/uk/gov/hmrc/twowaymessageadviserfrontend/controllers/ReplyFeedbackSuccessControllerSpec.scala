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

package controllers

import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import reactivemongo.bson.BSONObjectID

import scala.concurrent.ExecutionContext.Implicits.global

class ReplyFeedbackSuccessControllerSpec extends ControllerSpecBase {

  override val injector = new GuiceApplicationBuilder()
    .configure(Configuration("metrics.enabled" -> false))
    .injector()

  val replyFeedbackController = injector.instanceOf[ReplyFeedbackSuccessController]
  private val ID: BSONObjectID = BSONObjectID.parse("5c18eb166f0000110204b160").get

  "Reply success controller" should {
    "on page load" in {
      val result = call(replyFeedbackController.onPageLoad(ID), fakeRequest)
      contentAsString(result) contains "<h1 class=\"heading-xlarge\">Message Sent</h1>"
    }
  }
}
