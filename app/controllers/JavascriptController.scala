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

package controllers

import javax.inject.Inject
import play.api.mvc.{Action, AnyContent}
import play.api.routing.JavaScriptReverseRouter
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.twowaymessageadviserfrontend.controllers.util.StrideUtil

import scala.concurrent.{ExecutionContext, Future}

class JavascriptController @Inject()(val authConnector: AuthConnector, val strideUtil: StrideUtil)(implicit ec:ExecutionContext)
  extends FrontendController with AuthorisedFunctions {

  def javascriptRoutes: Action[AnyContent] = Action.async {
    implicit request =>
      authorised(AuthProviders(PrivilegedApplication)) {
        Future.successful(Ok(JavaScriptReverseRouter("jsRoutes")(
          routes.javascript.Assets.versioned
        )).as(contentType = "text/javascript"))
      }.recoverWith {
        case _: NoActiveSession => strideUtil.redirectToStrideLogin()
        case _: UnsupportedAuthProvider => strideUtil.redirectToStrideLogin()
      }
  }
}
