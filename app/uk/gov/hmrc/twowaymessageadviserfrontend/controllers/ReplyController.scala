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

import config.FrontendAppConfig
import forms.ReplyFormProvider
import javax.inject.Inject
import models.ReplyDetails
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Action
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.twowaymessageadviserfrontend.views

import scala.concurrent.Future

class ReplyController @Inject()(appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  formProvider: ReplyFormProvider) extends FrontendController with I18nSupport {

  val form: Form[ReplyDetails] = formProvider()

  def onPageLoad(id: BSONObjectID) =
    Action {
      implicit request =>

      Ok(views.html.reply(appConfig, form, id))
    }

  def onSubmit(id: BSONObjectID) = Action.async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest("")),

        (replyDetails) => {

          Logger.debug(s"replyDetails: ${replyDetails}")

          Future.successful(Redirect(routes.ReplyFeedbackSuccessController.onPageLoad(id)))
        }
      )
  }
}
