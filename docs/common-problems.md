# 1. Problems on Push

## `Rejected - non-fast-forward`

A team mate pushed something new to your team repository before you.

1. Commit your changes
2. Pull from your team repository
3. Resolve conflicts if needed
4. Commit and push

# 2. Problems on Pull

## `Checkout conflict` or `DIRTY_WORKTREE`

You have a local change that you did not commit. 

1. Two options:
   1. commit without pushing your local change, or 
   2. delete your local change by replacing the changed files/folders with a previous version (you can find the previous version of a file in the Timeline or Source Control Graph view in VSCode)
3. Pull from your team repository

This will maybe result in a conflict, if your team mate changed the same lines. 
In this case you will have to resolve the conflict before being able to commit your changes and push.

# 3. Problems on Pull Request

If the pull request is correct, it is merged automatically (within 5 minutes) and it is removed from the list of open pull requests in the main repository. If you still see your pull request, it means that there is some problem you will need to solve as soon as possible. The problems of your pull requests are listed at the bottom of the pull request page on github. The 3 possible kinds of problem are described next.

**Note that there is no need to close and reopen the pull request, you can just fix the problems and push to your repository.**

## `Some checks were not successful`: `Checks / file_ownership`

You are changing files outside of the java package of your team. You can see the files that you are changing in the `Files changed` tab of your pull request on the github website. This list must contain only files in your package.

1. Replace these files in VSCode with the version from the main repository: Fetch from all remotes, then you can find the version of a file from the main repository in the Source Control Graph view in VSCode (tagged as with the main repository branch, usually named `upstream`)
2. Commit and push

## `Some checks were not successful`: `Checks / java_tests`

Not accepting because the code does not pass the tests. You can read the error message by clicking on `Details` near to the failed check. Then click on `Build with Ant` to see the output of the test execution. Fix the error, then commit and push.

## `Some checks were not successful`: `Checks / performance_tests`

Not accepting because your update makes the simulation too slow. You can read the results of the performance tests by clicking on `Details` near to the failed check. You will see how much time (in milliseconds) each team takes when the button `Next` is clicked 300 times. The maximum time allowed per team is 20 seconds. Fix the problem, then commit and push.

## `This branch is out-of-date with the base branch`

Your team repository is not up-to-date with the changes that happened on the main repository. 

1. If you have not done it yet, add the main repository as a new remote. To do this, in the Source Control view in VSCode select `Remote` -> `Add Remote...`, then paste the URL of the main repository `https://github.com/CACAO2025/CACAO2025.git` and then give it a name (the typical name is `upstream`).
2. Pull from the main repository. To do this, in the Source Control view in VSCode select `Pull, Push` -> `Pull from...` then select the main repository (usually named `upstream`).
3. If there are conflicts, resolve all of them as usual in the VSCode editor.
4. Push.
