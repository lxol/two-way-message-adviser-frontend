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

package uk.gov.hmrc.twowaymessageadviserfrontend.forms

import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.twowaymessageadviserfrontend.forms.mappings.Mappings
import uk.gov.hmrc.twowaymessageadviserfrontend.models.{ReplyDetails, ReplyDetailsOptionalTopic}

class ReplyFormProviderWithoutTopic @Inject() extends Mappings {

  val minimumReplyCharacters = 100

  def apply(): Form[ReplyDetailsOptionalTopic] =
    Form(
      mapping(
        "adviser-reply" -> text(minLength = minimumReplyCharacters),
        "topic"         -> optional(text)
      )(ReplyDetailsOptionalTopic.apply)(ReplyDetailsOptionalTopic.unapply).verifying("Invalid content entered. Please try again removing any unusual characters or symbols.", reply =>
        reply.validate(reply.getContent))
    )
}
