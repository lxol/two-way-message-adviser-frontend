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

import javax.inject.Inject
import play.api.{Configuration, Environment, Logger}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import play.twirl.api.Html
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.auth.core.retrieve.Retrievals
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.twowaymessageadviserfrontend.config.FrontendAppConfig
import uk.gov.hmrc.twowaymessageadviserfrontend.connectors.TwoWayMessageConnector
import uk.gov.hmrc.twowaymessageadviserfrontend.controllers.util.StrideUtil
import uk.gov.hmrc.twowaymessageadviserfrontend.forms.ReplyFormProvider
import uk.gov.hmrc.twowaymessageadviserfrontend.models.ReplyDetails
import uk.gov.hmrc.twowaymessageadviserfrontend.services.ReplyService
import uk.gov.hmrc.twowaymessageadviserfrontend.views

import scala.concurrent.ExecutionContext

class ReplyController @Inject()(appConfig: FrontendAppConfig,
                                override val messagesApi: MessagesApi,
                                formProvider: ReplyFormProvider,
                                val config: Configuration,
                                val env: Environment,
                                val authConnector: AuthConnector,
                                val strideUtil: StrideUtil,
                                val twoWayMessageConnector: TwoWayMessageConnector,
                                val replyService: ReplyService)(implicit ec:ExecutionContext)
  extends FrontendController with I18nSupport with AuthorisedFunctions {

  val form: Form[ReplyDetails] = formProvider()

  def onPageLoad(id: BSONObjectID): Action[AnyContent] = Action.async {
    implicit request =>
      authorised(AuthProviders(PrivilegedApplication)).retrieve(Retrievals.name) { name =>
        for {
          customerId <- twoWayMessageConnector.getCustomerIdentifier(id.stringify)
          partial <- twoWayMessageConnector.getConversationPartial(id.stringify)
          threadSize <- twoWayMessageConnector.getMessageListSize(id.stringify)
          messageMetadata <- replyService.getMessageMetadata(id.stringify)
        } yield Ok(views.html.reply(appConfig, form, id, customerId, Some(replyService.getDefaultHtml(messageMetadata, threadSize, name)), partial)).withHeaders(CACHE_CONTROL -> "no-cache")
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
                partial <- twoWayMessageConnector.getConversationPartial(id.stringify)
            } yield { BadRequest( views.html.reply(
                            appConfig, formWithErrors, id,
                            formWithErrors.data("identifier"),
                            Some(Html(formWithErrors.data("adviser-reply").replaceAll("[\n\r]",""))), partial))},
        replyDetails => {
          Logger.debug(s"replyDetails: $replyDetails")
          twoWayMessageConnector.postMessage(replyDetails, id.stringify).map {_=>
            Redirect(routes.ReplyFeedbackSuccessController.onPageLoad(id))
          }
        }
      )
      }.recoverWith {
        case _: NoActiveSession => strideUtil.redirectToStrideLogin()
        case _: UnsupportedAuthProvider => strideUtil.redirectToStrideLogin()
      }
  }

}
