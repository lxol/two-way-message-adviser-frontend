import play.sbt.routes.RoutesKeys
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

val appName = "two-way-message-adviser-frontend"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, SbtAutoBuildPlugin, SbtDistributablesPlugin, SbtArtifactory)
  .settings(DefaultBuildSettings.scalaSettings: _*)
  .settings(DefaultBuildSettings.defaultSettings(): _*)
  .settings(SbtDistributablesPlugin.publishingSettings: _*)
  .settings(majorVersion := 0)
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(
    name := appName,
    RoutesKeys.routesImport += "models._",
    PlayKeys.playDefaultPort := 9000,
    ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*filters.*;.*handlers.*;.*components.*;.*repositories.*;" +
      ".*BuildInfo.*;.*javascript.*;.*FrontendAuditConnector.*;.*Routes.*;.*GuiceInjector;" +
      ".*ControllerConfiguration;.*LanguageSwitchController",
    ScoverageKeys.coverageMinimum := 0,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true,
    scalacOptions ++= Seq("-Xfatal-warnings", "-feature"),
    routesImport += "uk.gov.hmrc.twowaymessageadviserfrontend.controllers.binders.Binders._",
    libraryDependencies              ++= AppDependencies.compile ++ AppDependencies.test,

    retrieveManaged := true,
    evictionWarningOptions in update :=
      EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    fork in Test := true,
    resolvers ++= Seq(
      Resolver.bintrayRepo("hmrc", "releases"),
      Resolver.jcenterRepo
    ),
    // concatenate js
    Concat.groups := Seq(
      "javascripts/twowaymessageadviserfrontend-app.js" ->
        group(Seq("javascripts/show-hide-content.js", "javascripts/twowaymessageadviserfrontend.js"))
    ),
    // prevent removal of unused code which generates warning errors due to use of third-party libs
    UglifyKeys.compressOptions := Seq("unused=false", "dead_code=false"),
    pipelineStages := Seq(digest),
    // below line required to force asset pipeline to operate in dev rather than only prod
    pipelineStages in Assets := Seq(concat,uglify),
    // only compress files generated by concat
    includeFilter in uglify := GlobFilter("twowaymessageadviserfrontend-*.js")
  )
