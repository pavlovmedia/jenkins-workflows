def call(Closure call) {
  // Message no matter what, and throw the exception too!
  catchError {
    call()
    // If the build worked, set this so we get FIXED messages
    currentBuild.result = 'SUCCESS'
  }

  catchError {
    stage 'Email' {
      // Calculate email
      def remote = sh(returnStdout: true, script: 'git remote -v').trim()
      def matcher = (remote =~ "git@.+:(?<fork>.+)/.+\\(fetch\\)")
      def email = matcher.size() ? matcher[0][1] as String : 'dev'
      matcher = null // Since the job gets serialized

      echo "Sending email to ${email} if this is in error"
      step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: email, sendToIndividuals: true])
    }
  }

  catchError {
    stage("Notify slack") {
      def email = sh(returnStdout: true, script: 'git show -s --pretty=%aE').trim()
      def author = email.substring(0, email.indexOf('@'))
      def authorPretty = sh(returnStdout: true, script: 'git show -s --pretty=%aN').trim()
      def remote = sh(returnStdout: true, script: 'git ls-remote --get-url').trim()
      if (!currentBuild.result.equals("SUCCESS")) {
        slackSend(color:"danger", message:"@${author} <${email}> caused a build failure on branch ${env.BRANCH_NAME} of ${remote}. See ${currentBuild.absoluteUrl} for details")
      } else if (!currentBuild.previousBuild.result.equals("SUCCESS")) {
        slackSend(color:"good", message:"@${author} <${email}> fixed the build failure on branch ${env.BRANCH_NAME} of ${remote}. Thanks ${authorPretty}!")
      }
    }
  }
}