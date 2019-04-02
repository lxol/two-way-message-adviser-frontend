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

package uk.gov.hmrc.twowaymessageadviserfrontend.services

import com.google.inject.Inject
import play.twirl.api.Html
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.twowaymessageadviserfrontend.connectors.TwoWayMessageConnector
import uk.gov.hmrc.twowaymessageadviserfrontend.model.Message
import uk.gov.hmrc.twowaymessageadviserfrontend.views.html.thread_messages

import scala.concurrent.{ExecutionContext, Future}


class MessageRenderingServiceImpl @Inject()(twoWayMessageConnector: TwoWayMessageConnector)
                                        (implicit  ec: ExecutionContext) extends MessageRenderingService {

    override def renderMessages(messageId: String)(implicit hc:HeaderCarrier): Future[Html] = {
        for {
            messages <- twoWayMessageConnector.getMessages(messageId)
        } yield {
               thread_messages(Some("asdfa"))
        }
        Future.successful(
            thread_messages(Some("asdfasdf"))
        )
    }

}
