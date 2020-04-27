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

package uk.gov.hmrc.twowaymessageadviserfrontend.controllers.util

import javax.inject.Inject
import play.api.mvc.{ AnyContent, Request, Result }
import play.api.{ Configuration, Environment }
import uk.gov.hmrc.play.bootstrap.config.AuthRedirects

import scala.concurrent.Future

class StrideUtil @Inject()(val env: Environment, val config: Configuration) extends AuthRedirects {

  def redirectToStrideLogin()(implicit request: Request[AnyContent]): Future[Result] =
    config.getBoolean(path = "includeHostInRedirect") match {
      case Some(true) => Future.successful(toStrideLogin(successUrl = s"http://${request.host}${request.uri}"))
      case _          => Future.successful(toStrideLogin(request.uri))
    }
}
