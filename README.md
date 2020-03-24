# Locifier
Locifier App

Steps complete a ticket:

1. Make a new branch:
`git checkout -b [branchname]`
2. Update local master:
`git checkout master
git pull`
3. Rebase your new branch:
`git checkout [branchname]
git rebase master`
4. Do your amazing work (**make sure you are on your own branch**)
5. Check for changes made before comitting your work:
`git checkout master
git pull`
6. Get updated:
(if there were no changes in last step skip this step)
`git checkout [branchname]
git rebase master`
7. Commit your changes:
if everything went well you can now commit
`git status` to see your changed files
`git add [file]` for each file that has changed
(**if the project uses local variables like passwords/usernames do not add these,
remove the hardcode/local variables in the code to make the file unchanged again.**)
once everything is added you can commit
`git commit -m "your message"`" 
(When writing a message think "This commit..." in your head and continue the message)
For example a good commit message would be "Changes the color of main panel".
Now push to remote
`git push origin [branchname]`
Once everything has run smoothly you can go to github and create a new Pull Request (a.k.a. PR)

Last tip:
`git status` can be run throughout the process to see what has changed and where you are standing.
Always make sure you know what branch you are on and **never** work on the master branch but **always** make a new branch!

	



