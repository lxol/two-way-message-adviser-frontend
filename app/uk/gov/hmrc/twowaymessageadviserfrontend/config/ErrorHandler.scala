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

package config

import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{Request, RequestHeader, Result}
import play.api.mvc.Results.{InternalServerError, NotFound}
import play.twirl.api.Html
import uk.gov.hmrc.auth.core.{AuthorisationException, BearerTokenExpired, MissingBearerToken, NoActiveSession}
import uk.gov.hmrc.http.{NotFoundException, Upstream5xxResponse}
import uk.gov.hmrc.play.bootstrap.http.FrontendErrorHandler
import uk.gov.hmrc.twowaymessageadviserfrontend.views

@Singleton
class ErrorHandler @Inject()(val messagesApi: MessagesApi, implicit val appConfig: FrontendAppConfig) extends FrontendErrorHandler {
  override def standardErrorTemplate(pageTitle: String, heading: String, message: String)(implicit request: Request[_]): Html =
    views.html.error_template(pageTitle, heading, message)

 override def badRequestTemplate(implicit request: Request[_]): Html = {
   val replyPattern = """(?<=two-way-message-adviser-frontend/message/)[a-zA-Z0-9]+(?=/reply)""".r
   val replyError = replyPattern.findFirstIn(request.path) match {
     case Some(replyId) => s"Invalid id: $replyId"
     case None => Messages("global.error.badRequest400.message")
   }
   standardErrorTemplate(
     Messages("global.error.badRequest400.title"),
     Messages("global.error.badRequest400.heading"),
     replyError)
 }

  override def resolveError(rh: RequestHeader, ex: Throwable): Result = {
    ex match {
      case _: NotFoundException =>
        NotFound(notFoundTemplate(Request(rh, "")))
      case _: Upstream5xxResponse =>
        // currently the two-way-message m/s converts any errors from the message m/s to 502 errors so any errors originating there will end up here
        InternalServerError(standardErrorTemplate("Error","There was an error: ",
          "We are unable to process your enquiry at this time. Please try again later.")(Request(rh, "")))
      case _ => super.resolveError(rh, ex)
    }
  }
}
