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
package uk.gov.hmrc.twowaymessageadviserfrontend

import org.scalatest.{Matchers, WordSpec}
import play.api.libs.ws.WSClient
import uk.gov.hmrc.integration.ServiceSpec

class IntegrationTests extends WordSpec with Matchers with ServiceSpec  {

  def externalServices: Seq[String] = Seq("datastream")


  "Two way message adviser frontend" should {

    "Redirected with no auth on index" in {
      val wsClient = app.injector.instanceOf[WSClient]

      val response = wsClient.url(resource("/two-way-message-adviser-frontend")).get.futureValue

      response.body contains "<title>Stride IdP Login</title>"
    }

    "Redirected with no auth on reply" in {
      val wsClient = app.injector.instanceOf[WSClient]

      val response = wsClient.url(resource("/two-way-message-adviser-frontend/message/1234/reply")).get.futureValue

      response.body contains "<title>Stride IdP Login</title>"
    }

    "Access to index when authed" in {
      val wsClient = app.injector.instanceOf[WSClient]

      val response = wsClient.url(resource("/two-way-message-adviser-frontend"))
          .withHeaders(AuthUtil.buildStrideToken())
          .get
          .futureValue

      response.body contains "<title>Stride IdP Login</title>"

    }

    "Access to index reply when authed but get 400 due to incorrect message id" in {
      val wsClient = app.injector.instanceOf[WSClient]

      val response = wsClient.url(resource("/two-way-message-adviser-frontend/message/1234/reply"))
        .withHeaders(AuthUtil.buildStrideToken())
        .get
        .futureValue

      response.status shouldBe 400

    }
  }

  object AuthUtil {
    val httpClient = app.injector.instanceOf[WSClient]
    lazy val authPort = 8500
    lazy val ggAuthPort =  externalServicePorts.get("auth-login-api").get

    private val STRIDE_USER_PAYLOAD =
      """
        | {
        |  "clientId" : "id",
        |  "enrolments" : [],
        |  "ttl": 1200
        | }
      """.stripMargin


    def buildStrideToken(): (String, String) = {
      val response = httpClient.url(s"http://localhost:$authPort/auth/sessions")
        .withHeaders(("Content-Type", "application/json"))
        .post(STRIDE_USER_PAYLOAD).futureValue

      ("Authorization", response.header("Authorization").get)
    }

  }
}
