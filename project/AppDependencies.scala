import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(

    "uk.gov.hmrc"             %% "govuk-template"           % "5.3.0",
    "uk.gov.hmrc"             %% "play-ui"                  % "7.27.0-play-25",
    // "uk.gov.hmrc"             %% "bootstrap-play-25"        % "4.6.0",
    "uk.gov.hmrc"             %% "bootstrap-play-25"        % "4.8.0",
    "uk.gov.hmrc"             %% "play-reactivemongo"       % "6.2.0",
    "uk.gov.hmrc"             %% "play-language"            % "3.4.0"
  )

  val test = Seq(
    "com.typesafe.play"       %% "play-test"                % current                 % "test",
    "com.typesafe.play" %% "play-test" % current % "test",
    "io.github.cloudify" %% "spdf" % "1.3.1" % "test",
    "org.jsoup"               %  "jsoup"                    % "1.10.2"                % "test",
    "org.mockito" % "mockito-core" % "2.23.4" % "test",
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % "test, it",
    "org.scalatest"           %% "scalatest"                % "3.0.5"                 % "test",
    "org.scalatest" %% "scalatest" % "3.0.5" % "test",
    "net.codingwell" %% "scala-guice" % "4.2.2" % "test",

    "uk.gov.hmrc" %% "hmrctest" % "3.2.0" % "test,it",

    "com.github.tomakehurst" % "wiremock-standalone" % "2.20.0" % "test,it",
    "io.netty" % "netty-buffer" % "4.0.56.Final"   % "test, it",
    "io.netty" % "netty-codec-http" % "4.0.56.Final"   % "test, it",
    "io.netty" % "netty-transport-native-epoll" % "4.0.56.Final"   % "test, it",
    "org.asynchttpclient" % "async-http-client" % "2.0.39"   % "test, it",
    "org.scalacheck" %% "scalacheck" % "1.14.0" % "test,it",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % "test,it",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "2.0.1"                 % "test, it",
    "uk.gov.hmrc"             %% "service-integration-test" % "0.2.0"                 % "test, it"
  )

}
