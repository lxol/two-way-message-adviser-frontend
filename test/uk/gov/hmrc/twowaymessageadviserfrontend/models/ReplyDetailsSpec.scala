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

package uk.gov.hmrc.twowaymessageadviserfrontend.models

import org.scalatest.{Matchers, WordSpec}

class ReplyDetailsSpec extends WordSpec with Matchers {

  val exampleInput = <p>test</p><ul><li>test</li><li>test</li></ul><p>test</p><ol><li>test</li><li>test</li></ol>.mkString

  val expectedOutput = <p>test</p><ul class="list list-bullet"><li>test</li><li>test</li></ul><p>test</p><ol class="list list-number"><li>test</li><li>test</li></ol>.mkString

  val topic: String = "some-topic"

  "ReplyFormProvider.getContent" should {

    "add list classes to" in {
      val reply = ReplyDetailsOptionalTopic(exampleInput, None, "", 1)
      reply.getContent shouldEqual expectedOutput
    }
  }

  "ReplyForProvider which contains a topic" should {
    "creation of a reply including a topic" in {
      val reply = ReplyDetailsOptionalTopic(exampleInput, Some(topic), "", 1)
      reply.topic.get shouldEqual topic
    }
  }
}
