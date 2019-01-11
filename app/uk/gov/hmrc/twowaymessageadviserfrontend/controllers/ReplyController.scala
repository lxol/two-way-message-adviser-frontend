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

import javax.inject.Inject

import play.api.{Configuration, Logger}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import config.FrontendAppConfig
import forms.ReplyFormProvider

import reactivemongo.bson.BSONObjectID
//import utils.{Navigator, UserAnswers}
// import views.html.reply
import uk.gov.hmrc.twowaymessageadviserfrontend.views
// import views.reply

import play.api.mvc.{Action, AnyContent}

import scala.concurrent.Future
import utils.InputOption

import models.ReplyDetails

class ReplyController @Inject()(appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  formProvider: ReplyFormProvider) extends FrontendController with I18nSupport {

      def options: Seq[InputOption] = Seq(
        InputOption("queue1", "reply.dropdown.p1", Some("vat_vat-form")),
        InputOption("queue2", "reply.dropdown.p2", None),
        InputOption("queue99", "reply.dropdown.p3", None)
      )
  val form: Form[ReplyDetails] = formProvider(options)

  def onPageLoad(id: BSONObjectID) = //(identify andThen getData andThen requireData)
    Action {
      implicit request =>
      // val preparedForm = request.userAnswers.takingOverBusiness match {
      //   case None => form
      //   case Some(value) => form.fill(value)
      // }




      // val preparedForm = form.fill(options)
      Ok(views.html.reply(appConfig, form, options))
    }

  def onSubmit() = Action.async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest("asdfasdf")),

        (replyDetails) => {

          Logger.debug(s"replyDetails: ${replyDetails}")
          // val dataToAudit = ExitSurvey.asMap(surveyData)
          // val event = new ExitSurveyAuditEvent(dataToAudit)

          Future.successful(Redirect(routes.ReplyFeedbackSuccessController.onPageLoad()))
        }
      )
  }
  // def onSubmit() = //(identify andThen getData andThen requireData).async
  //   Action {
  //     implicit request =>
  //     // form.bindFromRequest().fold(
  //     //   (formWithErrors: Form[_]) =>
  //     //     Future.successful(BadRequest(takingOverBusiness(appConfig, formWithErrors, NormalMode))),
  //     //   (value) =>
  //     //     dataCacheConnector.save[Boolean](request.internalId, TakingO

  //     //verBusinessId.toString, value).map(cacheMap =>
  //     //       Redirect(navigator.nextPage(TakingOverBusinessId, NormalMode)(new UserAnswers(cacheMap))))
  //     // )
  //     ???
  //   }
}
