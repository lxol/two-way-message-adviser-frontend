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
import play.api.{Configuration, Environment, Logger}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import play.api.routing.JavaScriptReverseRouter
import play.Routes
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.twowaymessageadviserfrontend.connectors.TwoWayMessageConnector
import uk.gov.hmrc.twowaymessageadviserfrontend.controllers.util.StrideUtil
import uk.gov.hmrc.twowaymessageadviserfrontend.forms.EditReplyFormProvider
import uk.gov.hmrc.twowaymessageadviserfrontend.models.{EditReplyDetails, ReplyDetails}
import uk.gov.hmrc.twowaymessageadviserfrontend.views

import scala.concurrent.ExecutionContext

class ReplyController @Inject()(appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  formProvider: ReplyFormProvider,
  editFormProvider: EditReplyFormProvider,
  val config: Configuration,
  val env: Environment,
  val authConnector: AuthConnector,
  val strideUtil: StrideUtil,
  val twoWayMessageConnector: TwoWayMessageConnector)(implicit ec:ExecutionContext) extends FrontendController with I18nSupport with AuthorisedFunctions {

  val form: Form[ReplyDetails] = formProvider()
  val editForm: Form[EditReplyDetails] = editFormProvider()

  def onPageLoad(id: BSONObjectID): Action[AnyContent] = Action.async {
    implicit request =>
      authorised(AuthProviders(PrivilegedApplication)) {
        for {
          identifier <- twoWayMessageConnector.retrieveRecipientIdentifier(id.stringify)
          partial <- twoWayMessageConnector.loadMessagePartial(id.stringify)
        } yield ( Ok(views.html.reply(appConfig, form, id, identifier, Option.empty, partial)))
      }.recoverWith {
        case _: NoActiveSession => strideUtil.redirectToStrideLogin()
        case _: UnsupportedAuthProvider => strideUtil.redirectToStrideLogin()
      }
  }

  def onPageLoadEdit(id: BSONObjectID): Action[AnyContent] = Action.async {
    implicit request =>
      authorised(AuthProviders(PrivilegedApplication)) {
        for {
          identifier <- twoWayMessageConnector.retrieveRecipientIdentifier(id.stringify)
          partial <- twoWayMessageConnector.loadMessagePartial(id.stringify)
        } yield ( Ok(views.html.reply_edit(appConfig, editForm, id, identifier, Option.empty, partial)))
      }.recoverWith {
        case _: NoActiveSession => strideUtil.redirectToStrideLogin()
        case _: UnsupportedAuthProvider => strideUtil.redirectToStrideLogin()
      }
  }

  def onSubmit(id: BSONObjectID): Action[AnyContent] = Action.async {
    implicit request =>
      authorised(AuthProviders(PrivilegedApplication)) {
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
            for {
                partial <- twoWayMessageConnector.loadMessagePartial(id.stringify)
            } yield { BadRequest( views.html.reply(
                            appConfig, formWithErrors, id,
                            formWithErrors.data.get("identifier").get,
                            formWithErrors.data.get("content"), partial))},
        (replyDetails) => {
          Logger.debug(s"replyDetails: ${replyDetails}")
          twoWayMessageConnector.postMessage(replyDetails, id.stringify).map {
            case _  => Redirect(routes.ReplyFeedbackSuccessController.onPageLoad(id))
          }
        }
      )
      }.recoverWith {
        case _: NoActiveSession => strideUtil.redirectToStrideLogin()
        case _: UnsupportedAuthProvider => strideUtil.redirectToStrideLogin()
      }
  }

  def javascriptRoutes: Action[AnyContent] = Action { implicit request =>
    Ok(JavaScriptReverseRouter("jsRoutes")(
      routes.javascript.Assets.versioned
    )).as("text/javascript")
  }
}
